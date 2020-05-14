package com.lh.sms.client.work.socket.util;

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
}
