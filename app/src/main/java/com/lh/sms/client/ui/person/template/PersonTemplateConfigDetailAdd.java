package com.lh.sms.client.ui.person.template;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.work.app.entity.AppConfig;
import com.lh.sms.client.work.template.entity.SmsTemplate;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

/**
 * @do 应该配置详情
 * @author liuhua
 * @date 2020/5/13 7:15 PM
 */
public class PersonTemplateConfigDetailAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_template_config_detail_add);
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
        //状态开关
        EditText editTextName = findViewById(R.id.person_template_name);
        EditText editTextText = findViewById(R.id.person_template_text);
        Switch stateSwitch = findViewById(R.id.person_template_state);
        stateSwitch.setChecked(true);
        findViewById(R.id.person_template_button).setOnClickListener(v->{
            SmsTemplate smsTemplate = new SmsTemplate();
            smsTemplate.setState(stateSwitch.isChecked()?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            smsTemplate.setName(editTextName.getText().toString());
            smsTemplate.setText(editTextText.getText().toString());
            updateTemplate(smsTemplate);
        });
        EditText paramEditText = findViewById(R.id.person_template_param);
        //插入变量事件
        findViewById(R.id.person_template_param_add).setOnClickListener(v->{
            String s = paramEditText.getText().toString();
            if(StringUtils.isBlank(s)){
                AlertUtil.toast(PersonTemplateConfigDetailAdd.this,"参数名称不能为空哦",Toast.LENGTH_SHORT);
                return;
            }
            editTextText.setText(editTextText.getText().append("${").append(s).append("}"));
            paramEditText.setText("");
        });
    }

    /**
     * @do 添加模板
     * @author liuhua
     * @date 2020/5/9 10:36 PM
     */
    public void updateTemplate(SmsTemplate smsTemplate) {
        FormBody.Builder param = new FormBody.Builder().add("text", smsTemplate.getText()).add("name", smsTemplate.getName())
                .add("state", smsTemplate.getState().toString());
        HttpClientUtil.post(ApiConstant.TEMPLATE_CONFIG_UPDATE, param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().context(PersonTemplateConfigDetailAdd.this).onlyOk(true)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        AlertUtil.toast(PersonTemplateConfigDetailAdd.this, "操作成功", Toast.LENGTH_SHORT);
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
