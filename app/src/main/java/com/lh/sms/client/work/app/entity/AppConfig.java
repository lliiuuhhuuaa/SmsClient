package com.lh.sms.client.work.app.entity;

import lombok.Data;

/**
 * @author lh
 * @do
 * @date 2019-09-17 19:41
 */
@Data
public class AppConfig {
    private Integer id;
    /**
     * appId
     */
    private String appId;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 密钥
     */
    private String securityKey;
    /**
     * 使用公共服务
     */
    private Integer usePublic;
    /**
     * 总发送数
     */
    private Integer privateCount;
    /**
     * 发送公共数
     */
    private Integer publicCount;
    /**
     * 更新时间
     */
    private Long updateDate;
    /**
     * 创建时间
     */
    private Long createDate;

}
