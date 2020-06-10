package com.lh.sms.client.work.storage.entity;

import lombok.Data;

/**
 * @do 预上传信息
 * @author liuhua
 * @date 2020-01-09 10:30
 */
@Data
public class UpLoadInfo {
    /**
     * 预上传token
     */
    private String token;
    /**
     * 预上传文件名
     */
    private String name;
    /**
     * 上传域名
     */
    private String domain;
    /**
     * 类型
     */
    private String type;
    /**
     * 文件类型
     */
    private String contentType;
}
