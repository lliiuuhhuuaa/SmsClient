package com.lh.sms.client.ui.about;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import com.lh.sms.client.MainActivity;
import com.lh.sms.client.R;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.ApplicationUtil;
import com.lh.sms.client.ui.person.msg.PersonUserMsg;
import com.lh.sms.client.ui.view.ShowWebView;
import com.lh.sms.client.work.app.entity.AppVersion;
import com.lh.sms.client.work.app.service.AppUpdateService;
import com.lh.sms.client.work.app.service.AppVersionService;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUs extends AppCompatActivity {
    private static final String TAG = "AboutUs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        //退出
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        //版本名称
        String versionName = ApplicationUtil.getVersionName(this);
        TextView textView = findViewById(R.id.about_version_name);
        textView.setText("版本号 "+versionName);
        //显示版本
        refreshInfo(false);
        //检查是否有新版本
        ObjectFactory.get(AppVersionService.class).checkNewVersion(()->{
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{AboutUs.this,false};
            message.getData().putString(handleMessage.METHOD_KEY,"refreshInfo");
            handleMessage.sendMessage(message);
        });
        findViewById(R.id.about_version_install).setOnClickListener(v->{
            //显示版本
            refreshInfo(true);
        });
        //服务协议
        findViewById(R.id.agreement).setOnClickListener(v->{
            Intent intent = new Intent(this, ShowWebView.class);
            intent.putExtra("url","file:///android_asset/agreement.html");
            intent.putExtra("title","服务协议");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //隐私协议
        findViewById(R.id.privacy).setOnClickListener(v->{
            Intent intent = new Intent(this, ShowWebView.class);
            intent.putExtra("url","file:///android_asset/privacy.html");
            intent.putExtra("title","隐私协议");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });


    }
    /**
     * @do 刷新版本信息
     * @author liuhua
     * @date 2020/5/24 10:48 PM
     */
    public void refreshInfo(Boolean showAlert){
        TextView versionCheck = findViewById(R.id.about_version_check);
        long currVersion = ApplicationUtil.getVersion(this);
        AppVersion appVersion = ObjectFactory.get(SqlData.class).getObject(AppVersion.class);
        if(appVersion==null){
            return;
        }
        if(appVersion.getVersion()<=currVersion){
            versionCheck.setText("已是最新版本");
            versionCheck.setTextColor(Color.GRAY);
        }else{
            versionCheck.setText(String.format("发现新版本:%s",appVersion.getVersionName()));
            versionCheck.setTextColor(getResources().getColor(R.color.colorPrimary,null));
        }
        if(showAlert){
            //弹窗app更新提示
            AppUpdateService appUpdateService = ObjectFactory.get(AppUpdateService.class);
            if(appUpdateService.startCheckUpdate()){
                appUpdateService.alert();
            }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
