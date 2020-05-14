package com.lh.sms.client.ui.person.sms;

import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.lh.sms.client.work.sms.entity.SmsProvide;
import com.lh.sms.client.work.sms.enums.SmStateEnum;
import com.lh.sms.client.work.sms.service.SmsProvideService;

import org.joda.time.LocalDate;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

public class PersonSmsConfigDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_sms_config_detail);
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
        String iccId = getIntent().getStringExtra("iccId");
        SmsProvide smsProvide = ObjectFactory.get(SqlData.class).getObject(TablesEnum.SM_LIST.getTable(), iccId, SmsProvide.class);
        if (smsProvide == null) {
            return;
        }
        TextView textView = findViewById(R.id.list_item_icc_id);
        textView.setText(iccId);
        textView = findViewById(R.id.sms_config_detail_total_count);
        textView.setText(smsProvide.getTotalCount().toString());
        textView = findViewById(R.id.sms_config_detail_month_count);
        textView.setText(smsProvide.getMonthCount().toString());
        textView = findViewById(R.id.sms_config_detail_month_max);
        textView.setText(smsProvide.getMonthMax().toString());
        textView = findViewById(R.id.sms_config_detail_create_date);
        textView.setText(LocalDate.fromDateFields(new Date(smsProvide.getCreateDate())).toString("yyyy-MM-dd"));
        Button stateButton = findViewById(R.id.list_item_state);
        stateButton.setTag(smsProvide.getState());
        stateButton.setText(YesNoEnum.YES.getValue().equals(smsProvide.getState()) ? "正在使用" : "已停用");
        stateButton.setOnClickListener(v -> {
            int state = (int) v.getTag();
            Integer newState = YesNoEnum.YES.getValue().equals(state) ? YesNoEnum.NO.getValue() : YesNoEnum.YES.getValue();
            SmsProvide updateSmsProvide = new SmsProvide();
            updateSmsProvide.setIccId(iccId);
            updateSmsProvide.setState(newState);
            updateSm(updateSmsProvide);
        });
        findViewById(R.id.list_item_un_register).setOnClickListener(v->{
            SmsProvide updateSmsProvide = new SmsProvide();
            updateSmsProvide.setIccId(iccId);
            updateSmsProvide.setState(SmStateEnum.DELETE.getValue());
            updateSm(updateSmsProvide);
        });
        //更新最大发送数
        EditText editText = findViewById(R.id.sms_config_detail_input);
        findViewById(R.id.sms_config_detail_save).setOnClickListener(v-> {
            if(!editText.getText().toString().matches("^[0-9]{1,4}$")){
                AlertUtil.toast(PersonSmsConfigDetail.this,"输入内容有误",Toast.LENGTH_SHORT);
                editText.setText("");
                return;
            }
            Integer max = Integer.valueOf(editText.getText().toString());
            editText.setText("");
            SmsProvide updateSmsProvide = new SmsProvide();
            updateSmsProvide.setIccId(iccId);
            updateSmsProvide.setMonthMax(max);
            updateSm(updateSmsProvide);
        });
        //个人服务
        Switch privateSwitch = findViewById(R.id.sms_config_detail_private);
        privateSwitch.setChecked(YesNoEnum.isYes(smsProvide.getServicePrivate()));
        privateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SmsProvide updateSmsProvide = new SmsProvide();
            updateSmsProvide.setIccId(iccId);
            updateSmsProvide.setServicePrivate(isChecked?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            updateSm(updateSmsProvide);
        });
        //公共服务
        Switch publicSwitch = findViewById(R.id.sms_config_detail_public);
        publicSwitch.setChecked(YesNoEnum.isYes(smsProvide.getServicePublic()));
        publicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SmsProvide updateSmsProvide = new SmsProvide();
            updateSmsProvide.setIccId(iccId);
            updateSmsProvide.setServicePublic(isChecked?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            updateSm(updateSmsProvide);
        });
    }

    /**
     * @do 注册SM卡
     * @author liuhua
     * @date 2020/5/9 10:36 PM
     */
    public void updateSm(SmsProvide updateSmsProvide) {
        FormBody.Builder param = new FormBody.Builder().add("iccId", updateSmsProvide.getIccId());
        if (updateSmsProvide.getMonthMax() != null) {
            param.add("monthMax", updateSmsProvide.getMonthMax().toString());
        }
        if (updateSmsProvide.getState() != null) {
            param.add("state", updateSmsProvide.getState().toString());
        }
        if (updateSmsProvide.getServicePrivate() != null) {
            param.add("servicePrivate", updateSmsProvide.getServicePrivate().toString());
        }
        if (updateSmsProvide.getServicePublic() != null) {
            param.add("servicePublic", updateSmsProvide.getServicePublic().toString());
        }
        HttpClientUtil.post(ApiConstant.PROVIDE_UPDATE, param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().login(true).context(PersonSmsConfigDetail.this)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        AlertUtil.toast(PersonSmsConfigDetail.this, "操作成功", Toast.LENGTH_SHORT);
                        if(SmStateEnum.DELETE.getValue().equals(updateSmsProvide.getState())){
                            //取消注册
                            ObjectFactory.get(SqlData.class).deleteObject(TablesEnum.SM_LIST.getTable(),updateSmsProvide.getIccId());
                            finish();
                            return;
                        }
                        //保存状态
                        ObjectFactory.get(SmsProvideService.class).cacheSmsProvide(updateSmsProvide);

                        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                        Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                        message.obj = new Object[]{PersonSmsConfigDetail.this, updateSmsProvide.getIccId()};
                        message.getData().putString(handleMessage.METHOD_KEY, "refreshShow");
                        handleMessage.sendMessage(message);
                    }
                });
    }

    /**
     * @do 刷新显示
     * @author liuhua
     * @date 2020/5/9 11:12 PM
     */
    public void refreshShow(String iccId) {
        SmsProvide smsProvide = ObjectFactory.get(SqlData.class).getObject(TablesEnum.SM_LIST.getTable(), iccId, SmsProvide.class);
        if (smsProvide == null) {
            return;
        }
        TextView textView = findViewById(R.id.list_item_icc_id);
        textView.setText(iccId);
        textView = findViewById(R.id.sms_config_detail_total_count);
        textView.setText(smsProvide.getTotalCount().toString());
        textView = findViewById(R.id.sms_config_detail_month_count);
        textView.setText(smsProvide.getMonthCount().toString());
        textView = findViewById(R.id.sms_config_detail_month_max);
        textView.setText(smsProvide.getMonthMax().toString());
        Button stateButton = findViewById(R.id.list_item_state);
        stateButton.setTag(smsProvide.getState());
        stateButton.setText(YesNoEnum.YES.getValue().equals(smsProvide.getState()) ? "正在使用" : "已停用");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
