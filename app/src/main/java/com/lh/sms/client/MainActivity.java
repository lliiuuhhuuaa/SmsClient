package com.lh.sms.client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.ui.dialog.SmAlertDialog;
import com.lh.sms.client.work.app.service.AppUpdateService;
import com.lh.sms.client.work.app.service.AppVersionService;
import com.lh.sms.client.work.config.service.ConfigService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Integer permissionsCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        //从通知栏中转到日志记录
        if (getIntent().getIntExtra("fragment", 0) == 2) {
            navController.navigate(R.id.navigation_record);
            return;
        }
        init();
        //请求权限
        requestPermission();
        //检查新版本
        checkNewVersion();
    }
    /**
     * @do 初始化
     * @author liuhua
     * @date 2020/4/21 8:14 PM
     */
    private void init() {
        ObjectFactory.push(this);
        //初始化本地数据库
        ObjectFactory.push(new SqlData());
        //初始化消息处理器
        ObjectFactory.push(new HandleMsg());
        //线程处理
        ThreadPool.exec(() -> {
            //更新本地配置
            ObjectFactory.get(ConfigService.class).updateConfig(null);
        });

    }
    /**
     * @do 请求权限
     * @author liuhua
     * @date 2020/3/13 5:23 PM
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.SEND_SMS};
            //逐个判断你要的权限是否已经通过
            List<String> applyPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    applyPermissionList.add(permissions[i]);//添加还未授予的权限
                }
            }
            //申请权限
            if (applyPermissionList.size() > 0) {//有权限没有通过，需要申请
                SmAlertDialog smAlertDialog = new SmAlertDialog(this);
                smAlertDialog.setTitleText("权限使用说明");
                smAlertDialog.setContentText("提供服务者需要以下权限\n【获取手机信息】\n【发送短信】\n如果无法弹出授权申请,请手动前往权限设置允许\n请知悉!!!");
                smAlertDialog.setConfirmListener(v -> {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions,permissionsCode);
                    smAlertDialog.cancel();
                });
                AlertUtil.alertOther(smAlertDialog);
                return;
            }
        }
        //初始化链接
        initSocket();
        return;

    }
    /**
     * @do 初始化连接
     * @author liuhua
     * @date 2020/6/5 8:48 PM
     */
    public void initSocket(){
        SubscriptionManager sManager = (SubscriptionManager) MainActivity.this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        List<SubscriptionInfo> mList = sManager.getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : mList) {
            if(StringUtils.isNotBlank(subscriptionInfo.getIccId())){
                if(sb.length()>0){
                    sb.append(",");
                }
                sb.append(subscriptionInfo.getIccId());
            }
        }
        if(sb.length()>0){
            ObjectFactory.get(SqlData.class).saveObject(DataConstant.LOCAL_ICC_ID,sb.toString());
            //启动service
            Intent intent = new Intent(MainActivity.this, SmRunningService.class);
            startService(intent);
        }
    }
    /**
     * @do 权限回调
     * @author liuhua
     * @date 2020/6/5 8:42 PM
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (permissionsCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                AlertUtil.toast(this,"未同意授予权限,如有需要请前往系统权限设置",Toast.LENGTH_LONG);
                return;
            }else{
                //全部权限通过，可以进行下一步操作。。。
                initSocket();
            }
        }
    }

    /**
     * @do 检查新版本
     * @author liuhua
     * @date 2020/5/27 9:48 PM
     */
    private void checkNewVersion() {

        //检查是否有新版本
        ObjectFactory.get(AppVersionService.class).checkNewVersion(()->{
            //弹窗app更新提示
            AppUpdateService appUpdateService = ObjectFactory.get(AppUpdateService.class);
            if(!appUpdateService.isNotPrompt()&&appUpdateService.startCheckUpdate()){
                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                message.obj = new Object[]{appUpdateService};
                message.getData().putString(handleMessage.METHOD_KEY,"alert");
                handleMessage.sendMessage(message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
