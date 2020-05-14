package com.lh.sms.client.work.sms.entity;

import lombok.Data;

/**
 * @author lh
 * @do
 * @date 2019-09-17 19:41
 */
@Data
public class SmsProvide {
    /**
     * 状态
     */
    private Integer state;
    /**
     * iccId
     */
    private String iccId;
    /**
     * 总发送条数
     */
    private Integer totalCount;
    /**
     * 月发送条数
     */
    private Integer monthCount;
    /**
     * 月上限
     */
    private Integer monthMax;
    /**
     * 个人服务
     */
    private Integer servicePrivate;
    /**
     * 公共服务
     */
    private Integer servicePublic;
    /**
     * 更新时间
     */
    private Long updateDate;
    /**
     * 创建时间
     */
    private Long createDate;
    /**
     * 注册状态
     */
    private Integer registerState;

}
