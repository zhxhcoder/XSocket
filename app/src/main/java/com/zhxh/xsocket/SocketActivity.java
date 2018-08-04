package com.zhxh.xsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.zhxh.xsocketlib.socket.AsyncSocketService;
import com.zhxh.xsocketlib.socket.OnDataReceivedListener;
import com.zhxh.xsocketlib.socket.SocketData;
import com.zhxh.xsocketlib.socket.SocketParser;

public class SocketActivity extends AppCompatActivity {

    final String stockCode = "000001";
    final String stockMarket = "3";
    final String TAG = "zhxhDebug";

    TextView tvSocket;


    AsyncSocketService socket;
    OnDataReceivedListener socketListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        tvSocket = findViewById(R.id.tvSocket);

        //链接 socket
        connectionSocket();

        socketListener = new OnDataReceivedListener() {
            @Override
            public void onReceiveData(String data) {
                SocketData socketData = SocketParser.parseData(data);
                tvSocket.append(data);
                tvSocket.append("\n");
                tvSocket.append(socketData.getInnerCode());
                tvSocket.append("\n");
                tvSocket.append(socketData.getNewPrice());
                tvSocket.append("\n");
                tvSocket.append(socketData.getStockTime());
            }

            @Override
            public void onConnected() {
                Log.d(TAG, "onConnected ");
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "onDisconnected ");
            }
        };
    }

    /**
     * 链接 socket
     */
    private void connectionSocket() {

        boolean socketInit = socket == null || !socket.isConnection();
        if (socketInit) {
            socket = new AsyncSocketService();
            socket.setEndpoint(Consts.HOST_NAME, Consts.PORT, SocketParser.requestHeartbeatData(Consts.userToken, Consts.deviceID, Consts.msgType));
            socket.setListener(socketListener);
            socket.connection();
        }
    }

    /**
     * 开启 socket 刷新
     */
    private void socketSendData() {
        if (null == socket) {
            return;
        }
        socket.sendData(SocketParser.requestSubscriptionData(SocketParser.ZS_PAGE_TYPE, stockCode, stockMarket));
    }

    /**
     * 关闭 socket 刷新
     */
    private void socketCancelData() {
        if (null == socket) {
            return;
        }
        socket.sendData(SocketParser.requestUnsubscribeData(SocketParser.ZS_PAGE_TYPE, stockCode, stockMarket));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (socket != null) {
            socket.resume();
            socketSendData();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (socket != null) {
            socketCancelData();
            socket.disconnect();
            socket = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
