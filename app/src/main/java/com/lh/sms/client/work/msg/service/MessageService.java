package com.lh.sms.client.work.msg.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.work.msg.entity.Message;

import okhttp3.FormBody;

public class MessageService {
    /**
     * @do 刷新SM
     * @author liuhua
     * @date 2020/5/10 9:51 AM
     */
    public void refreshMsg(String state,Integer page,Runnable runnable){
        FormBody.Builder param = new FormBody.Builder().add("page",page.toString()).add("state",state).add("rows",String.valueOf(20));
        HttpClientUtil.post(ApiConstant.MSG_LIST,param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).alertError(false).animation(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        SqlData sqlData = ObjectFactory.get(SqlData.class);
                        //删除本地已注册
                        JSONObject jsonObject = httpResult.getJSONObject();
                        if (jsonObject != null) {
                            Message message = null;
                            JSONArray rows = jsonObject.getJSONArray("rows");
                            for (int i = 0; i < rows.size(); i++) {
                                message = rows.getObject(i, Message.class);
                                sqlData.saveObject(TablesEnum.MSG_LIST.getTable(), message.getId().toString(), message);
                            }
                        }
                        if(runnable!=null){
                            //回调
                            runnable.run();
                        }
                    }
                });
    }
    /**
     * @do 获取SM
     * @author liuhua
     * @date 2020/5/10 9:54 AM
     */
    public Message getMsg(Long id){
        return ObjectFactory.get(SqlData.class).getObject(TablesEnum.MSG_LIST.getTable(),id.toString(),Message.class);
    }
    /**
     * @do 保存注册状态
     * @author liuhua
     * @date 2020/5/10 12:33 PM
     */
    public void cacheMsg(Long id,Integer state) {
        Message updateMessage = getMsg(id);
        if(updateMessage==null){
            return;
        }
        updateMessage.setState(state);
        updateMessage.setUpdateDate(System.currentTimeMillis());
        ObjectFactory.get(SqlData.class).saveObject(TablesEnum.SM_LIST.getTable(),id.toString(),updateMessage);
    }
}
