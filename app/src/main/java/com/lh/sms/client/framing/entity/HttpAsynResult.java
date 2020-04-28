package com.lh.sms.client.framing.entity;

import android.content.Context;

/**
 * @do 异步返回结果
 * @author liuhua
 * @date 2020/4/25 2:35 PM
 */
public abstract class HttpAsynResult{
    //配置
    private Config config;
    public Config getConfig() {
        return config;
    }
    public HttpAsynResult(Context context){
        this.config = new Config();
        this.config.context = context;
    }
    public HttpAsynResult(Config config){
        this.config = config;
    }
    /**
     * @do 回调
     * @author liuhua
     * @date 2020/4/25 2:35 PM
     */
    public void callback(HttpResult httpResult) {

    }
    /**
     * @do 配置类
     * @author liuhua
     * @date 2020/4/25 5:43 PM
     */
    public static class Config{
        //只返回成功
        private boolean onlyOk = false;
        //是否弹错
        private boolean alertError = true;
        //是否需要登陆
        private boolean login = true;
        //当前activity
        private Context context;
        //是否显示动画
        private boolean animation = true;

        public Context getContext() {
            return context;
        }

        public boolean isAnimation() {
            return animation;
        }

        public boolean isAlertError() {
            return alertError;
        }

        public boolean isLogin() {
            return login;
        }

        public boolean isOnlyOk() {
            return onlyOk;
        }
        public static Config builder(){
            return new Config();
        }

        public Config onlyOk(boolean onlyOk) {
            this.onlyOk = onlyOk;
            return this;
        }
        public Config login(boolean login) {
            this.login = login;
            return this;
        }
        public Config alertError(boolean alertError) {
            this.alertError = alertError;
            return this;
        }

        public Config context(Context context) {
            this.context = context;
            return this;
        }

        public Config animation(boolean animation) {
            this.animation = animation;
            return this;
        }
    }
}
