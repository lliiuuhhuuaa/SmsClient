package com.lh.sms.client.framing.handle;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.ui.dialog.SmAlertDialog;

import java.lang.reflect.Method;

public class HandleMsg extends Handler {
    private final static String TAG = "HandleMsg";
    public static final String METHOD_KEY = "method";
    public HandleMsg() {
        super();
    }

    @Override
    public void handleMessage(final Message message) {
        super.handleMessage(message);
        //回调指定类方法
        if(message.what == HandleMsgTypeEnum.CALL_BACK.getValue()){
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
            return;
        }
        //弹消息提示框
        if(message.what==HandleMsgTypeEnum.ALERT_MSG.getValue()){
            Bundle data = message.getData();
            new SmAlertDialog((Context) message.obj)
                    .setTitleText(data.getString("title"))
                    .setContentText(data.getString("msg"))
                    .setConfirmText("我知道了")
                    .show();
            return;
        }
        //弹自定义提示框
        if(message.what==HandleMsgTypeEnum.ALERT_SWEET.getValue()){
            ((SmAlertDialog)message.obj).show();
            return;
        }
        //关闭弹框
        if(message.what==HandleMsgTypeEnum.CLOSE_ALERT.getValue()){
            try {
                ((SmAlertDialog) message.obj).cancel();
            }catch (Exception e){
                Log.d(TAG, "handleMessage: "+e.getMessage());
            }
            return;
        }
        //弹系统提示
        if(message.what==HandleMsgTypeEnum.ALERT_TOAST.getValue()){
            Bundle data = message.getData();
            Toast.makeText((Context) message.obj,data.getString("msg"),data.getInt("length")).show();
            return;
        }
    }
}
