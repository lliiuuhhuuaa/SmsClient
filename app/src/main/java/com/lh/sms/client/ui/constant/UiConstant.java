package com.lh.sms.client.ui.constant;

public class UiConstant {
    /**
     * 验证码发送默认间隔时间
     */
    public static final int DEFAULT_TIME = 60;
    /**
     * 验证码时间临时存储key
     */
    public static final String SMS_SEND_TIME_KEY = "sms_send_time";
    /**
     * 验证码发送状态key
     */
    public static final String SMS_SEND_STATE_KEY = "sms_send_state";

    public static final String SMS_SEND_TEXT = "重新发送验证码(%d秒)";

    public static final String SMS_RE_SEND_TEXT = "点击重新发送验证码";
    /**
     * 快速点击限制时间
     */
    public static final String TIME_QUICK_TAP = "time_quick_tap";
    public static final String NO_READ_PHONE_STATE_PERMISSION = "无法获取本机电话卡信息,因为没有读取电话卡信息权限";
    public static final String NO_CHECK_PHONE_INFO = "没有检测到本机有效电话卡信息,请检查是否已插好电话卡";
}
