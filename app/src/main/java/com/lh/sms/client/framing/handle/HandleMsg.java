package com.lh.sms.client.framing.handle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;

import java.lang.reflect.Method;

public class HandleMsg extends Handler {
    public String METHOD_KEY = "method";
    public HandleMsg() {
        super();
    }

    @Override
    public void handleMessage(final Message message) {
        super.handleMessage(message);
        if(message.what == HandleMsgTypeEnum.CALL_BACK.getValue()){
            //回调指定类方法
            Bundle data = message.getData();
            String methodName = data.getString("method");
            Object[] objects= (Object[]) message.obj;
            try {
                assert methodName != null;
                if(objects.length>1) {
                    Class[] clazzs = new Class[objects.length-1];
                    Object[] objs = new Object[objects.length-1];
                    for (int i = 1; i < objects.length; i++) {
                        clazzs[i-1] = objects[i].getClass();
                        objs[i-1] = objects[i];
                    }
                    Method method = objects[0].getClass().getMethod(methodName, clazzs);
                    method.invoke(objects[0],objs);
                }else{
                    Method method = objects[0].getClass().getMethod(methodName);
                    method.invoke(objects[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
