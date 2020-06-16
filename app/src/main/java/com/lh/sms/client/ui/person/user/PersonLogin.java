package com.lh.sms.client.ui.person.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lh.sms.client.MainActivity;
import com.lh.sms.client.R;
import com.lh.sms.client.SmRunningService;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.entity.ThreadCallback;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.util.UiUtil;
import com.lh.sms.client.work.socket.service.SocketService;
import com.lh.sms.client.work.storage.util.ImageUtil;
import com.lh.sms.client.work.user.service.UserService;

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
                String text = accountEdit.getText().toString();
                pass[0] = StringUtils.isNotBlank(text);
                UiUtil.buttonEnable(login,pass[0]&&pass[1]);
                if(text.matches("^1[0-9]{10}$")){
                    //请求头像
                    ObjectFactory.get(UserService.class).getPhoto(text, (ThreadCallback<String>) s1 -> {
                        ImageUtil.loadImage(PersonLogin.this, s1, s2 -> {
                                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                                message.obj = new Object[]{PersonLogin.this, s2};
                                message.getData().putString(HandleMsg.METHOD_KEY, "showUrlImage");
                                handleMessage.sendMessage(message);
                        });
                    });
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
                UiUtil.buttonEnable(login,pass[0]&&pass[1]);
            }
        });
        login.setOnClickListener(v->{
            FormBody param = new FormBody.Builder().add("account",accountEdit.getText().toString() )
                    .add("password",passwordEdit.getText().toString()).build();
            HttpClientUtil.post(ApiConstant.USER_LOGIN, param,
                    new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).login(false)) {
                        @Override
                        public void callback(HttpResult httpResult) {
                            SqlData sqlData = ObjectFactory.get(SqlData.class);
                            sqlData.deleteAll();
                            sqlData.saveObject(DataConstant.KEY_USER_TK,httpResult.getData());
                            sqlData.saveObject(DataConstant.KEY_IS_LOGIN,YesNoEnum.YES.getValue());
                            //连接socket
                            Intent intent = new Intent(PersonLogin.this, SmRunningService.class);
                            startService(intent);
                            finish();
                        }
                    });
        });
    }
    /**
     * @do 显示头像
     * @author liuhua
     * @date 2020/6/9 7:40 PM
     */
    public void showUrlImage(Bitmap bitmap){
        if(bitmap!=null) {
            ImageView imageView = findViewById(R.id.person_user_photo);
            imageView.setImageBitmap(bitmap);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
