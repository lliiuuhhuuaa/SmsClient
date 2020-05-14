package com.lh.sms.client.work.socket.entity;

import lombok.Data;

/**
 * @author lh
 * @do
 * @date 2020-04-02 15:42
 */
@Data
public class SendSmsBySocket {
    /**
     * 订单号
     */
    private String orderId;
    /**
     * appId
     */
    private String appId;
    /**
     * iccId
     */
    private String iccId;
    /**
     * 发送号码
     */
    private String phone;
    /**
     * 内容
     */
    private String text;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 签名
     */
    private String sign;
}
