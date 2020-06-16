package com.lh.sms.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.work.socket.service.SocketService;
import com.lh.sms.client.work.socket.util.SocketUtil;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;

public class SmRunningService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        connectSocket();
        show();
        ObjectFactory.push(this);
    }
    /**
     * @do 创建通知前台运行
     * @author liuhua
     * @date 2020/3/21 5:29 PM
     */
    public void show() {
        Integer state = ObjectFactory.get(SqlData.class).getObject(DataConstant.SHOW_NOTICE, Integer.class);
        if(state!=null&& !YesNoEnum.isYes(state)){
            return;
        }
        String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
        String CHANNEL_ONE_NAME= "CHANNEL_ONE_ID";
        NotificationChannel notificationChannel= null;
        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel= new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment",2);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        Integer count = Objects.requireNonNull(sqlData).getObject(SocketUtil.getTodayCountKey(), Integer.class);
        Notification notification = builder.setTicker("Nature")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("发送监听服务持续运行中...")
                .setContentIntent(pendingIntent)
                .setContentText(String.format(Locale.CHINA,"今日已收到发送请求[%d]",count!=null?count:0))
                .setOngoing(true)
                .build();
        notification.flags|= Notification.FLAG_NO_CLEAR;
        startForeground(1, notification);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        intent.putExtra("fragment",2);
        return new Binder();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    /**
     * @do 准备连接
     * @author liuhua
     * @date 2020/3/21 10:10 AM
     */
    private void connectSocket() {
        //连接socket
        ObjectFactory.get(SocketService.class).connect();
    }
    /**
     * @do 关闭服务
     * @author liuhua
     * @date 2020/6/6 6:46 PM
     */
    public void close() {
        try {
            stopForeground(true);
        }catch (Exception e){

        }
    }
}
