package com.lh.sms.client.ui.person.sms;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.constant.SystemConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.ExceptionCodeEnum;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.ResultCodeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.constant.UiConstant;
import com.lh.sms.client.ui.person.user.PersonFindPass;
import com.lh.sms.client.work.sms.entity.SmsProvide;
import com.lh.sms.client.work.sms.enums.SmStateEnum;
import com.lh.sms.client.work.sms.service.SmsProvideService;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.Person;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.FormBody;

public class PersonSmsConfig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_sms_config);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private boolean isLocal = true;
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(layout -> {
            //刷新注册状态
            layout.finishRefresh(1000,true,true);
            ObjectFactory.get(SmsProvideService.class).refreshSm(() -> {
                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                message.obj = new Object[]{PersonSmsConfig.this};
                message.getData().putString(handleMessage.METHOD_KEY,isLocal? "showLocalSimInfo":"showAllSimInfo");
                handleMessage.sendMessage(message);
            });
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            layout.setNoMoreData(true);
        });
        //tab切换
        TextView local = findViewById(R.id.person_sms_config_local);
        TextView all = findViewById(R.id.person_sms_config_all);
        showLocalSimInfo();
        isLocal = true;
        local.setOnClickListener(v->{
            local.setBackgroundColor(Color.WHITE);
           all.setBackgroundResource(R.color.gray);
           //显示本机
            showLocalSimInfo();
            isLocal = true;
        });
        all.setOnClickListener(v->{
            all.setBackgroundColor(Color.WHITE);
            local.setBackgroundResource(R.color.gray);
            showAllSimInfo();
            isLocal = false;
        });
        //刷新注册状态
        ObjectFactory.get(SmsProvideService.class).refreshSm(() -> {
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{PersonSmsConfig.this};
            message.getData().putString(handleMessage.METHOD_KEY, "showLocalSimInfo");
            handleMessage.sendMessage(message);
        });

    }
    /**
     * @do 显示本机sim卡信息
     * @author liuhua
     * @date 2020/4/28 10:45 PM
     */
    public void showLocalSimInfo(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, UiConstant.NO_READ_PHONE_STATE_PERMISSION, Toast.LENGTH_LONG).show();
            return;
        }
        SubscriptionManager sManager = (SubscriptionManager) this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> mList = sManager.getActiveSubscriptionInfoList();
        if (mList == null || mList.size() < 1) {
            Toast.makeText(this,UiConstant.NO_CHECK_PHONE_INFO, Toast.LENGTH_LONG).show();
            return;
        }
        ListView listView = findViewById(R.id.person_sms_config_list);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = PersonSmsConfig.this.getLayoutInflater();
                View view = convertView==null?inflater.inflate(R.layout.sms_config_list_item, null):convertView;
                SubscriptionInfo subscriptionInfo = mList.get(position);
                TextView textView = view.findViewById(R.id.list_item_1);
                textView.setText(String.format("SM卡%d",position+1));
                textView = view.findViewById(R.id.list_item_2);
                textView.setText(subscriptionInfo.getIccId());
                Button button= view.findViewById(R.id.list_item_3);
                button.setText(SmStateEnum.NO_REGISTER.getName());
                button.setTag(SmStateEnum.NO_REGISTER.getValue());
                SmsProvide smsProvide = ObjectFactory.get(SmsProvideService.class).getSmsProvide(subscriptionInfo.getIccId());
                if(smsProvide!=null) {
                    if (SmStateEnum.APPLYING.getValue().equals(smsProvide.getRegisterState())) {
                        button.setText(SmStateEnum.APPLYING.getName());
                        button.setTag(SmStateEnum.APPLYING.getValue());
                    } else if (SmStateEnum.REGISTER.getValue().equals(smsProvide.getRegisterState())) {
                        button.setText(SmStateEnum.REGISTER.getName());
                        button.setTag(SmStateEnum.REGISTER.getValue());
                    }
                }
                button.setOnClickListener(v->{
                    if(SmStateEnum.APPLYING.getValue().equals(v.getTag())){
                        //申请中
                        register(subscriptionInfo.getIccId(),YesNoEnum.YES.getValue(),0);
                    }else if(SmStateEnum.REGISTER.getValue().equals(v.getTag())){
                        //已注册,更新资料
                        Intent intent=new Intent(PersonSmsConfig.this, PersonSmsConfigDetail.class);
                        intent.putExtra("iccId",smsProvide.getIccId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }else{
                        //注册
                        register(subscriptionInfo.getIccId(),YesNoEnum.NO.getValue(),0);
                    }
                });
                return view;
            }
        });
    }
    /**
     * @do 显示全部sim卡信息
     * @author liuhua
     * @date 2020/4/28 10:45 PM
     */
    public void showAllSimInfo(){
        List<SmsProvide> smsProvides = ObjectFactory.get(SqlData.class).listObject(TablesEnum.SM_LIST.getTable(), SmsProvide.class);
        if(smsProvides==null||smsProvides.isEmpty()){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            smsProvides.removeIf(e->!SmStateEnum.REGISTER.getValue().equals(e.getRegisterState()));
        }else{
            for (int i = smsProvides.size() - 1; i >= 0; i--) {
                if(!SmStateEnum.REGISTER.getValue().equals(smsProvides.get(i).getRegisterState())){
                    smsProvides.remove(i);
                }
            }
        }
        if(smsProvides.isEmpty()){
            return;
        }
        ListView listView = findViewById(R.id.person_sms_config_list);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return smsProvides.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = PersonSmsConfig.this.getLayoutInflater();
                View view = convertView==null?inflater.inflate(R.layout.sms_config_list_item, null):convertView;
                SmsProvide smsProvide = smsProvides.get(position);
                TextView textView = view.findViewById(R.id.list_item_1);
                textView.setText("SM卡");
                textView = view.findViewById(R.id.list_item_2);
                textView.setText(smsProvide.getIccId());
                Button button= view.findViewById(R.id.list_item_3);
                button.setText(SmStateEnum.REGISTER.getName());
                button.setTag(SmStateEnum.REGISTER.getValue());
                button.setOnClickListener(v->{
                    //查看SM卡详情
                    Intent intent=new Intent(PersonSmsConfig.this, PersonSmsConfigDetail.class);
                    intent.putExtra("iccId",smsProvide.getIccId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                });
                return view;
            }
        });
    }
    /**
     * @do 注册SM卡
     * @author liuhua
     * @date 2020/5/9 10:36 PM
     */
    public void register(String iccId,Integer sure,Integer monthMax){
        FormBody.Builder param = new FormBody.Builder().add("iccId",iccId).add("sure",sure.toString());
        if(monthMax!=null){
            param.add("monthMax",monthMax.toString());
        }
        HttpClientUtil.post(ApiConstant.PROVIDE_UPDATE, param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().login(true).context(PersonSmsConfig.this)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                        Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                        if(ExceptionCodeEnum.ICC_ID_BIND.getValue().equals(httpResult.getCode())){
                            message.obj = new Object[]{PersonSmsConfig.this, iccId};
                            message.getData().putString(handleMessage.METHOD_KEY, "showBindWarn");
                            handleMessage.sendMessage(message);
                        }else if(!ResultCodeEnum.OK.getValue().equals(httpResult.getCode())){
                            AlertUtil.alertError(PersonSmsConfig.this,httpResult.getMsg());
                        }else {
                            SmsProvide smsProvide = new SmsProvide();
                            smsProvide.setIccId(iccId);
                            if ((boolean) httpResult.getData()) {
                                AlertUtil.toast(PersonSmsConfig.this, "操作成功", Toast.LENGTH_SHORT);
                                //保存已注册
                                smsProvide.setRegisterState(SmStateEnum.REGISTER.getValue());
                                ObjectFactory.get(SmsProvideService.class).cacheSmsProvide(smsProvide);
                                message.obj = new Object[]{PersonSmsConfig.this};
                                message.getData().putString(handleMessage.METHOD_KEY, "showLocalSimInfo");
                                handleMessage.sendMessage(message);
                            } else {
                                //保存申请中
                                smsProvide.setRegisterState(SmStateEnum.APPLYING.getValue());
                                ObjectFactory.get(SmsProvideService.class).cacheSmsProvide(smsProvide);
                                AlertUtil.toast(PersonSmsConfig.this, "正在向绑定者申请,请耐心等待申请结果", Toast.LENGTH_LONG);
                                message.obj = new Object[]{PersonSmsConfig.this};
                                message.getData().putString(handleMessage.METHOD_KEY, "showLocalSimInfo");
                                handleMessage.sendMessage(message);
                            }
                        }
                    }
                });
    }
    /**
     * @do 显示绑定警告
     * @author liuhua
     * @date 2020/5/9 11:12 PM
     */
    public void showBindWarn(String iccId){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PersonSmsConfig.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("SM卡正在被其它账户使用")
                .setContentText("SM卡已经被其它账户绑定,点击[确认]向绑定者申请转移使用权限(SM卡在60天内有活动)");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmText("确认");
        sweetAlertDialog.setCancelText("取消");
        sweetAlertDialog.setConfirmClickListener(v->{
            sweetAlertDialog.cancel();
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{PersonSmsConfig.this, iccId, YesNoEnum.YES.getValue(),0};
            message.getData().putString(handleMessage.METHOD_KEY, "register");
            handleMessage.sendMessage(message);
        });
        AlertUtil.alertOther(PersonSmsConfig.this,sweetAlertDialog);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isLocal){
            showLocalSimInfo();
        }else{
            showAllSimInfo();
        }

    }
}
