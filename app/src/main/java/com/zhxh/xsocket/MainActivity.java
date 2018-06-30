package com.zhxh.xsocket;

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
    private static final String HOST = "222.17.106.37";
    private static final int PORT = 5252;
    private Socket socket;
    private PrintWriter out;
    // private BufferedReader in;
    private String getText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btnSend = findViewById(R.id.btnSend);
        //开启新线程访问网络，否则会报错
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    socket = new Socket(HOST, PORT);
                    // in = new BufferedReader(new InputStreamReader(
                    // socket.getInputStream()));
                    out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(),
                                    "utf-8")), true);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getText = editText.getText().toString();
                if (socket.isConnected()) {
                    if (!socket.isOutputShutdown()) {
                        out.println(getText);
                    }
                }
            }
        });
    }

}
