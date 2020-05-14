package com.lh.sms.client.ui.person;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.MainActivity;
import com.lh.sms.client.R;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.ui.constant.UiConstant;
import com.lh.sms.client.ui.person.app.PersonAppConfig;
import com.lh.sms.client.ui.person.balance.PersonBalance;
import com.lh.sms.client.ui.person.bill.PersonBillRecord;
import com.lh.sms.client.ui.person.sms.PersonSmsConfig;
import com.lh.sms.client.ui.person.template.PersonTemplateConfig;
import com.lh.sms.client.ui.person.user.PersonLogin;
import com.lh.sms.client.ui.person.msg.PersonUserMsg;
import com.lh.sms.client.work.user.entity.UserInfo;
import com.lh.sms.client.work.user.service.UserService;

import java.math.BigDecimal;

public class PersonFragment extends Fragment {
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person, container, false);
        this.root = root;
        ObjectFactory.push(this);
        //绑定activity中转事件
        bindIntentEvent();
        return root;
    }

    /**
     * @do 绑定跳转事件
     * @author liuhua
     * @date 2020/4/16 7:54 PM
     */
    private void bindIntentEvent() {
        //余额
        root.findViewById(R.id.person_balance_menu_item).setOnClickListener(v -> {
            Intent intent=new Intent(root.getContext(), PersonBalance.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //账单
        root.findViewById(R.id.person_bill_menu_item).setOnClickListener(v -> {
            Intent intent=new Intent(root.getContext(), PersonBillRecord.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //短信服务配置
        root.findViewById(R.id.person_sms_config_item).setOnClickListener(v -> {
            Intent intent=new Intent(root.getContext(), PersonSmsConfig.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //用户
        root.findViewById(R.id.person_user_info).setOnClickListener(v -> {
            //判断是否已登陆
            Intent intent=null;
            if(!YesNoEnum.YES.getValue().equals(ObjectFactory.get(SqlData.class).getObject(DataConstant.KEY_IS_LOGIN, Integer.class))){
                //未登陆
                intent = new Intent(root.getContext(), PersonLogin.class);
            }else{
                intent = new Intent(root.getContext(), PersonSmsConfig.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //消息
        root.findViewById(R.id.person_user_msg).setOnClickListener(v->{
            Intent intent = new Intent(root.getContext(), PersonUserMsg.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //应用设置
        root.findViewById(R.id.person_app_config).setOnClickListener(v->{
            Intent intent = new Intent(root.getContext(), PersonAppConfig.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //模板设置
        root.findViewById(R.id.person_template_config).setOnClickListener(v->{
            Intent intent = new Intent(root.getContext(), PersonTemplateConfig.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //退出
        root.findViewById(R.id.person_exit).setOnClickListener(v->{
            ObjectFactory.get(UserService.class).unLogin();
            clearUserInfo();
        });
    }
    /**
     * @do 初始化用户数据
     * @author liuhua
     * @date 2020/4/26 10:04 PM
     */
    private void initData() {
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        if(YesNoEnum.YES.getValue().equals(sqlData.getObject(DataConstant.KEY_IS_LOGIN, Integer.class))){
            //已登陆
            //获取用户信息
            showUserInfo();
            //获取用户余额
            showUserBalance();
            //显示未读
            showUnRead();
            //30秒只请求一次
            Intent intent = ObjectFactory.get(MainActivity.class).getIntent();
            long time = intent.getLongExtra(UiConstant.TIME_QUICK_TAP, 0);
            if(time+30000>System.currentTimeMillis()) {
                return;
            }
            intent.putExtra(UiConstant.TIME_QUICK_TAP,System.currentTimeMillis());
            ThreadPool.createNewThread(() -> {
                HttpClientUtil.post(ApiConstant.USER_GET_INFO,
                        new HttpAsynResult(HttpAsynResult.Config.builder().context(ObjectFactory.get(MainActivity.class)).onlyOk(true).alertError(true).animation(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        UserInfo info = (UserInfo) httpResult.getObject(UserInfo.class);
                        if(info==null){
                            return;
                        }
                        sqlData.saveObject(info);
                        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                        Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                        message.obj = new Object[]{PersonFragment.this};
                        message.getData().putString(HandleMsg.METHOD_KEY, "showUserInfo");
                        handleMessage.sendMessage(message);
                    }
                });
                HttpClientUtil.post(ApiConstant.USER_WALLET,
                        new HttpAsynResult(HttpAsynResult.Config.builder().context(ObjectFactory.get(MainActivity.class)).onlyOk(true).alertError(true).animation(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        JSONObject jsonObject = httpResult.getJSONObject();
                        if(jsonObject==null){
                            return;
                        }
                        BigDecimal balance = jsonObject.getBigDecimal("balance");
                        if(balance==null){
                            return;
                        }
                        sqlData.saveObject(DataConstant.KEY_USER_BALANCE,balance);
                        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                        Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                        message.obj = new Object[]{PersonFragment.this};
                        message.getData().putString(HandleMsg.METHOD_KEY, "showUserBalance");
                        handleMessage.sendMessage(message);
                    }
                });
                HttpClientUtil.post(ApiConstant.MSG_UN_READ_COUNT,
                        new HttpAsynResult(HttpAsynResult.Config.builder().context(ObjectFactory.get(MainActivity.class)).onlyOk(true).alertError(false).animation(false)) {
                            @Override
                            public void callback(HttpResult httpResult) {
                                Integer count = (Integer)httpResult.getData();
                                if(count==null){
                                    count = 0;
                                }
                                sqlData.saveObject(DataConstant.KEY_MSG_UN_READ_COUNT,count);
                                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                                message.obj = new Object[]{PersonFragment.this};
                                message.getData().putString(HandleMsg.METHOD_KEY, "showUnRead");
                                handleMessage.sendMessage(message);
                            }
                        });
            });
        }else{
            clearUserInfo();
        }
    }
    /**
     * @do 清除用户信息
     * @author liuhua
     * @date 2020/4/28 9:59 PM
     */
    public void clearUserInfo(){
        TextView textView = root.findViewById(R.id.person_user_nickname);
        textView.setText(R.string.click_login);
        textView = root.findViewById(R.id.person_user_phone);
        textView.setText(R.string.click_login_notice);
        textView =  root.findViewById(R.id.person_user_balance);
        textView.setText(R.string.default_money);
    }
    /**
     * @do 显示用户信息
     * @author liuhua
     * @date 2020/4/26 10:55 PM
     */
    public void showUserInfo(){
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        UserInfo userInfo = sqlData.getObject(UserInfo.class);
        if(userInfo!=null) {
            TextView textView = root.findViewById(R.id.person_user_nickname);
            textView.setText(userInfo.getNickname());
            textView = root.findViewById(R.id.person_user_phone);
            textView.setText(userInfo.getPhone());
        }
    }
    /**
     * @do 显示未读消息数
     * @author liuhua
     * @date 2020/4/26 10:55 PM
     */
    public void showUnRead(){
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        Integer count = sqlData.getObject(DataConstant.KEY_MSG_UN_READ_COUNT,Integer.class);
        TextView textView = root.findViewById(R.id.person_user_msg_un_read);
        if(count==null||count<1){
            textView.setVisibility(View.INVISIBLE);
        }else{
            if(count>99){
                count=99;
            }
            textView.setText(count.toString());
            textView.setVisibility(View.VISIBLE);
        }
    }
    /**
     * @do 显示用户余额
     * @author liuhua
     * @date 2020/4/26 10:55 PM
     */
    public void showUserBalance(){
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        //获取用户余额
        BigDecimal balance = sqlData.getObject(DataConstant.KEY_USER_BALANCE,BigDecimal.class);
        if(balance!=null) {
            TextView textView = root.findViewById(R.id.person_user_balance);
            textView.setText(balance.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //初始化数据
        initData();
    }

}
