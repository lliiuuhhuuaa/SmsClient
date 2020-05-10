package com.lh.sms.client.work.sms.service;

import com.alibaba.fastjson.JSONArray;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.work.sms.entity.SmsProvide;
import com.lh.sms.client.work.sms.enums.SmStateEnum;

import java.util.List;

/**
 * @do sms
 * @author liuhua
 * @date 2020/5/10 12:26 PM
 */
public class SmsProvideService {
    /**
     * @do 刷新SM
     * @author liuhua
     * @date 2020/5/10 9:51 AM
     */
    public void refreshSm(Runnable runnable){
        HttpClientUtil.post(ApiConstant.PROVIDE_LIST,
                new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).alertError(false).animation(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        SqlData sqlData = ObjectFactory.get(SqlData.class);
                        //删除本地已注册
                        List<SmsProvide> list = sqlData.listObject(TablesEnum.SM_LIST.getTable(),SmsProvide.class);
                        if(list!=null&&!list.isEmpty()){
                            for (SmsProvide smsProvide : list) {
                                if(!SmStateEnum.APPLYING.getValue().equals(smsProvide.getRegisterState())){
                                    sqlData.deleteObject(TablesEnum.SM_LIST.getTable(),smsProvide.getIccId());
                                }
                            }
                        }
                        JSONArray jsonArray = httpResult.getJSONArray();
                        if(jsonArray!=null){
                            SmsProvide smsProvide = null;
                            for (int i = 0; i < jsonArray.size(); i++) {
                               smsProvide = jsonArray.getObject(i, SmsProvide.class);
                                smsProvide.setRegisterState(SmStateEnum.REGISTER.getValue());
                               sqlData.saveObject(TablesEnum.SM_LIST.getTable(),smsProvide.getIccId(),smsProvide);
                            }
                        }
                        if(runnable!=null){
                            //回调
                            ThreadPool.createNewThread(runnable);
                        }
                    }
                });
    }
    /**
     * @do 获取SM
     * @author liuhua
     * @date 2020/5/10 9:54 AM
     */
    public SmsProvide getSmsProvide(String iccId){
        return ObjectFactory.get(SqlData.class).getObject(TablesEnum.SM_LIST.getTable(),iccId,SmsProvide.class);
    }
    /**
     * @do 保存注册状态
     * @author liuhua
     * @date 2020/5/10 12:33 PM
     */
    public void cacheSmsProvide(SmsProvide smsProvide) {
        SmsProvide updateSmsProvide = getSmsProvide(smsProvide.getIccId());
        if(updateSmsProvide==null){
            updateSmsProvide = new SmsProvide();
            updateSmsProvide.setCreateDate(System.currentTimeMillis());
            updateSmsProvide.setIccId(smsProvide.getIccId());
            updateSmsProvide.setMonthCount(0);
            updateSmsProvide.setMonthMax(0);
            updateSmsProvide.setTotalCount(0);
            updateSmsProvide.setState(YesNoEnum.NO.getValue());
        }
        if(smsProvide.getState()!=null){
            updateSmsProvide.setState(smsProvide.getState());
        }
        if(smsProvide.getRegisterState()!=null) {
            updateSmsProvide.setRegisterState(smsProvide.getRegisterState());
        }
        if(smsProvide.getMonthMax()!=null) {
            updateSmsProvide.setMonthMax(smsProvide.getMonthMax());
        }
        updateSmsProvide.setUpdateDate(System.currentTimeMillis());
        ObjectFactory.get(SqlData.class).saveObject(TablesEnum.SM_LIST.getTable(),smsProvide.getIccId(),updateSmsProvide);
    }
}
