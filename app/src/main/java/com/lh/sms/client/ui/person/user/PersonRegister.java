package com.lh.sms.client.ui.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.person.user.enums.SmsTypeEnum;
import com.lh.sms.client.ui.util.UiUtil;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

public class PersonRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_register);
        //绑定事件
        bindEvent();
        //等下好关闭
        ObjectFactory.push(this);
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        //检测输入内容改变登陆按钮样式
        EditText phoneEdit = findViewById(R.id.phone);
        Button register = findViewById(R.id.person_register_button);
        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean pass = phoneEdit.getText().toString().matches("^1[0-9]{10}$");
                UiUtil.buttonEnable(register,pass);
            }
        });
        //注册事件
        register.setOnClickListener(v->{
            if(YesNoEnum.YES.getValue().equals(v.getTag())){
                //请求后台发送验证码
                FormBody param = new FormBody.Builder().add("phone", phoneEdit.getText().toString()).add("type", SmsTypeEnum.REGISTER.getValue()).build();
                HttpClientUtil.post(ApiConstant.SEND_SMS_CODE, param,
                        new HttpAsynResult(HttpAsynResult.Config.builder().context(PersonRegister.this).onlyOk(true).login(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        Intent intent=new Intent(PersonRegister.this, VerifySmsCode.class);
                        intent.putExtra("title","注册");
                        intent.putExtra("phone",phoneEdit.getText().toString());
                        intent.putExtra("type",SmsTypeEnum.REGISTER.getValue());
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                });

            }

        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
