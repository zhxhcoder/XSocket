package com.zhxh.xsocketlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zhxh on 2018/6/30
 */
public class SocketServer {
    // 端口号
    private static final int PORT = 5252;
    // ServerSocket类
    private ServerSocket mServerSocket;
    private Socket mSocket;
    private BufferedReader in;

    // private PrintWriter out;
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new SocketServer();
    }

    public SocketServer() {
        super();
        // TODO Auto-generated constructor stub
        try {
            mServerSocket = new ServerSocket(PORT);
            System.out.println("------Server Satrted------");
            mSocket = mServerSocket.accept();
            in = new BufferedReader(new InputStreamReader(
                    mSocket.getInputStream()));
            // out = new PrintWriter(mSocket.getOutputStream(), true);
            String line = in.readLine();
            System.out.println("you sent msg is " + line);
            // out.close();
            in.close();
            mSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}