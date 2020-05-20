package com.lh.sms.client.ui.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.lh.sms.client.R;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.util.UiUtil;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

public class PersonLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_login);
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
        //去注册
        findViewById(R.id.person_login_register).setOnClickListener(v->{
            Intent intent=new Intent(this, PersonRegister.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //找回密码
        findViewById(R.id.person_login_forget).setOnClickListener(v->{
            Intent intent=new Intent(this, PersonFindPass.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //检测输入内容改变登陆按钮样式
        EditText accountEdit = findViewById(R.id.account);
        EditText passwordEdit = findViewById(R.id.password);
        Button login = findViewById(R.id.person_login_button);
        boolean[] pass = new boolean[]{StringUtils.isNotBlank(accountEdit.getText().toString()),StringUtils.isNotBlank(passwordEdit.getText().toString())};
        UiUtil.buttonEnable(login,pass[0]&&pass[1]);
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
                UiUtil.buttonEnable(login,pass[0]&&pass[1]);
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
                UiUtil.buttonEnable(login,pass[0]&&pass[1]);
            }
        });
        login.setOnClickListener(v->{
            FormBody param = new FormBody.Builder().add("account",accountEdit.getText().toString() )
                    .add("password",passwordEdit.getText().toString()).build();
            HttpClientUtil.post(ApiConstant.USER_LOGIN, param,
                    new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).context(PersonLogin.this).login(false)) {
                        @Override
                        public void callback(HttpResult httpResult) {
                            SqlData sqlData = ObjectFactory.get(SqlData.class);
                            sqlData.deleteAll();
                            sqlData.saveObject(DataConstant.KEY_USER_TK,httpResult.getData());
                            sqlData.saveObject(DataConstant.KEY_IS_LOGIN,YesNoEnum.YES.getValue());
                            finish();
                        }
                    });
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
