package com.lh.sms.client.work.app.entity;

import lombok.Data;

/**
 * @author lh
 * @do
 * @date 2019-09-17 19:41
 */
@Data
public class AppVersion {
    private Long id;
    /**
     * 版本号
     */
    private Integer version;
    /**
     * 版本名
     */
    private String versionName;
    /**
     * 版本说明
     */
    private String notice;
    /**
     * 是否强制更新
     */
    private Integer force;
    /**
     * app大小
     */
    private Long size;
    /**
     * 更新时间
     */
    private Long updateDate;
    /**
     * 创建时间
     */
    private Long createDate;
    /**
     * 文件md5
     */
    private String md5;

}
