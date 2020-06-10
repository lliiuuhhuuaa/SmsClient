package com.lh.sms.client.ui.person.user;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.work.user.entity.UserInfo;
import com.lh.sms.client.work.user.entity.UserInfoByUpdate;
import com.lh.sms.client.work.user.service.UserService;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

public class PersonUserInfoUpdate extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_user_info_update);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        TextView textView = findViewById(R.id.close_intent);
        textView.setOnClickListener(v-> finish());
        String nickname = null;
        EditText editText = findViewById(R.id.person_user_info_nickname_input);
        EditText oldPassword = findViewById(R.id.person_user_info_old_password_input);
        EditText newPassword = findViewById(R.id.person_user_info_new_password_input);
        EditText newPassword2 = findViewById(R.id.person_user_info_new_password2_input);
        final boolean pass = YesNoEnum.isYes(getIntent().getIntExtra("password", YesNoEnum.NO.getValue()));
        if(pass){
            textView.setText("修改密码");
            findViewById(R.id.person_user_info_nickname_item).setVisibility(TextView.GONE);
        }else{
            findViewById(R.id.person_user_info_old_password_item).setVisibility(TextView.GONE);
            findViewById(R.id.person_user_info_new_password_item).setVisibility(TextView.GONE);
            findViewById(R.id.person_user_info_new_password2_item).setVisibility(TextView.GONE);
            nickname = getIntent().getStringExtra("nickname");
            if(nickname!=null) {
                editText.setText(nickname);
            }
        }
        findViewById(R.id.update_button).setOnClickListener(v->{
            if(!pass) {
                String nick = getIntent().getStringExtra("nickname");
                if (nick != null && nick.equals(editText.getText().toString())) {
                    finish();
                    return;
                }
                if (StringUtils.isBlank(editText.getText())) {
                    AlertUtil.toast(this, "昵称不能为空", Toast.LENGTH_SHORT);
                    return;
                }
                UserInfoByUpdate userInfoByUpdate = new UserInfoByUpdate();
                userInfoByUpdate.setNickname(editText.getText().toString());
                ObjectFactory.get(UserService.class).updateUserInfo(userInfoByUpdate, o -> {
                    finish();
                });
                return;
            }
            if(StringUtils.isBlank(oldPassword.getText())){
                AlertUtil.toast(this, "旧密码不能为空", Toast.LENGTH_SHORT);
                return;
            }
            if(StringUtils.isBlank(newPassword.getText())||StringUtils.isBlank(newPassword2.getText())){
                AlertUtil.toast(this, "新密码不能为空", Toast.LENGTH_SHORT);
                return;
            }
            if(!newPassword.getText().toString().equals(newPassword2.getText().toString())){
                AlertUtil.toast(this, "两次新密码输入不一致", Toast.LENGTH_SHORT);
                return;
            }
            //修改密码
            HttpClientUtil.post(ApiConstant.UPDATE_LOGIN_PASSWORD,new FormBody.Builder().add("oldPassword",oldPassword.getText().toString())
                    .add("newPassword",newPassword.getText().toString()).build(),new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).login(true)){
                @Override
                public void callback(HttpResult httpResult) {
                    AlertUtil.toast("修改成功", Toast.LENGTH_SHORT);
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
