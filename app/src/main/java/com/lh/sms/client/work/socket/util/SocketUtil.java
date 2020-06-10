package com.lh.sms.client.work.socket.util;

import org.joda.time.LocalDate;

/**
 * @author: lh
 * @description: socket工具
 * @date: 2019-04-14 13:46
 **/
public class SocketUtil {
    /**
     * @do 获取消息监听
     * @author liuhua
     * @date 2020-03-14 22:40
     */
    public static String getSocketMessageHandle(String game){
        return String.format("game_message_%s",game);
    }
    /**
     * @do 今日请求发送次数
     * @author liuhua
     * @date 2020/6/6 5:38 PM
     */
    public static String getTodayCountKey() {
        return "send_count_today_"+LocalDate.now().toString("yyyyMMdd");
    }
}
