package com.lh.sms.client.ui.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.enums.YesNoEnum;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;

public class PersonRegister1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_register_1);
        //绑定事件
        bindEvent();
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
                if(pass) {
                    register.setBackgroundResource(R.color.colorPrimary);
                    register.setTag(YesNoEnum.YES.getValue());
                }else{
                    register.setBackgroundResource(R.color.primary_tran_5);
                    register.setTag(YesNoEnum.NO.getValue());
                }
            }
        });
        //注册事件
        register.setOnClickListener(v->{
            if(YesNoEnum.YES.getValue().equals(v.getTag())){
                //请求后台发送验证码
                Intent intent=new Intent(this, PersonRegister2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }

        });
    }

}