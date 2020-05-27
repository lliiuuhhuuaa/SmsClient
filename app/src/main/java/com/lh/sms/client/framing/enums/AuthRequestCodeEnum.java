package com.lh.sms.client.framing.enums;

public enum AuthRequestCodeEnum {
    INSTALL_APK(10), // 成功
    ;
    private Integer value;

    AuthRequestCodeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
