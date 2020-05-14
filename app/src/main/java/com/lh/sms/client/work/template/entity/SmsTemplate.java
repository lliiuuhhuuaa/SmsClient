package com.lh.sms.client.work.template.entity;

import lombok.Data;

/**
 * @author lh
 * @do
 * @date 2019-09-17 19:41
 */
@Data
public class SmsTemplate {
    private Integer id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 审核状态
     */
    private Integer authState;
    /**
     * 模板code
     */
    private String code;
    /**
     * 内容
     */
    private String text;
    /**
     * 名称
     */
    private String name;
    /**
     * 审核结果
     */
    private String authResult;
    /**
     * 创建时间
     */
    private Long createDate;
    /**
     * 更新时间
     */
    private Long updateDate;

}
