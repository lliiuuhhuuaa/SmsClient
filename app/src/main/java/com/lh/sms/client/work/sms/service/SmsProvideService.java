package com.lh.sms.client.work.sms.service;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import com.alibaba.fastjson.JSONArray;
import com.lh.sms.client.MainActivity;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.constant.SystemConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.exceptions.MsgException;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.work.sms.constant.SmsConstant;
import com.lh.sms.client.work.sms.entity.SmsProvide;
import com.lh.sms.client.work.sms.enums.SmStateEnum;
import com.lh.sms.client.work.socket.entity.SendSmsBySocket;

import java.util.List;

import androidx.core.app.ActivityCompat;

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
        if(smsProvide.getServicePrivate()!=null) {
            updateSmsProvide.setServicePrivate(smsProvide.getServicePrivate());
        }
        if(smsProvide.getServicePublic()!=null) {
            updateSmsProvide.setServicePublic(smsProvide.getServicePublic());
        }
        updateSmsProvide.setUpdateDate(System.currentTimeMillis());
        ObjectFactory.get(SqlData.class).saveObject(TablesEnum.SM_LIST.getTable(),smsProvide.getIccId(),updateSmsProvide);
    }
    /**
     * @do 发送消息
     * @author liuhua
     * @date 2020/5/12 12:41 PM
     */
    public boolean sendSms(SendSmsBySocket sendSmsBySocket) {
        //检查签名

        //注册消息通知
        //必须先注册广播接收器,否则接收不到发送结果
        Context context = ObjectFactory.get(MainActivity.class);
        SendReceiver receiver = new SendReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SendReceiver.ACTION);
        context.registerReceiver(receiver, filter);
        SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            throw new MsgException(SmsConstant.NO_PERMISSION_SEND_SMS);
        }
        int subscriptionId = -1;
        List<SubscriptionInfo> mList = sManager.getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : mList) {
            if(sendSmsBySocket.getIccId().equals(subscriptionInfo.getIccId())){
                subscriptionId = subscriptionInfo.getSubscriptionId();
            }
        }
        if(subscriptionId<0){
            throw new MsgException(SmsConstant.NO_GET_SM_INFO);
        }
        SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
        Intent intent = new Intent();
        intent.putExtra("phone",sendSmsBySocket.getPhone());
        intent.putExtra("text",sendSmsBySocket.getText());
        intent.putExtra("iccId",sendSmsBySocket.getIccId());
        intent.putExtra("orderId",sendSmsBySocket.getOrderId());
        intent.putExtra("appId",sendSmsBySocket.getAppId());
        intent.setAction(SendReceiver.ACTION);
        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            smsManager.sendTextMessage(sendSmsBySocket.getPhone(),null,sendSmsBySocket.getText(),sentIntent,sentIntent);
            return true;
        } catch (Exception e) {
            throw new MsgException(e.getMessage());
        }
    }
}
