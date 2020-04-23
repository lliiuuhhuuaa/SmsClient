package com.lh.sms.client.ui.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.ui.person.balance.PersonBalanceTransactionRecord;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;

public class PersonLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_login);
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
        //去注册
        findViewById(R.id.person_login_register).setOnClickListener(v->{
            Intent intent=new Intent(this, PersonRegister1.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //检测输入内容改变登陆按钮样式
        EditText accountEdit = findViewById(R.id.account);
        EditText passwordEdit = findViewById(R.id.password);
        Button login = findViewById(R.id.person_login_button);
        boolean[] pass = new boolean[]{false,false};
        accountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pass[0] = StringUtils.isNotBlank(accountEdit.getText().toString());
                if(pass[0]&&pass[1]) {
                    login.setBackgroundResource(R.color.colorPrimary);
                    login.setTag(YesNoEnum.YES.getValue());
                }else{
                    login.setBackgroundResource(R.color.primary_tran_5);
                    login.setTag(YesNoEnum.NO.getValue());
                }
            }
        });
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pass[1] = StringUtils.isNotBlank(passwordEdit.getText().toString());
                if(pass[0]&&pass[1]) {
                    login.setBackgroundResource(R.color.colorPrimary);
                    login.setTag(YesNoEnum.YES.getValue());
                }else{
                    login.setBackgroundResource(R.color.primary_tran_5);
                    login.setTag(YesNoEnum.NO.getValue());
                }
            }
        });
    }

}
