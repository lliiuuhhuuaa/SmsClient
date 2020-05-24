package com.lh.sms.client.work.socket.entity;

import lombok.Data;

/**
 * @author lh
 * @do
 * @date 2020-04-02 15:42
 */
@Data
public class SendSmsByReply {
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 消息
     */
    private String msg;
    /**
     * 是否已接收
     */
    private Integer receive;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 签名
     */
    private String sign;
}
