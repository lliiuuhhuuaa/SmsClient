package com.lh.sms.client.work.user.entity;

import lombok.Data;

@Data
public class UserInfo {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String nickname;
    /**
     * 角色
     */
    private String role;
    /**
     * 创建时间
     */
    private Long createDate;
    /**
     * 更新时间
     */
    private Long updateDate;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 客户端密钥
     */
    private String smsClientKey;
}
