package com.lh.sms.client.ui.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.ui.constant.UiConstant;

import org.apache.commons.lang3.RandomUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

public class PersonRegister2 extends AppCompatActivity {

    private String TAG = "PersonRegister2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_register_2);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private EditText code1Edit = null;
    private EditText code2Edit = null;
    private EditText code3Edit = null;
    private EditText code4Edit = null;
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        //重新发送验证码
        View viewById = findViewById(R.id.resend_code);
        viewById.setOnClickListener(v->{
            v.setEnabled(false);
            smsCodeTime((AppCompatTextView) v, RandomUtils.nextInt());
        });
        smsCodeTime((AppCompatTextView) viewById,RandomUtils.nextInt());
        code1Edit = findViewById(R.id.code1);
        code2Edit = findViewById(R.id.code2);
        code3Edit = findViewById(R.id.code3);
        code4Edit = findViewById(R.id.code4);

        code1Edit.setKeyListener(new CodeKeyListener());
        code2Edit.setKeyListener(new CodeKeyListener());
        code3Edit.setKeyListener(new CodeKeyListener());
        code4Edit.setKeyListener(new CodeKeyListener());
    }

    /**
     * 自定义key监听
     */
    class CodeKeyListener extends NumberKeyListener {
        @NonNull
        @Override
        protected char[] getAcceptedChars() {
            char numberChars[] ={'0'  , '1' ,'2' ,'3' , '4' , '5'  ,'6'  ,'7' ,  '8'  , '9'};
            return numberChars;
        }

        @Override
        public int getInputType() {
            return InputType.TYPE_CLASS_NUMBER;
        }

        @Override
        public boolean onKeyUp(View view, Editable content, int keyCode, KeyEvent event) {
            EditText editText = (EditText) view;
            //修改二次覆盖
            int nextFocusDownId;
            if(keyCode== KeyEvent.KEYCODE_DEL){
                nextFocusDownId = view.getNextFocusUpId();
                editText.setText("");
            }else {
                //1-0 code为8-17
                editText.setText(String.valueOf(keyCode - 7));
                nextFocusDownId = view.getNextFocusDownId();
            }
            //获取下一焦点或上一焦点
            if(nextFocusDownId>0){
                editText = findViewById(nextFocusDownId);
                editText.requestFocus();
            }
            //检查输入完成
            inputDone();
            return super.onKeyUp(view, content, keyCode, event);
        }

    }
    /**
     * 输入完成
     */
    private void inputDone(){
        StringBuilder sb = new StringBuilder();
        sb.append(code1Edit.getText().toString());
        sb.append(code2Edit.getText().toString());
        sb.append(code3Edit.getText().toString());
        sb.append(code4Edit.getText().toString());
        if(sb.length()==4){
            Log.d(TAG, "inputDone: "+sb.toString());
        }
    }
    /**
     * @do 验证码倒计时
     * @author liuhua
     * @date 2020/3/21 1:40 PM
     */
    public void smsCodeTime(AppCompatTextView textView,Integer state){
        //防止时间丢失
        Intent intent = getIntent();
        int time = intent.getIntExtra(UiConstant.SMS_SEND_TIME_KEY, UiConstant.DEFAULT_TIME);
        if(time<1){
            textView.setText(UiConstant.SMS_RE_SEND_TEXT);
            textView.setEnabled(true);
            intent.removeExtra(UiConstant.SMS_SEND_STATE_KEY);
            intent.putExtra(UiConstant.SMS_SEND_TIME_KEY,UiConstant.DEFAULT_TIME);
            return;
        }
        textView.setText(String.format(UiConstant.SMS_SEND_TEXT,time));
        if(state.equals(intent.getIntExtra(UiConstant.SMS_SEND_STATE_KEY,state))){
            intent.putExtra(UiConstant.SMS_SEND_TIME_KEY,time-1);
            intent.putExtra(UiConstant.SMS_SEND_STATE_KEY,state);
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{this,textView,state};
            message.getData().putString(handleMessage.METHOD_KEY,"smsCodeTime");
            handleMessage.sendMessage(message);
        },1000);
    }
}
