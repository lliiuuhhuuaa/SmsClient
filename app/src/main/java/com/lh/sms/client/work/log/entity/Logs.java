package com.lh.sms.client.work.log.entity;

import lombok.Data;

@Data
public class Logs {
    /**
     * id
     */
    private Long id;
    /**
     * 时间
     */
    private Long time;
    /**
     * 文本
     */
    private String text;
    /**
     * 级别
     */
    private Integer level;
}
