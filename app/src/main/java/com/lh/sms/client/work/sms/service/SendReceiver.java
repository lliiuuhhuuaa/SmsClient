package com.lh.sms.client.work.sms.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.work.log.service.LogService;
import com.lh.sms.client.work.socket.entity.SendSmsBySocket;
import com.lh.sms.client.work.socket.entity.SocketMessage;
import com.lh.sms.client.work.socket.enums.MsgHandleTypeEnum;
import com.lh.sms.client.work.socket.enums.SmsSocketCodeEnum;
import com.lh.sms.client.work.socket.service.SocketService;
import com.lh.sms.client.work.socket.util.SmsUtil;
import com.lh.sms.client.work.user.entity.UserInfo;

public class SendReceiver extends BroadcastReceiver {
    public static final String SMS_SEND = "sms_send";
    public static final String SMS_DELIVERED = "sms_delivered";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SMS_SEND.equals(action)) {
            handleResult(intent,false);
        }else if(SMS_DELIVERED.equals(action)){
            handleResult(intent,true);
        }
    }

    private void handleResult(Intent intent,boolean receive) {
        SendSmsBySocket sendSmsBySocket = JSONObject.parseObject(intent.getStringExtra("sendSmsBySocket"),SendSmsBySocket.class);
        String phone = sendSmsBySocket.getPhone().replaceAll("^([0-9]{3}).*([0-9]{4})$","$1****$2");
        String text = sendSmsBySocket.getText().replaceAll("^(.{2}).*(.{4})$","$1****$2");
        int resultCode = getResultCode();
        if (resultCode == Activity.RESULT_OK) {
            SocketMessage socketMessage = new SocketMessage();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("iccId",sendSmsBySocket.getIccId());
            socketMessage.setBody(jsonObject);
            socketMessage.setCode(SmsSocketCodeEnum.SEND.getValue());
            // 发送成功
            ObjectFactory.get(SocketService.class).sendMessage(MsgHandleTypeEnum.SMS.getValue(),socketMessage,null);
            //记录日志
            ObjectFactory.get(LogService.class).success("%s:手机号[%s]->内容[%s]",receive?"接收成功":"发送完成",phone,text);
            ObjectFactory.get(SocketService.class).reply(MsgHandleTypeEnum.SMS.getValue(), SmsUtil.okBody(sendSmsBySocket,receive),null);
        } else {
            ObjectFactory.get(LogService.class).error("%s:手机号[%s]->内容[%s]",receive?"接收失败":"发送失败",phone,text);
            ObjectFactory.get(SocketService.class).reply(MsgHandleTypeEnum.SMS.getValue(), SmsUtil.errorBody(sendSmsBySocket,receive,"监听发送失败"),null);
        }
    }


}