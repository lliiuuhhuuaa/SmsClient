package com.lh.sms.client.framing.enums;

/***
 * 倒计时状态
 */
public enum HandleMsgTypeEnum {
    //回调
    CALL_BACK(0),
    ;
    private Integer value;
    HandleMsgTypeEnum(Integer value){
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
