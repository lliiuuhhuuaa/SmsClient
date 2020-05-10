package com.lh.sms.client.framing.constant;

/**
 * @do 接口常量
 * @author liuhua
 * @date 2020/4/25 2:57 PM
 */
public class ApiConstant {
    //获取用户信息
    public static final String USER_GET_INFO = "/web/user/getUserInfo";
    //获取用户钱包
    public static final String USER_WALLET = "/web/wallet/getUserWallet";
    //登陆
    public static final String USER_LOGIN = "/user/login";
    //注册与更新SM卡
    public static final String PROVIDE_UPDATE = "/sms/provide/update";
    //SM卡列表
    public static final String PROVIDE_LIST = "/sms/provide/list";
    //配置列表
    public static final String CONFIG_LIST = "/open/config/listConfig";
    //消息列表
    public static final String MSG_LIST = "/message/listMessage";
}
