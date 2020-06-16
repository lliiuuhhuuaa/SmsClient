package com.lh.sms.client.framing.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.lh.sms.client.framing.ActivityManager;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.ui.dialog.SmAlertDialog;

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
        AlertUtil.toast(msg, Toast.LENGTH_LONG);
//        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_MSG.getValue());
//        Bundle data = message.getData();
//        message.obj = context;
//        data.putString("msg",msg);
//        data.putInt("type",SweetAlertDialog.ERROR_TYPE);
//        data.putString("title","错误提示");
//        ObjectFactory.get(HandleMsg.class).sendMessage(message);
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
        data.putString("title","系统提示");
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
    /**
     * @do 正确弹窗
     * @author liuhua
     * @date 2020/3/12 9:19 PM
     */
    public static void alertOther(SmAlertDialog smAlertDialog) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_SWEET.getValue());
        message.obj =  smAlertDialog;
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
    /**
     * @do 弹系统提示
     * @author liuhua
     * @date 2020/3/12 10:23 PM
     */
    public static void toast(String msg, int length) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_TOAST.getValue());
        Bundle data = message.getData();
        message.obj = ActivityManager.getInstance().getCurrentActivity();
        data.putString("msg",msg);
        data.putInt("length",length);
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
    /**
     * @do 弹系统提示
     * @author liuhua
     * @date 2020/3/12 10:23 PM
     */
    public static void toast(Context context,String msg, int length) {
        if(context==null){
            toast(msg,length);
            return;
        }
        Toast.makeText(context,msg,length).show();
    }
    /**
     * @do 进度条
     * @author liuhua
     * @date 2020/3/12 11:17 PM
     */
    public static SmAlertDialog alertProcess(String text) {
        String content = text!=null?text:"请稍候...";
        SmAlertDialog loadingDialog = new SmAlertDialog(ActivityManager.getInstance().getCurrentActivity(),true)
                .setContentText(content);
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.ALERT_SWEET.getValue());
        message.obj =  loadingDialog;
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
        return loadingDialog;
    }
    /**
     * @do 关闭弹窗
     * @author liuhua
     * @date 2020/4/25 5:36 PM
     */
    public static void close(SmAlertDialog loadingDialog) {
        Message message = Message.obtain(ObjectFactory.get(HandleMsg.class), HandleMsgTypeEnum.CLOSE_ALERT.getValue());
        message.obj =  loadingDialog;
        ObjectFactory.get(HandleMsg.class).sendMessage(message);
    }
}
