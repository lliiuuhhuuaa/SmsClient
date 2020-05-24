package com.lh.sms.client.work.config.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;

/**
 * @do 配置
 * @author liuhua
 * @date 2020/4/25 4:25 PM
 */
public class ConfigService {
    /**
     * @do 更新配置
     * @author liuhua
     * @date 2020/4/25 4:25 PM
     */
    public void updateConfig(Runnable runnable){
        HttpClientUtil.post(ApiConstant.CONFIG_LIST,
                new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).alertError(false).animation(false)) {
            @Override
            public void callback(HttpResult httpResult) {
                if(httpResult.getData()!=null){
                    JSONArray jsonArray = httpResult.getJSONArray();
                    SqlData sqlData = ObjectFactory.get(SqlData.class);
                    for(int i = 0; i<jsonArray.size();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        sqlData.saveObject(jsonObject.getString("configKey"),jsonObject.getString("configValue"));
                    }
                }
                if(runnable!=null){
                    ThreadPool.exec(runnable);
                }
            }
        });
    }
}
