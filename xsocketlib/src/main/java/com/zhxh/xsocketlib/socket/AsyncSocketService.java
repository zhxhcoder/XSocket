package com.zhxh.xsocketlib.socket;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * Created by zhxh on 2018/8/3
 */
public class AsyncSocketService implements ICommunicator, CompletedCallback {

    private static final String TAG = AsyncSocketService.class.getSimpleName();

    /**
     * 接收到的所有socket数据,需要循环按条取出，防止粘包
     */
    private static StringBuffer socketAllInfo = new StringBuffer("");

    private AsyncSocket socket;
    private OnDataReceivedListener listener;
    private HeartbeatR heartbeatRunner;

    private AsyncServer client;

    private String address;
    private int port;
    private String heartbeat;
    private boolean isHeartbeat = false;
    private boolean isConnection = false;
    private Handler mHandler = new SocketHandler(this);

    @Override
    public void setEndpoint(final String address, final int port, final String heartbeat) {
        Log.i(TAG, "Connecting to " + address + ":" + port);

        this.address = address;
        this.port = port;
        this.heartbeat = heartbeat;
        this.client = AsyncServer.getDefault();
    }

    @Override
    public void connection() {

        if (client == null || isConnection)
            return;

        //链接成功回调
        client.connectSocket(address, port, new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                if (ex != null) {
                    reconnect();
                    return;
                }
                isConnection = true;

                AsyncSocketService.this.socket = socket;

                //心跳
                if (null != heartbeatRunner) {
                    heartbeatRunner.update(socket, heartbeat);
                } else {
                    heartbeatRunner = new HeartbeatR(socket, heartbeat);
                }

                socket.setDataCallback(new DataCallback() {
                    @Override
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                        String data = new String(bb.getAllByteArray());

                        if (TextUtils.isEmpty(data))
                            return;

                        socketAllInfo.append(data);

                        if (socketAllInfo.indexOf("\n") >= 0) {

                            data = socketAllInfo.toString();

                            String[] arr = data.split("\n");

                            for (String value : arr) {
                                mHandler.obtainMessage(1, value).sendToTarget();
                                socketAllInfo.delete(socketAllInfo.indexOf(value), value.length() + 1);
                            }
                        }
                    }
                });

                socket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        Log.i(TAG, "Successfully closed connection");
                        isConnection = false;
                        if (listener != null && ex == null) {
                            listener.onDisconnected();
                        }
                    }
                });

                socket.setEndCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        Log.i(TAG, "Successfully end connection");
                        isConnection = false;
                        if (ex != null) {
                            reconnect();
                        }
                    }
                });

                Log.i(TAG, "Connected to " + address + ":" + port);

                if (listener != null) {
                    listener.onConnected();
                }
            }
        });
    }

    private class HeartbeatR implements Runnable {
        AsyncSocket socket;
        String heartbeat;
        private volatile boolean isStopHeartbeat = false;

        public HeartbeatR(AsyncSocket socket, String heartbeat) {
            this.socket = socket;
            this.heartbeat = heartbeat;
        }

        public void update(AsyncSocket socket, String heartbeat) {
            this.socket = socket;
            this.heartbeat = heartbeat;
        }

        public void run() {
            if (null != socket) {
                byte[] bytes = heartbeat.getBytes();
                ByteBuffer bb = ByteBufferList.obtain(bytes.length);
                bb.put(bytes);
                bb.flip();
                ByteBufferList bbl = new ByteBufferList();
                bbl.add(bb);
                if (!isStopHeartbeat) {
                    socket.write(bbl);
                    socket.getServer().postDelayed(this, 5000);//AsyncSocket 内部会进行轮询操作

                }
            }
        }

        public void stop(boolean isTrue) {
            isStopHeartbeat = isTrue;
        }
    }

    @Override
    public void setListener(OnDataReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void sendData(String data) {
        if (socket != null) {
            Util.writeAll(socket, data.getBytes(), this);
            socketAllInfo = new StringBuffer("");
        }
    }

    /**
     * @param ex
     */
    @Override
    public void onCompleted(Exception ex) {
        Log.d(TAG, "onCompleted");

        if (listener != null) {
            if (ex == null) {
                Log.d(TAG, "Successfully wrote message");
                if (!isHeartbeat) {
                    isHeartbeat = true;
                    heartbeatRunner.stop(false);
                    heartbeatRunner.run();//启动心跳
                }
            } else {
                Log.d(TAG, "Message lost");
            }
        }
    }

    /**
     * 重新连接
     */
    private void reconnect() {
        isHeartbeat = false;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(0, 5000);
    }


    private static class SocketHandler extends Handler {
        private WeakReference<AsyncSocketService> socketServiceWeakReference;

        SocketHandler(AsyncSocketService socketService) {
            socketServiceWeakReference = new WeakReference<>(socketService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AsyncSocketService asyncSocketService = socketServiceWeakReference.get();
            if (asyncSocketService != null) {
                if (msg.what == 0) {
                    asyncSocketService.mHandler.removeMessages(0);
                    asyncSocketService.connection();
                } else if (msg.what == 1) {
                    if (asyncSocketService.listener != null)
                        asyncSocketService.listener.onReceiveData((String) msg.obj);
                }
            }
        }
    }

    @Override
    public void resume() {
        if (socket != null && socket.isPaused())
            socket.resume();
    }

    @Override
    public void pause() {
        if (socket != null && socket.isOpen())
            socket.pause();
    }

    @Override
    public synchronized void disconnect() {
        Log.i(TAG, "Disconnect");
        mHandler.removeCallbacksAndMessages(null);
        if (socket != null) {
            socket.end();
            socket.close();
            socket = null;
            heartbeatRunner.stop(true);
        }
        if (null != listener) {
            listener = null;
        }

        if (null != client) {
            client.stop();
        }

    }

    @Override
    public boolean isConnection() {
        return isConnection;
    }
}
