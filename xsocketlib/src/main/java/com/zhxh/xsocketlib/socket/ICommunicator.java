package com.zhxh.xsocketlib.socket;

/**
 * Created by zhxh on 2018/8/3
 */
public interface ICommunicator {

    /**
     * 设置客户端发送数据的服务器地址。
     *
     * @param address   服务器的url。
     * @param port      正在侦听的服务器的开放端口。
     * @param Heartbeat 心跳保持与服务器链接。
     */
    void setEndpoint(String address, int port, String Heartbeat);

    /**
     * 连接终端
     */
    void connection();

    /**
     * 设置侦听器以接收状态更改和有关从/向服务器接收/发送数据的信息。
     *
     * @param listener 通知收听者
     */
    void setListener(OnDataReceivedListener listener);

    /**
     * 将数据发送到服务器。
     *
     * @param data 要发送的数据
     */
    void sendData(String data);

    /**
     * 断开与服务器的连接并释放已使用的资源。
     */
    void disconnect();

    /**
     * 连接状态
     *
     * @return true 连接中  false 断连
     */
    boolean isConnection();

    /**
     * 重新连接
     */
    void resume();

    /**
     * 暂停连接
     */
    void pause();
}
