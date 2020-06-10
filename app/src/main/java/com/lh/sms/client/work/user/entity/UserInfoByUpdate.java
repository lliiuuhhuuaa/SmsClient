package com.lh.sms.client.work.user.entity;

import lombok.Data;

@Data
public class UserInfoByUpdate {
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String photo;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 验证码
     */
    private String oldCode;
    /**
     * 新验证码
     */
    private String newCode;
}
