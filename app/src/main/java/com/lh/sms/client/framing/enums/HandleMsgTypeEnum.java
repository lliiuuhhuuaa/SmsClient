package com.lh.sms.client.framing.enums;

/***
 * 倒计时状态
 */
public enum HandleMsgTypeEnum {
    //回调
    CALL_BACK(0),
    //弹Sweet消息
    ALERT_MSG(2),
    //弹系统提示
    ALERT_TOAST(4),
    //弹Sweet对象
    ALERT_SWEET(6),
    //关闭弹窗
    CLOSE_ALERT(8),
    ;
    private Integer value;
    HandleMsgTypeEnum(Integer value){
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
