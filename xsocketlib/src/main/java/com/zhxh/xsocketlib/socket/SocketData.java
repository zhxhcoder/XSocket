package com.zhxh.xsocketlib.socket;

/**
 * Created by zhxh on 2018/8/3
 */
public class SocketData {

    private int code;
    private String msg;
    /**
     * 股票代码
     **/
    private String stockCode;
    /**
     * 股票内码
     **/
    private String innerCode;
    /**
     * 最新价
     **/
    private String newPrice;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return "SocketData{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", innerCode='" + innerCode + '\'' +
                ", newPrice='" + newPrice + '\'' +
                '}';
    }
}
