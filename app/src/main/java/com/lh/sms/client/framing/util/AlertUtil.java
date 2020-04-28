package com.lh.sms.client.framing.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.lh.sms.client.MainActivity;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @do 弹窗工具
 * @author liuhua
 * @date 2020/3/12 8:18 PM
 */
public class AlertUtil {
    private final static String TAG = "AlertUtil";
    /**
     * @do 错误弹窗
     * @author liuhua
     * @date 2020/3/12 9:19 PM
     */
    public static void alertError(Context context,String msg) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_MSG.getValue());
        Bundle data = message.getData();
        message.obj = context;
        data.putString("msg",msg);
        data.putInt("type",SweetAlertDialog.ERROR_TYPE);
        data.putString("title","错误提示");
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
    /**
     * @do 正确弹窗
     * @author liuhua
     * @date 2020/3/12 9:19 PM
     */
    public static void alertOK(Context context,String msg) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_MSG.getValue());
        Bundle data = message.getData();
        message.obj = context;
        data.putString("msg",msg);
        data.putInt("type",SweetAlertDialog.SUCCESS_TYPE);
        data.putString("title","系统提示");
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
    /**
     * @do 正确弹窗
     * @author liuhua
     * @date 2020/3/12 9:19 PM
     */
    public static void alertOther(Context context,SweetAlertDialog sweetAlertDialog) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_SWEET.getValue());
        message.obj =  sweetAlertDialog;
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
    /**
     * @do 弹系统提示
     * @author liuhua
     * @date 2020/3/12 10:23 PM
     */
    public static void toast(Context context,String msg, int length) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_TOAST.getValue());
        Bundle data = message.getData();
        message.obj = context;
        data.putString("msg",msg);
        data.putInt("length",length);
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
    /**
     * @do 进度条
     * @author liuhua
     * @date 2020/3/12 11:17 PM
     */
    public static SweetAlertDialog alertProcess(Context context,String... text) {
        String title = "处理中";
        String content = "请稍候...";
        if(text!=null){
            if(text.length>0){
                title = text[0];
            }
            if(text.length>1){
                content = text[1];
            }
        }
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(title)
                .setContentText(content);
        sweetAlertDialog.setCancelable(false);
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_SWEET.getValue());
        message.obj =  sweetAlertDialog;
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
        return sweetAlertDialog;
    }
    /**
     * @do 关闭弹窗
     * @author liuhua
     * @date 2020/4/25 5:36 PM
     */
    public static void close(SweetAlertDialog sweetAlertDialog) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.CLOSE_ALERT.getValue());
        message.obj =  sweetAlertDialog;
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
}
