package com.lh.sms.client.work.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.MainActivity;
import com.lh.sms.client.R;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ActivityManager;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.AuthRequestCodeEnum;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.ApplicationUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.dialog.SmAlertDialog;
import com.lh.sms.client.work.app.entity.AppVersion;
import com.lh.sms.client.work.app.util.VersionUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import lombok.Getter;
import okhttp3.Response;
/**
 * @do app更新
 * @author liuhua
 * @date 2020/5/27 8:45 PM
 */
public class AppUpdateService {
    private static final String TAG = "AppUpdateService";
    private NotificationManager notificationManager;
    private Notification notification; //下载通知进度提示
    private NotificationCompat.Builder builder;
    public static boolean isUpdate = false; //是否正在更新
    private int manageId = 1;
    private AppVersion appVersion = null;
    private File appPath = null;
    @Getter
    private boolean notPrompt = false;
    /**
     * @do 初始化必须参数
     * @author liuhua
     * @date 2020/5/27 8:50 PM
     */
    public boolean startCheckUpdate() {
        this.appPath = new File(ActivityManager.getInstance().getCurrentActivity().getCacheDir(),"app");
        if(!this.appPath.exists()){
            this.appPath.mkdirs();
        }
        return true;
    }
    /**
     * @do 弹更新提示窗
     * @author liuhua
     * @date 2020/5/27 8:51 PM
     */
    public void alert(){
        AppVersion appVersion = ObjectFactory.get(SqlData.class).getObject(AppVersion.class);
        if(appVersion==null){
            return;
        }
        long currVersion = ApplicationUtil.getVersion(ActivityManager.getInstance().getCurrentActivity());
        if(appVersion.getVersion()<=currVersion){
            return;
        }
        this.appVersion = appVersion;
        File file = new File(appPath,String.format("smsApp_%s.apk",appVersion.getVersion()));
        if(file.exists()){
            //提示用户安装
            hintInstallApk();
            return;
        }
        //显示更新弹窗
        SmAlertDialog smAlertDialog = new SmAlertDialog(ActivityManager.getInstance().getCurrentActivity())
                .setTitleText("更新提示");
        View view = ActivityManager.getInstance().getCurrentActivity().getLayoutInflater().inflate(R.layout.activity_app_version_alert, null);
        TextView textView = view.findViewById(R.id.app_version_notice);
        textView.setText(appVersion.getNotice());
        textView = view.findViewById(R.id.app_version_size);
        textView.setText(String.format("更新包大小: %sMB", BigDecimal.valueOf(appVersion.getSize()/1024f/1024f).setScale(2, RoundingMode.FLOOR).toString()));
        smAlertDialog.setContentView(view);
        smAlertDialog.setCancelable(false);
        smAlertDialog.setConfirmText("立即下载");
        smAlertDialog.setCancelText("取消");
        smAlertDialog.setConfirmListener(v->{
            smAlertDialog.cancel();
            download();
        });
        smAlertDialog.setCancelListener(v->{
            smAlertDialog.cancel();
        });
        AlertUtil.alertOther(smAlertDialog);
    }
    //初始化通知
    private void initNotification() {
        String VERSION_UPDATE_ID = "version_update_id";
        notificationManager = (NotificationManager)ObjectFactory.get(MainActivity.class).getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(VERSION_UPDATE_ID, ActivityManager.getInstance().getCurrentActivity().getPackageName(), NotificationManager.IMPORTANCE_MIN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//锁屏显示通知
            channel.enableVibration(false);
            //通知管理者创建的渠道
            notificationManager.createNotificationChannel(channel);
        }
        builder = new NotificationCompat.Builder(ActivityManager.getInstance().getCurrentActivity(), VERSION_UPDATE_ID);
        builder.setContentTitle("更新包下载中...") //设置通知标题
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(false)//设置通知被点击一次是否自动取消
                .setContentText("六画短信客户端已下载" + "0%")
                .setVibrate(null)
                .setDefaults(Notification.DEFAULT_ALL)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setProgress(100, 0, false);
        notification = builder.build();//构建通知对象
        notificationManager.notify(manageId, builder.build());
    }
    /**
     * @do 下载app
     * @author liuhua
     * @date 2020/5/27 7:33 PM
     */
    public void download() {
        File file = new File(appPath,String.format("smsApp_%s.apk",appVersion.getVersion()));
        if(file.exists()){
            //提示用户安装
            hintInstallApk();
            return;
        }
        if(isUpdate){
            AlertUtil.toast(ActivityManager.getInstance().getCurrentActivity(),"正在下载中...", Toast.LENGTH_SHORT);
            return;
        }
        isUpdate = true;
        initNotification();
        HttpClientUtil.get(ApiConstant.DOWNLOAD_VERSION.replace("{version}", appVersion.getVersion().toString()),
                new HttpAsynResult(HttpAsynResult.Config.builder().context(ActivityManager.getInstance().getCurrentActivity()).login(false).animation(false).file(true)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        isUpdate = true;
                        AlertUtil.toast(ActivityManager.getInstance().getCurrentActivity(),httpResult.getMsg(),Toast.LENGTH_LONG);
                    }

                    @Override
                    public void callback(Response response) {
                        OutputStream outputStream = null;
                        InputStream inputStream = null;
                        boolean isDone = false;
                        try {
                            outputStream = new FileOutputStream(file);
                            inputStream = response.body().byteStream();
                            double totalSize = Double.valueOf(response.header("content-length"));
                            long ingSize = 0;
                            byte[] bytes = new byte[1024];
                            int len = 0;
                            while ((len =inputStream.read(bytes))>0){
                                outputStream.write(bytes,0,len);
                                ingSize+=len;
                                double ing = ingSize / totalSize;
                                int process = (int) (ing * 100);
                                if(process>100){
                                    process = 100;
                                }
                                builder.setProgress(100, process, false);
                                builder.setContentText("六画短信客户端已下载" + process + "%");
                                notification = builder.build();
                                notificationManager.notify(manageId, notification);
                                isDone = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            if(outputStream!=null){
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(inputStream!=null){
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        notificationManager.cancel(manageId);
                        isUpdate = false;
                        if(!isDone||!file.exists()){
                            AlertUtil.toast(ActivityManager.getInstance().getCurrentActivity(),"下载新版App失败,请重试", Toast.LENGTH_SHORT);
                            file.delete();
                            return;
                        }
                        //检查安装包完整性
                        String fileMd5 = VersionUtil.getFileMd5(file);
                        if(fileMd5 == null||!fileMd5.equals(appVersion.getMd5())){
                            AlertUtil.toast(ActivityManager.getInstance().getCurrentActivity(),"下载新版App失败,请重试", Toast.LENGTH_SHORT);
                            file.delete();
                            return;
                        }
                        //提示用户安装
                        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                        Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                        message.obj = new Object[]{AppUpdateService.this};
                        message.getData().putString(handleMessage.METHOD_KEY,"hintInstallApk");
                        handleMessage.sendMessage(message);
                    }
                });
    }
    /**
     * @do 提示安装app
     * @author liuhua
     * @date 2020/5/27 7:13 PM
     */
    public void hintInstallApk() {
        //显示更新弹窗
        SmAlertDialog smAlertDialog = new SmAlertDialog(ActivityManager.getInstance().getCurrentActivity())
                .setTitleText("安装提示");
        smAlertDialog.setContentText("六画短信客户端下载完成");
        smAlertDialog.setCancelable(false);
        smAlertDialog.setConfirmText("立即安装");
        smAlertDialog.setCancelText("取消");
        smAlertDialog.setConfirmListener(v -> {
            readyInstallApk();
        });
        smAlertDialog.setCanceledOnTouchOutside(false);
        smAlertDialog.setCancelListener(v->{
            isUpdate = false;
            notPrompt = true;
            smAlertDialog.cancel();
        });
        AlertUtil.alertOther(smAlertDialog);
    }
   /**
    * @do 准备安装
    * @author liuhua
    * @date 2020/5/27 7:13 PM
    */
    public void readyInstallApk() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ActivityManager.getInstance().getCurrentActivity().getPackageManager().canRequestPackageInstalls()) {
            //是否有安装位置来源的权限
            Uri packageUri = Uri.parse("package:"+ ActivityManager.getInstance().getCurrentActivity().getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageUri);
            ObjectFactory.get(MainActivity.class).startActivityForResult(intent, AuthRequestCodeEnum.INSTALL_APK.getValue());
            return;
        }*/
        installApk();

    }
    /**
     * @do 安装apk
     * @author liuhua
     * @date 2020/5/27 7:33 PM
     */
    public void installApk() {
        File file = new File(appPath,String.format("smsApp_%s.apk",appVersion.getVersion()));
        if(!file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(ActivityManager.getInstance().getCurrentActivity(), ActivityManager.getInstance().getCurrentActivity().getPackageName()+".fileProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        ActivityManager.getInstance().getCurrentActivity().startActivity(intent);
    }


}
 