package com.lh.sms.client.work.template.service;

import com.alibaba.fastjson.JSONArray;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.work.template.entity.SmsTemplate;

public class TemplateService {
    /**
     * @do 刷新应用列表
     * @author liuhua
     * @date 2020/5/13 9:15 PM
     */
    public void refreshTemplate(Runnable runnable) {
        HttpClientUtil.post(ApiConstant.TEMPLATE_CONFIG_LIST,
                new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).alertError(false).animation(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        SqlData sqlData = ObjectFactory.get(SqlData.class);
                        //删除本地已注册
                        sqlData.deleteAll(TablesEnum.TEMPLATE_LIST.getTable());
                        JSONArray jsonArray = httpResult.getJSONArray();
                        if(jsonArray!=null){
                            SmsTemplate smsTemplate = null;
                            for (int i = 0; i < jsonArray.size(); i++) {
                                smsTemplate = jsonArray.getObject(i, SmsTemplate.class);
                                sqlData.saveObject(TablesEnum.TEMPLATE_LIST.getTable(),smsTemplate.getCode(),smsTemplate);
                            }
                        }
                        if(runnable!=null){
                            //回调
                            ThreadPool.createNewThread(runnable);
                        }
                    }
                });
    }
}
