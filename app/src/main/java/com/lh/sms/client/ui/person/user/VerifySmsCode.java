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
import android.widget.EditText;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.ResultCodeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.ui.constant.UiConstant;

import org.apache.commons.lang3.RandomUtils;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import okhttp3.FormBody;

public class VerifySmsCode extends AppCompatActivity {

    private String TAG = "VerifySmsCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_sms_code);
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.close_intent);
        textView.setText(intent.getStringExtra("title"));
        textView = findViewById(R.id.text_notice);
        SpannableString span = new SpannableString(String.format("我们已向%s发送验证码短信,请查看短信并输入验证码", intent.getStringExtra("phone")));
//        手机号粗体
        span.setSpan(new StyleSpan(Typeface.BOLD), 4, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(span);
        //绑定事件
        bindEvent();
    }

    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private TextView[] codeText = new TextView[4];

    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v -> {
            finish();
        });
        //重新发送验证码
        View viewById = findViewById(R.id.resend_code);
        viewById.setOnClickListener(v -> {
            v.setEnabled(false);
            String phone = getIntent().getStringExtra("phone");
            String type = getIntent().getStringExtra("type");
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{this, ""};
            message.getData().putString(HandleMsg.METHOD_KEY, "showErrorMsg");
            handleMessage.sendMessage(message);
            //请求后台发送验证码
            FormBody param = new FormBody.Builder().add("phone", phone).add("type", type).build();
            HttpClientUtil.post("/show/sms/sendSmsCode", param,
                new HttpAsynResult(VerifySmsCode.this) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        if (!ResultCodeEnum.OK.getValue().equals(httpResult.getCode())) {
                            ((Object[])message.obj)[1] = httpResult.getMsg();
                            handleMessage.sendMessage(message);
                            return;
                        }
                        smsCodeTime((AppCompatTextView) v, RandomUtils.nextInt());
                    }
                });
        });
        smsCodeTime((AppCompatTextView) viewById, RandomUtils.nextInt());
        codeText[0] = findViewById(R.id.code1);
        codeText[1] = findViewById(R.id.code2);
        codeText[2] = findViewById(R.id.code3);
        codeText[3] = findViewById(R.id.code4);
        EditText editText = findViewById(R.id.code_input);
        editText.requestFocus();
        editText.addTextChangedListener(new TextChangeListener(editText));
        editText.setText("");
        //进入页面默认弹出键盘
        Timer timer = new Timer();//开启一个时间等待任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                alertKeyboard(null);
            }
        }, 1000);

    }

    /**
     * 弹出键盘
     */
    public void alertKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//得到系统的输入方法服务
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * @author liuhua
     * @do 文本变更事件
     * @date 2020/4/23 8:17 PM
     */
    class TextChangeListener implements TextWatcher {
        private EditText editText;

        public TextChangeListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //默认清空
            for (TextView textView : codeText) {
                textView.setText("");
                textView.setBackgroundResource(R.drawable.edit_solid_4_gray);
            }
            //赋值
            for (int i = 0; i < s.length(); i++) {
                codeText[i].setText(String.valueOf(s.charAt(i)));
            }
            //改变颜色
            if (s.length() < codeText.length) {
                codeText[s.length()].setBackgroundResource(R.drawable.edit_solid_4_bule);
            }
            if (s.toString().matches("^[0-9]{4}$")) {
                //检查输入完成
                inputDone(s.toString());
            }
        }
    }

    /**
     * 输入完成
     */
    private void inputDone(String code) {
        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
        Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
        message.obj = new Object[]{this, ""};
        message.getData().putString(HandleMsg.METHOD_KEY, "showErrorMsg");
        //请求后台发送验证码
        String phone = getIntent().getStringExtra("phone");
        FormBody param = new FormBody.Builder().add("phone", phone).add("code", code).build();
        HttpClientUtil.post("/show/sms/verifySmsCode", param,
                new HttpAsynResult(VerifySmsCode.this) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        if (!ResultCodeEnum.OK.getValue().equals(httpResult.getCode()) || Boolean.FALSE.equals(httpResult.getData())) {
                            ((Object[])message.obj)[1] = Boolean.FALSE.equals(httpResult.getData()) ? "验证码错误" : httpResult.getMsg();
                            handleMessage.sendMessage(message);
                            return;
                        }
                        Intent intent = new Intent(VerifySmsCode.this, ConfigPassword.class);
                        intent.putExtras(getIntent());
                        intent.putExtra("code", code);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    /**
     * @do 显示错误消息
     * @author liuhua
     * @date 2020/4/26 7:49 PM
     */
    public void showErrorMsg(String msg) {
        TextView textView = findViewById(R.id.show_error_msg);
        textView.setText(msg);
    }

    /**
     * @do 验证码倒计时
     * @author liuhua
     * @date 2020/3/21 1:40 PM
     */
    public void smsCodeTime(AppCompatTextView textView, Integer state) {
        //防止时间丢失
        Intent intent = getIntent();
        int time = intent.getIntExtra(UiConstant.SMS_SEND_TIME_KEY, UiConstant.DEFAULT_TIME);
        if (time < 1) {
            textView.setText(UiConstant.SMS_RE_SEND_TEXT);
            textView.setEnabled(true);
            intent.removeExtra(UiConstant.SMS_SEND_STATE_KEY);
            intent.putExtra(UiConstant.SMS_SEND_TIME_KEY, UiConstant.DEFAULT_TIME);
            return;
        }
        textView.setText(String.format(UiConstant.SMS_SEND_TEXT, time));
        if (state.equals(intent.getIntExtra(UiConstant.SMS_SEND_STATE_KEY, state))) {
            intent.putExtra(UiConstant.SMS_SEND_TIME_KEY, time - 1);
            intent.putExtra(UiConstant.SMS_SEND_STATE_KEY, state);
        }
        ThreadPool.schedule(() -> {
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{this, textView, state};
            message.getData().putString(handleMessage.METHOD_KEY, "smsCodeTime");
            handleMessage.sendMessage(message);
        },1);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
