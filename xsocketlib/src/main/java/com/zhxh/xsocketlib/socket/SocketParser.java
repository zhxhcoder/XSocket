package com.zhxh.xsocketlib.socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by zhxh on 2018/8/3
 */
public class SocketParser {
    static String token;
    static String deviceID;

    /**
     * 接收到的所有socket数据,需要循环按条取出，防止粘包
     */
    public static StringBuffer socketAllInfo = new StringBuffer("");

    public static final int ZS_PAGE_TYPE = 103;

    /**
     * 请求协议 心跳
     *
     * @return 协议
     */
    public static String requestHeartbeatData(String token, String deviceID, String msgType) {

        SocketParser.token = token;
        SocketParser.deviceID = deviceID;
        JSONObject object = new JSONObject();
        try {
            object.put("niuguToken", token);
            object.put("deviceID", deviceID);
            object.put("msgType", msgType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getProtocol(object.toString());
    }

    /**
     * 请求协议 订阅
     *
     * @param responseType 不传则默认1表示量化所需常用数据，会推送responseType=1的数据。
     *                     101表示个股分时页面，102表示买卖页面，103表示指数页，保留到199。会推送responseType=pageID的数据。
     * @param stockCode    股票代码
     * @param stockMarket  股票市场
     * @return 协议
     */
    public static String requestSubscriptionData(int responseType, String stockCode, String stockMarket) {
        ArrayList<SocketData> stockList = new ArrayList<>();
        SocketData data = new SocketData();
        data.setStockCode(stockCode);
        stockList.add(data);
        return subscriptionData(responseType, stockList);
    }

    public static String requestSubscriptionData(int responseType, ArrayList<SocketData> stockList) {
        return subscriptionData(responseType, stockList);
    }

    private static String subscriptionData(int responseType, ArrayList<SocketData> stockList) {
        JSONObject object = new JSONObject();
        try {
            object.put("niuguToken", token);
            object.put("deviceID", toUTF8Str(deviceID));
            object.put("packettype", "1");
            object.put("msgType", "1");
            object.put("pageID", String.valueOf(responseType));
            object.put("symbols", getSymbols(stockList));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getProtocol(object.toString());
    }

    /**
     * 请求协议 取消订阅（如果不取消订阅，那么按照先进先出的顺序，保留用户对最新200个股票的订阅）
     *
     * @param responseType 取消类型
     * @param stockCode    股票列表
     * @param stockMarket  股票市场
     * @return 协议
     */
    public static String requestUnsubscribeData(int responseType, String stockCode, String stockMarket) {
        ArrayList<SocketData> stockList = new ArrayList<>();
        SocketData data = new SocketData();
        data.setStockCode(stockCode);
        data.setStockMarket(stockMarket);
        stockList.add(data);
        return unsubscribeData(responseType, stockList);
    }

    public static String requestUnsubscribeData(int responseType, ArrayList<SocketData> stockList) {
        return unsubscribeData(responseType, stockList);
    }

    private static String unsubscribeData(int responseType, ArrayList<SocketData> stockList) {
        JSONObject object = new JSONObject();
        try {
            object.put("niuguToken", token);
            object.put("deviceID", toUTF8Str(deviceID));
            object.put("packettype", "1");
            object.put("msgType", "2");
            object.put("pageID", String.valueOf(responseType));
            object.put("symbols", getSymbols(stockList));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getProtocol(object.toString());
    }

    /**
     * @param list 股票列表
     * @return 股票列表，逗号分隔。600570.SH（股票代码和市场需要中间加"."）表示上海市场的600570。目前支持四个市场：SH、SZ、HK、US
     */
    private static String getSymbols(ArrayList<SocketData> list) {

        if (null == list || list.size() == 0)
            return "";
        StringBuffer data = new StringBuffer();
        for (SocketData item : list) {
            data.append(item.getStockCode() + "." + getStockMarketType(item.getStockMarket()) + ",");
        }
        return data.substring(0, data.length() - 1);
    }

    private static String getStockMarketType(String marketID) {
        switch (marketID) {
            case "3":
                return "SH";

        }
        return marketID;
    }

    public static SocketData parseData(String data) {

        SocketData quoteBean = new SocketData();
        try {

            //String[] arr = data.split("\n");
            String[] bean = data.split("\0");
            int code = Integer.parseInt(bean[0]);
            switch (code) {
                case -1:
                case 0: // 心跳信息
                case 1: // 订阅
                case 2: //
                    quoteBean.setCode(code);
                    quoteBean.setMsg(bean[1]);
                    break;

                case ZS_PAGE_TYPE://指数
                    quoteBean.setCode(Integer.parseInt(bean[0]));
                    quoteBean.setStockCode(bean[1]);
                    quoteBean.setStockMarket(bean[2]);
                    quoteBean.setStockTime(bean[3]);
                    quoteBean.setNewPrice(bean[4]);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quoteBean;
    }

    /**
     * 其中HQSR是固定包头，00000131是八个字符的长度，描述后面的json数据的长度。
     *
     * @param json json数据
     * @return 协议
     */
    private static String getProtocol(String json) {
        return "HQSR" + padLeft(String.valueOf(json.length()), 8, '0') + json;
    }

    /**
     * 左补位，右对齐
     *
     * @param oriStr 原字符串
     * @param len    目标字符串长度
     * @param alexin 补位字符
     * @return 目标字符串
     */
    private static String padLeft(String oriStr, int len, char alexin) {
        int strlen = oriStr.length();
        String str = "";
        if (strlen < len) {
            for (int i = 0; i < len - strlen; i++) {
                str = str + alexin;
            }
        }
        str = str + oriStr;
        return str;
    }

    /***
     * 获取URLEncoder值
     * @param value
     * @return
     */
    public static String toUTF8Str(String value) {

        String tempValue = "";

        if (null == value)
            return tempValue;

        try {

            tempValue = URLEncoder.encode(value, "utf-8");

        } catch (Exception ex) {

            tempValue = "";
        }

        return tempValue;
    }

}
