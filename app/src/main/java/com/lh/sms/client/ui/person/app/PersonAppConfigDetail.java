package com.lh.sms.client.ui.person.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Message;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.R;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.work.app.entity.AppConfig;
import com.lh.sms.client.work.app.service.AppConfigService;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

/**
 * @do 应该配置详情
 * @author liuhua
 * @date 2020/5/13 7:15 PM
 */
public class PersonAppConfigDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_app_config_detail);
        //绑定事件
        bindEvent();
    }

    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v -> {
            finish();
        });
        AppConfig appConfig = JSONObject.parseObject(getIntent().getStringExtra("appConfig"),AppConfig.class);

        //使用公共服务开关
        Switch publicSwitch = findViewById(R.id.person_app_config_public);
        Switch stateSwitch = findViewById(R.id.person_app_config_state);
        publicSwitch.setChecked(YesNoEnum.isYes(appConfig.getUsePublic()));
        stateSwitch.setChecked(YesNoEnum.isYes(appConfig.getState()));
        TextView textView = findViewById(R.id.person_app_config_app_id);
        textView.setText(appConfig.getAppId());
        textView.setOnClickListener(v->{
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(PersonAppConfigDetail.CLIPBOARD_SERVICE);
            // 将ClipData内容放到系统剪贴板里。
            if(cm!=null) {
                cm.setPrimaryClip(ClipData.newPlainText("Label", appConfig.getAppId()));
                AlertUtil.toast(PersonAppConfigDetail.this, "应用ID已复制到粘贴板", Toast.LENGTH_SHORT);
            }
        });
        textView = findViewById(R.id.person_app_config_key);
        textView.setText(appConfig.getSecurityKey());
        //点击复制
        textView.setOnClickListener(v->{
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(PersonAppConfigDetail.CLIPBOARD_SERVICE);
            // 将ClipData内容放到系统剪贴板里。
            if(cm!=null) {
                cm.setPrimaryClip(ClipData.newPlainText("Label", appConfig.getSecurityKey()));
                AlertUtil.toast(PersonAppConfigDetail.this, "密钥已复制到粘贴板", Toast.LENGTH_SHORT);
            }
        });
        textView = findViewById(R.id.person_app_config_private_count);
        textView.setText(appConfig.getPrivateCount()==null?"0":appConfig.getPrivateCount().toString());
        textView = findViewById(R.id.person_app_config_public_count);
        textView.setText(appConfig.getPublicCount()==null?"0":appConfig.getPublicCount().toString());
        //重置密钥
        findViewById(R.id.person_app_config_reset_key).setOnClickListener(v->{
            AppConfig updateAppConfig = new AppConfig();
            updateAppConfig.setAppId(appConfig.getAppId());
            updateAppConfig.setSecurityKey(YesNoEnum.YES.getValue().toString());
            updateAppConfig(updateAppConfig);
        });
        //使用公共服务
        publicSwitch.setOnCheckedChangeListener((buttonView, isChecked)->{
            AppConfig updateAppConfig = new AppConfig();
            updateAppConfig.setAppId(appConfig.getAppId());
            updateAppConfig.setUsePublic(isChecked?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            updateAppConfig(updateAppConfig);
        });
        //状态
        stateSwitch.setOnCheckedChangeListener((buttonView, isChecked)->{
            AppConfig updateAppConfig = new AppConfig();
            updateAppConfig.setAppId(appConfig.getAppId());
            updateAppConfig.setState(isChecked?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            updateAppConfig(updateAppConfig);
        });
        findViewById(R.id.person_app_config_button).setOnClickListener(v->{
            AppConfig updateAppConfig = new AppConfig();
            updateAppConfig.setAppId(appConfig.getAppId());
            updateAppConfig.setState(-1);
            updateAppConfig(updateAppConfig);
        });
    }

    /**
     * @do 添加应用
     * @author liuhua
     * @date 2020/5/9 10:36 PM
     */
    public void updateAppConfig(AppConfig appConfig) {
        FormBody.Builder param = new FormBody.Builder().add("appId", appConfig.getAppId());
        if(appConfig.getState()!=null){
            param.add("state", appConfig.getState().toString());
        }
        if(appConfig.getUsePublic()!=null){
            param.add("usePublic", appConfig.getUsePublic().toString());
        }
        if(appConfig.getSecurityKey()!=null){
            param.add("securityKey", appConfig.getSecurityKey());
        }
        HttpClientUtil.post(ApiConstant.APP_CONFIG_UPDATE, param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().login(true).context(PersonAppConfigDetail.this).onlyOk(true)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        AlertUtil.toast("操作成功", Toast.LENGTH_SHORT);
                        SqlData sqlData = ObjectFactory.get(SqlData.class);
                        if(appConfig.getSecurityKey()!=null){
                            ObjectFactory.get(AppConfigService.class).refreshAppConfig(()->{
                                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                                message.obj = new Object[]{PersonAppConfigDetail.this,appConfig.getAppId()};
                                message.getData().putString(handleMessage.METHOD_KEY,"refreshData");
                                handleMessage.sendMessage(message);
                            });
                            return;
                        }
                        //删除
                        if(appConfig.getState()!=null&&appConfig.getState()==-1){
                            sqlData.deleteObject(TablesEnum.APP_LIST.getTable(), appConfig.getAppId());
                            finish();
                            return;
                        }
                        AppConfig saveAppConfig = sqlData.getObject(TablesEnum.APP_LIST.getTable(), appConfig.getAppId(), AppConfig.class);
                        if(appConfig.getState()!=null){
                            saveAppConfig.setState(appConfig.getState());
                        }
                        if(appConfig.getUsePublic()!=null){
                            saveAppConfig.setUsePublic(appConfig.getUsePublic());
                        }
                        sqlData.saveObject(TablesEnum.APP_LIST.getTable(), appConfig.getAppId(),saveAppConfig);
                    }
                });
    }
    /**
     * @do 刷新显示
     * @author liuhua
     * @date 2020/5/13 11:05 PM
     */
    public void refreshData(String appId){
        AppConfig appConfig = ObjectFactory.get(SqlData.class).getObject(TablesEnum.APP_LIST.getTable(), appId, AppConfig.class);
        Switch publicSwitch = findViewById(R.id.person_app_config_public);
        Switch stateSwitch = findViewById(R.id.person_app_config_state);
        publicSwitch.setChecked(YesNoEnum.isYes(appConfig.getUsePublic()));
        stateSwitch.setChecked(YesNoEnum.isYes(appConfig.getState()));
        TextView textView = findViewById(R.id.person_app_config_app_id);
        textView.setText(appConfig.getAppId());
        textView = findViewById(R.id.person_app_config_key);
        textView.setText(appConfig.getSecurityKey());
        textView = findViewById(R.id.person_app_config_private_count);
        textView.setText(appConfig.getPrivateCount()==null?"0":appConfig.getPrivateCount().toString());
        textView = findViewById(R.id.person_app_config_public_count);
        textView.setText(appConfig.getPublicCount()==null?"0":appConfig.getPublicCount().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
