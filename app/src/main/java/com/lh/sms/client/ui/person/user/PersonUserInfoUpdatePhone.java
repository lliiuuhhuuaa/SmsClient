package com.lh.sms.client.ui.person.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.entity.ThreadCallback;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.ui.person.user.enums.SmsTypeEnum;
import com.lh.sms.client.work.user.entity.UserInfoByUpdate;
import com.lh.sms.client.work.user.service.UserService;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PersonUserInfoUpdatePhone extends AppCompatActivity {
    private final Integer requestCode = 34;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_user_info_update_phone);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    UserInfoByUpdate userInfoByUpdate = new UserInfoByUpdate();
    private void bindEvent() {
        TextView textView = findViewById(R.id.close_intent);
        textView.setOnClickListener(v->{
            finish();
        });
        String phone = getIntent().getStringExtra("phone");
        int step = getIntent().getIntExtra("step", 1);
        Button button = findViewById(R.id.update_button);
        EditText editText = findViewById(R.id.person_user_info_phone_input);
        button.setOnClickListener(v->{
            //检查原手机号是否正确
            String phoneText = editText.getText().toString();
            if(!phoneText.matches("^1[0-9]{10}$")){
                AlertUtil.toast(this,"手机号码错误", Toast.LENGTH_SHORT);
                return;
            }
            if(step==1) {
                ObjectFactory.get(UserService.class).sendCode(phoneText, SmsTypeEnum.VERIFY_OLD.getValue(), o -> {
                    Intent intent=new Intent(this, VerifySmsCode.class);
                    intent.putExtra("title","验证原手机号");
                    intent.putExtra("phone",phoneText);
                    intent.putExtra("type",SmsTypeEnum.VERIFY_OLD.getValue());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent,requestCode);
                });
            }else if(step==2){
                ObjectFactory.get(UserService.class).sendCode(phoneText, SmsTypeEnum.VERIFY_NEW.getValue(), o -> {
                    userInfoByUpdate.setPhone(phoneText);
                    Intent intent=new Intent(this, VerifySmsCode.class);
                    intent.putExtra("title","验证原手机号");
                    intent.putExtra("phone",phoneText);
                    intent.putExtra("type",SmsTypeEnum.VERIFY_NEW.getValue());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent,requestCode);
                });
            }else{
                //请求修改
                ObjectFactory.get(UserService.class).updateUserInfo(userInfoByUpdate, o -> {
                    finish();
                });
            }
        });
        if(step==1) {
            textView.setText("验证原手机号");
            button.setText("验证手机号");
            if (phone != null) {
                editText.setHint("请输入原手机号:"+phone);
            }
        }else if(step==2){
            textView.setText("验证新手机号");
            button.setText("更换手机号");
            editText.setHint("请输入新手机号");
            editText.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode==requestCode){
            if(data==null){
                return;
            }
           String code = data.getStringExtra("code");
           String type = data.getStringExtra("type");
           if(code==null||type==null){
               return;
           }
            if(SmsTypeEnum.VERIFY_OLD.getValue().equals(type)){
                getIntent().putExtra("step", 2);
                userInfoByUpdate.setOldCode(code);
                bindEvent();
                return;
            }
            if(SmsTypeEnum.VERIFY_NEW.getValue().equals(type)){
                getIntent().putExtra("step", 3);
                userInfoByUpdate.setNewCode(code);
                bindEvent();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().getIntExtra("step", 1)==3) {
            //请求修改
            ObjectFactory.get(UserService.class).updateUserInfo(userInfoByUpdate, o -> {
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
