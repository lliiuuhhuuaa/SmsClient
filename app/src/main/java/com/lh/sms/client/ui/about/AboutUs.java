package com.lh.sms.client.ui.about;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.ApplicationUtil;
import com.lh.sms.client.ui.person.app.PersonAppConfigDetail;
import com.lh.sms.client.work.config.service.ConfigService;

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
        //检查是否有新版本
        ObjectFactory.get(ConfigService.class).updateConfig(()->{
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{AboutUs.this};
            message.getData().putString(handleMessage.METHOD_KEY,"refreshInfo");
            handleMessage.sendMessage(message);
        });

    }
    /**
     * @do 刷新版本信息
     * @author liuhua
     * @date 2020/5/24 10:48 PM
     */
    public void refreshInfo(){
        TextView versionCheck = findViewById(R.id.about_version_check);
        long currVersion = ApplicationUtil.getVersion(this);
        String version = ObjectFactory.get(SqlData.class).getObject(DataConstant.KEY_APP_VERSION, String.class);
        if(version==null){
            return;
        }
        String[] split = version.split(",");
        if(Integer.valueOf(split[0])<=currVersion){
            versionCheck.setText("已是最新版本");
            versionCheck.setTextColor(Color.GRAY);
        }else{
            versionCheck.setText("发现新版本:"+split[1]);
            versionCheck.setTextColor(getResources().getColor(R.color.colorPrimary,null));
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
