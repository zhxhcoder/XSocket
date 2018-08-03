package com.zhxh.xsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhxh.xsocketlib.socket.AsyncSocketService;
import com.zhxh.xsocketlib.socket.OnDataReceivedListener;
import com.zhxh.xsocketlib.socket.SocketData;
import com.zhxh.xsocketlib.socket.SocketParser;

import java.util.ArrayList;

public class SocketActivity extends AppCompatActivity {

    AsyncSocketService socket;

    OnDataReceivedListener socketListener;

    ArrayList<SocketData> stockListSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);


        socketListener = new OnDataReceivedListener() {
            @Override
            public void onReceiveData(String data) {
                SocketData socketData = SocketParser.parseData(data);

            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }
        };
    }

    /**
     * 开启 socket 刷新
     */
    private void socketSendData() {
        if (null == socket) {
            return;
        }
        socket.sendData(SocketParser.requestSubscriptionData(SocketParser.ZS_PAGE_TYPE, stockListSocket));
    }

    /**
     * 关闭 socket 刷新
     */
    private void socketCancelData() {
        if (null == socket) {
            return;
        }
        socket.sendData(SocketParser.requestUnsubscribeData(SocketParser.ZS_PAGE_TYPE, stockListSocket));
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
    protected void onPause() {
        super.onPause();
        if (socket != null) {
            socketCancelData();
            socket.disconnect();
            socket = null;
        }
    }
}
