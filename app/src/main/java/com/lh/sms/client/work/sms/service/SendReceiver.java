package com.lh.sms.client.work.sms.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.work.log.service.LogService;
import com.lh.sms.client.work.socket.entity.SocketMessage;
import com.lh.sms.client.work.socket.enums.MsgHandleTypeEnum;
import com.lh.sms.client.work.socket.enums.SmsSocketCodeEnum;
import com.lh.sms.client.work.socket.service.SocketService;

public class SendReceiver extends BroadcastReceiver {
    public static final String ACTION = "action.send.sms";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION.equals(action)) {
            int resultCode = getResultCode();
            if (resultCode == Activity.RESULT_OK) {
                SocketMessage socketMessage = new SocketMessage();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("iccId",intent.getStringExtra("iccId"));
                socketMessage.setBody(jsonObject);
                socketMessage.setCode(SmsSocketCodeEnum.SEND.getValue());
                // 发送成功
                ObjectFactory.get(SocketService.class).sendMessage(MsgHandleTypeEnum.SMS.getValue(),socketMessage,null);
                //记录日志
                ObjectFactory.get(LogService.class).success("发送成功:手机号[%s]->内容[%s]",
                        intent.getStringExtra("phone"),intent.getStringExtra("text"));
            } else {
                ObjectFactory.get(LogService.class).error("发送失败:手机号[%s]->内容[%s]",
                        intent.getStringExtra("phone"),intent.getStringExtra("text"));
            }
        }
        context.unregisterReceiver(this);
    }


}