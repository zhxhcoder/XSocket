package com.zhxh.xsocketlib.socket;

/**
 * Created by zhxh on 2018/8/3
 */
public interface OnDataReceivedListener {


    /**
     * 从服务器接收到数据时调用。
     * @param data 接收到的数据
     */
    void onReceiveData(String data);

    /**
     * 当客户端已成功连接到服务器时调用。
     */
    void onConnected();

    /**
     * 当客户端与服务器断开连接时调用。
     */
    void onDisconnected();

}
