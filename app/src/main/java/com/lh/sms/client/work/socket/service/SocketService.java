package com.lh.sms.client.work.socket.service;

import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.SmRunningService;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ActivityManager;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.ApplicationUtil;
import com.lh.sms.client.work.log.service.LogService;
import com.lh.sms.client.work.sms.service.SmsProvideService;
import com.lh.sms.client.work.socket.entity.SendSmsBySocket;
import com.lh.sms.client.work.socket.entity.SocketMessage;
import com.lh.sms.client.work.socket.enums.MsgHandleTypeEnum;
import com.lh.sms.client.work.socket.util.SmsUtil;
import com.lh.sms.client.work.socket.util.SocketUtil;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SocketService {
    //socket连接
    private Socket socket;
    private Integer reConnCount = 0;
    /**
     * @do 开始连接socket
     * @author liuhua
     * @date 2020/3/14 11:05 PM
     */
    public void connect(){
        try{
            if(socket!=null){
                //先断开之前连接
                ObjectFactory.get(LogService.class).info("SM服务:准备重连...");
                socket.disconnect();
            }
            SqlData sqlData = ObjectFactory.get(SqlData.class);
            if(!YesNoEnum.YES.getValue().equals(sqlData.getObject(DataConstant.KEY_IS_LOGIN, Integer.class))){
                ObjectFactory.get(LogService.class).error("SM服务:未登陆账号,无法连接至SM服务");
                return;
            }
            String tk = sqlData.getObject(DataConstant.KEY_USER_TK,String.class);
            String iccId = sqlData.getObject(DataConstant.LOCAL_ICC_ID,String.class);
            if(StringUtils.isBlank(iccId)){
                ObjectFactory.get(LogService.class).error("SM服务:获取本机SM卡信息失败,无法连接至SM服务(请检查SIM卡是否正常、权限是否通过、是否使用空白通行证等)");
                return;
            }
            String domain = sqlData.getObject(DataConstant.KEY_SOCKET_DOMAIN, String.class);
            IO.Options opts = new IO.Options();
            //最大重连50次
            opts.reconnectionAttempts = 50;
            socket = IO.socket(String.format(Locale.CANADA,"%s?iccId=%s&tk=%s&type=sms&model=%s&v=%d",domain,iccId,tk, URLEncoder.encode(Build.MODEL, "UTF-8"), ApplicationUtil.getVersion(ActivityManager.getInstance().getCurrentActivity())),opts);
            //连接超时事件
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                ObjectFactory.get(LogService.class).error("SM服务:连接超时");
            });
            //连接成功事件
            socket.on(Socket.EVENT_CONNECT, args -> {
                ObjectFactory.get(LogService.class).success("SM服务:连接成功");
                reConnCount = 0;
            });
            socket.on(Socket.EVENT_ERROR,args -> {
                ObjectFactory.get(LogService.class).error("SM服务:连接错误");
            });
            socket.on(Socket.EVENT_RECONNECTING,args->{
                reConnCount++;
                ObjectFactory.get(LogService.class).warn("SM服务:第(%d)次重新连接中...",reConnCount);
            });
            socket.on(Socket.EVENT_RECONNECT_ERROR,args->{
                ObjectFactory.get(LogService.class).warn("SM服务:重接错误");
            });
            socket.on(Socket.EVENT_RECONNECT_FAILED,args->{
                ObjectFactory.get(LogService.class).warn("SM服务:重接失败,不再尝试重连,请检查网络状态后重启程序");
            });
            socket.on("connect_failed", args -> {
                if(!(args[0] instanceof Exception)){
                    JSONObject jsonObject = JSONObject.parseObject(args[0].toString());
                    ObjectFactory.get(LogService.class).error("SM服务:连接错误,%s",jsonObject.getString("msg"));
                }
            });
            socket.on(MsgHandleTypeEnum.SMS.getValue(), args -> {
                //获取消息
                SocketMessage socketMessage = JSONObject.parseObject(args[0].toString(), SocketMessage.class);
                //获取消息体
                SendSmsBySocket sendSmsBySocket = JSONObject.parseObject(socketMessage.getBody().toString(),SendSmsBySocket.class);
                //记录今日接收次数
                saveTodayCount();
                //记录日志
                ObjectFactory.get(LogService.class).info("SM服务:接收到SM服务发送请求,%s",sendSmsBySocket.getPhone().replaceAll("^([0-9]{3}).*([0-9]{4})$","$1****$2"));
                try {
                    boolean result = ObjectFactory.get(SmsProvideService.class).sendSms(sendSmsBySocket);
                }catch (Exception e){
                    ObjectFactory.get(LogService.class).error("SM服务:短信发送失败,%s",e.getMessage());
                    reply(MsgHandleTypeEnum.SMS.getValue(), SmsUtil.errorBody(sendSmsBySocket, false, e.getMessage()),null);
                }
            });
            socket.connect();
        } catch (Exception e) {
            Log.e(TAG, "connect: ", e);
            ObjectFactory.get(LogService.class).error("SM服务:发生异常,",e.getMessage());
        }
    }
    /**
     * @do 记录今日接收次数
     * @author liuhua
     * @date 2020/6/6 5:36 PM
     */
    private void saveTodayCount() {
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        String key = SocketUtil.getTodayCountKey();
        Integer count = sqlData.getObject(key, Integer.class);
        sqlData.saveObject(key,count==null?1:count+1);
        Objects.requireNonNull(ObjectFactory.get(SmRunningService.class)).referNotification();
    }

    /**
     * @do 发送消息
     * @author liuhua
     * @date 2020/3/15 1:06 PM
     */
    public void reply(String handle, Object obj, Ack ack){
        if(socket!=null){
            socket.emit(handle, JSONObject.toJSONString(obj),ack);
        }
    }

    public void sendMessage(String value, SocketMessage socketMessage, Object o) {
    }
}
