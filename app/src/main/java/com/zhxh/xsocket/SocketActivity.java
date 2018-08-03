package com.zhxh.xsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhxh.xsocketlib.socket.OnDataReceivedListener;

public class SocketActivity extends AppCompatActivity {
    OnDataReceivedListener socketListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);


        socketListener.onConnected();
    }
}
