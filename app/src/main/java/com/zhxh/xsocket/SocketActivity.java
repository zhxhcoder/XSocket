package com.zhxh.xsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhxh.xsocketlib.socket.AsyncSocketService;
import com.zhxh.xsocketlib.socket.OnDataReceivedListener;
import com.zhxh.xsocketlib.socket.SocketData;

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

            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
