package com.lh.sms.client.ui.person.user.enums;

/***
 * 短信验证状态
 */
public enum SmsTypeEnum {
    //回调
    REGISTER("register","/open/user/register"),
    //弹Sweet消息
    FIND("find","/open/user/resetPassword"),
    ;
    private String value;
    private String api;
    SmsTypeEnum(String value,String api){
        this.value = value;
        this.api = api;
    }

    public String getValue() {
        return value;
    }

    public String getApi() {
        return api;
    }
    /**
     * 获取元素
     *
     * @param value
     * @return
     */
    public static SmsTypeEnum getEnum(String value) {
        SmsTypeEnum[] values = SmsTypeEnum.values();
        for (SmsTypeEnum em : values) {
            if (em.getValue().equals(value)) {
                return em;
            }
        }
        return null;
    }
}
