package com.lh.sms.client.work.app.service;

import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.work.app.entity.AppVersion;

public class AppVersionService {
    /**
     * @do 检查新版app
     * @author liuhua
     * @date 2020/5/13 9:15 PM
     */
    public void checkNewVersion(Runnable runnable) {
        HttpClientUtil.post(ApiConstant.CHECK_NEW_VERSION,
                new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).alertError(false).login(false).animation(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        AppVersion appVersion = httpResult.getObject(AppVersion.class);
                        SqlData sqlData = ObjectFactory.get(SqlData.class);
                        sqlData.saveObject(appVersion);
                        if(runnable!=null){
                            //回调
                            ThreadPool.exec(runnable);
                        }
                    }
                });
    }
}
