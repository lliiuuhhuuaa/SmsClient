package com.lh.sms.client.framing.constant;

/**
 * @do 接口常量
 * @author liuhua
 * @date 2020/4/25 2:57 PM
 */
public class ApiConstant {
    //获取用户信息
    public static final String USER_GET_INFO = "/web/user/getUserInfo";
    //修改登陆密码
    public static final String UPDATE_LOGIN_PASSWORD = "/web/user/updateLoginPassword";
    //获取用户钱包
    public static final String USER_WALLET = "/web/wallet/getUserWallet";
    //获取用户钱包明细
    public static final String USER_WALLET_DETAIL = "/web/wallet/listWalletDetail";
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
    //更新消息
    public static final String MSG_UPDATE = "/message/updateMessage";
    //未读消息数
    public static final String MSG_UN_READ_COUNT = "/message/getWaitMessageCount";
    //添加或更新应用配置
    public static final String APP_CONFIG_UPDATE = "/sms/app/config/update";
    //应用列表
    public static final String APP_CONFIG_LIST = "/sms/app/config/list";
    //添加或更新模板配置
    public static final String TEMPLATE_CONFIG_UPDATE = "/sms/template/update";
    //模板列表
    public static final String TEMPLATE_CONFIG_LIST = "/sms/template/list";
    //发送验证码
    public static final String SEND_SMS_CODE = "/open/sms/sendSmsCode";
    //检查验证码
    public static final String VERIFY_SMS_CODE = "/open/sms/verifySmsCode";
    //检查新版本
    public static final String CHECK_NEW_VERSION = "/open/app/version/getNewVersion";
    //下载版本
    public static final String DOWNLOAD_VERSION = "/open/app/version/download/{version}";
    //首页统计数据
    public static final String STAT_LIST = "/sms/provide/getStat";
    //上传预处理
    public static final String STORAGE_PRETREATMENT = "/storage/upload/pretreatment";
    //更新用户信息
    public static final String UPDATE_USER_INFO = "/web/user/updateUserInfo";
}
