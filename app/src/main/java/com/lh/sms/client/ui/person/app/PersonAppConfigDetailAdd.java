package com.lh.sms.client.ui.person.app;

import android.os.Bundle;
import android.os.Message;
import android.widget.Switch;
import android.widget.Toast;

import com.lh.sms.client.R;
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
public class PersonAppConfigDetailAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_app_config_detail_add);
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
        //使用公共服务开关
        Switch publicSwitch = findViewById(R.id.person_app_config_public);
        Switch stateSwitch = findViewById(R.id.person_app_config_state);
        publicSwitch.setChecked(false);
        stateSwitch.setChecked(true);
        findViewById(R.id.person_app_config_button).setOnClickListener(v->{
            AppConfig appConfig = new AppConfig();
            appConfig.setUsePublic(publicSwitch.isChecked()?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            appConfig.setState(stateSwitch.isChecked()?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            updateAppConfig(appConfig);
        });
    }

    /**
     * @do 添加应用
     * @author liuhua
     * @date 2020/5/9 10:36 PM
     */
    public void updateAppConfig(AppConfig appConfig) {
        FormBody.Builder param = new FormBody.Builder().add("usePublic", appConfig.getUsePublic().toString())
                .add("state", appConfig.getState().toString());
        HttpClientUtil.post(ApiConstant.APP_CONFIG_UPDATE, param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().login(true).context(PersonAppConfigDetailAdd.this).onlyOk(true)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        AlertUtil.toast("操作成功", Toast.LENGTH_SHORT);
                        finish();
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
