package com.lh.sms.client.ui.person.template;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.EditText;
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
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.work.template.entity.SmsTemplate;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

/**
 * @do 应该配置详情
 * @author liuhua
 * @date 2020/5/13 7:15 PM
 */
public class PersonTemplateConfigDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_template_config_detail);
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
        SmsTemplate smsTemplate = JSONObject.parseObject(getIntent().getStringExtra("smsTemplate"), SmsTemplate.class);
        if(smsTemplate==null){
            finish();
        }
        //状态开关
        Switch stateSwitch = findViewById(R.id.person_template_state);
        stateSwitch.setChecked(YesNoEnum.isYes(smsTemplate.getState()));
        TextView textView = findViewById(R.id.person_template_code);
        textView.setText(smsTemplate.getCode());
        textView.setOnClickListener(v->{
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(PersonTemplateConfigDetail.CLIPBOARD_SERVICE);
            // 将ClipData内容放到系统剪贴板里。
            if(cm!=null) {
                cm.setPrimaryClip(ClipData.newPlainText("Label", smsTemplate.getCode()));
                AlertUtil.toast(PersonTemplateConfigDetail.this, "模板Code已复制到粘贴板", Toast.LENGTH_SHORT);
            }
        });
        EditText editTextName = findViewById(R.id.person_template_name);
        editTextName.setText(smsTemplate.getName());
        textView = findViewById(R.id.person_template_auth_result);
        textView.setText(smsTemplate.getAuthResult());
        EditText editTextText = findViewById(R.id.person_template_text);
        editTextText.setText(smsTemplate.getText());
        EditText paramEditText = findViewById(R.id.person_template_param);
        //插入变量事件
        findViewById(R.id.person_template_param_add).setOnClickListener(v->{
            String s = paramEditText.getText().toString();
            if(StringUtils.isBlank(s)){
                AlertUtil.toast(PersonTemplateConfigDetail.this,"参数名称不能为空哦",Toast.LENGTH_SHORT);
                return;
            }
            editTextText.setText(editTextText.getText().append("${").append(s).append("}"));
            paramEditText.setText("");
        });
        findViewById(R.id.person_template_button).setOnClickListener(v->{
            SmsTemplate updateSmsTemplate = new SmsTemplate();
            updateSmsTemplate.setCode(smsTemplate.getCode());
            updateSmsTemplate.setName(editTextName.getText().toString());
            updateSmsTemplate.setText(editTextText.getText().toString());
            updateSmsTemplate.setState(stateSwitch.isChecked()?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            updateSmsTemplateConfig(updateSmsTemplate);
        });
        findViewById(R.id.person_template_delete).setOnClickListener(v->{
            SmsTemplate updateSmsTemplate = new SmsTemplate();
            updateSmsTemplate.setCode(smsTemplate.getCode());
            updateSmsTemplate.setState(-1);
            updateSmsTemplateConfig(updateSmsTemplate);
        });
    }

    /**
     * @do 添加应用
     * @author liuhua
     * @date 2020/5/9 10:36 PM
     */
    public void updateSmsTemplateConfig(SmsTemplate smsTemplate) {
        FormBody.Builder param = new FormBody.Builder().add("code", smsTemplate.getCode());
        if(smsTemplate.getState()!=null){
            param.add("state", smsTemplate.getState().toString());
        }
        if(smsTemplate.getName()!=null){
            param.add("name", smsTemplate.getName().toString());
        }
        if(smsTemplate.getText()!=null){
            param.add("text", smsTemplate.getText());
        }
        HttpClientUtil.post(ApiConstant.TEMPLATE_CONFIG_UPDATE, param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().login(true).context(PersonTemplateConfigDetail.this).onlyOk(true)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        AlertUtil.toast(PersonTemplateConfigDetail.this, "操作成功", Toast.LENGTH_SHORT);
                        SqlData sqlData = ObjectFactory.get(SqlData.class);
                        //删除
                        if(smsTemplate.getState()!=null&&smsTemplate.getState()==-1){
                            sqlData.deleteObject(TablesEnum.TEMPLATE_LIST.getTable(), smsTemplate.getCode());
                            finish();
                            return;
                        }
                        SmsTemplate saveSmsTemplate = sqlData.getObject(TablesEnum.TEMPLATE_LIST.getTable(), smsTemplate.getCode(), SmsTemplate.class);
                        if(smsTemplate.getState()!=null){
                            saveSmsTemplate.setState(smsTemplate.getState());
                        }
                        if(smsTemplate.getText()!=null){
                            saveSmsTemplate.setText(smsTemplate.getText());
                        }
                        if(smsTemplate.getName()!=null){
                            saveSmsTemplate.setName(smsTemplate.getName());
                        }
                        sqlData.saveObject(TablesEnum.TEMPLATE_LIST.getTable(), saveSmsTemplate.getCode(),saveSmsTemplate);
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
