package com.zhxh.xsocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnSend;
    private Button btnSocket;
    private static final String HOST = "222.17.106.37";
    private static final int PORT = 5252;
    private Socket socket;
    private PrintWriter out;
    //private BufferedReader in;
    private String getText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btnSend = findViewById(R.id.btnSend);
        btnSocket = findViewById(R.id.btnSocket);
        //开启新线程访问网络，否则会报错
        //new Thread(() -> {
        //    // TODO Auto-generated method stub
        //    try {
        //        socket = new Socket(HOST, PORT);
        //        // in = new BufferedReader(new InputStreamReader(
        //        // socket.getInputStream()));
        //        out = new PrintWriter(new BufferedWriter(
        //                new OutputStreamWriter(socket.getOutputStream(),
        //                        "utf-8")), true);
        //    } catch (IOException e) {
        //        // TODO Auto-generated catch block
        //        e.printStackTrace();
        //    }
        //}).start();

        btnSend.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            getText = editText.getText().toString();
            if (socket.isConnected()) {
                if (!socket.isOutputShutdown()) {
                    out.println(getText);
                }
            }
        });


        btnSocket.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SocketActivity.class));
            }
        });
    }

}
