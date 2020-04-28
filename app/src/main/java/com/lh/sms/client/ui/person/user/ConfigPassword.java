package com.lh.sms.client.ui.person.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.MainActivity;
import com.lh.sms.client.R;
import com.lh.sms.client.data.SqlData;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.ResultCodeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.constant.UiConstant;
import com.lh.sms.client.ui.person.user.enums.SmsTypeEnum;
import com.lh.sms.client.ui.util.UiUtil;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import okhttp3.FormBody;

public class ConfigPassword extends AppCompatActivity {

    private String TAG = "ConfigPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_password);
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.close_intent);
        textView.setText(intent.getStringExtra("title"));
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
        EditText editText = findViewById(R.id.input_password);
        editText.requestFocus();
        editText.setText("");
        //进入页面默认弹出键盘
        Timer timer = new Timer();//开启一个时间等待任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                alertKeyboard(null);
            }
        }, 1000);
        //检测输入内容改变登陆按钮样式
        EditText password = findViewById(R.id.input_password);
        EditText password2 = findViewById(R.id.input_password2);
        Button login = findViewById(R.id.person_config_pass_ok_button);
        boolean[] pass = new boolean[]{false,false};
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pass[0] = StringUtils.isNotBlank(password.getText().toString());
                UiUtil.buttonEnable(login,pass[0]&&pass[1]);
            }
        });
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pass[1] = StringUtils.isNotBlank(password2.getText().toString());
                UiUtil.buttonEnable(login,pass[0]&&pass[1]);
            }
        });
        //错误文字提示
        TextView errorText = findViewById(R.id.error_text);
        //确认按钮监听事件
        login.setOnClickListener(v->{
            errorText.setText("");
            if(!password.getText().toString().equals(password2.getText().toString())){
                errorText.setText("两次输入密码不一致");
            }
            if(password.getText().length()<6||password.getText().length()>20){
                errorText.setText(R.string.pass_format);
            }
            //请求服务器
            String phone = getIntent().getStringExtra("phone");
            String code = getIntent().getStringExtra("code");
            SmsTypeEnum smsTypeEnum = SmsTypeEnum.getEnum(getIntent().getStringExtra("type"));
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{this, ""};
            message.getData().putString(HandleMsg.METHOD_KEY, "showErrorMsg");
            handleMessage.sendMessage(message);
            FormBody param = new FormBody.Builder().add("phone", phone).add("code", code)
                    .add("password",password.getText().toString()).build();
            HttpClientUtil.post(smsTypeEnum.getApi(), param,
                    new HttpAsynResult(ConfigPassword.this) {
                        @Override
                        public void callback(HttpResult httpResult) {
                            if (!ResultCodeEnum.OK.getValue().equals(httpResult.getCode())) {
                                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                                message.obj = new Object[]{ConfigPassword.this, httpResult.getMsg()};
                                message.getData().putString(HandleMsg.METHOD_KEY, "showErrorMsg");
                                handleMessage.sendMessage(message);
                                return;
                            }
                            if(SmsTypeEnum.REGISTER.getValue().equals(smsTypeEnum.getValue())){
                                SqlData sqlData = ObjectFactory.get(SqlData.class);
                                sqlData.saveObject(DataConstant.KEY_USER_TK,httpResult.getData());
                                sqlData.saveObject(DataConstant.KEY_IS_LOGIN,YesNoEnum.YES.getValue());
                            }
                            Intent intent = new Intent(ConfigPassword.this, MainActivity.class);
                            intent.putExtras(getIntent());
                            intent.putExtra("code", code);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                            ObjectFactory.finish(PersonLogin.class);
                            ObjectFactory.finish(PersonRegister.class);
                        }
                    });
        });
    }
    /**
     * @do 显示错误消息
     * @author liuhua
     * @date 2020/4/26 7:49 PM
     */
    public void showErrorMsg(String msg) {
        TextView textView = findViewById(R.id.error_text);
        textView.setText(msg);
    }
    /**
     * 弹出键盘
     */
    public void alertKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//得到系统的输入方法服务
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
