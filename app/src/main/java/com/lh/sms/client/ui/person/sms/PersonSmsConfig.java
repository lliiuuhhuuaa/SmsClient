package com.lh.sms.client.ui.person.sms;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.ui.constant.UiConstant;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;

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
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        //tab切换
        TextView local = findViewById(R.id.person_sms_config_local);
        TextView all = findViewById(R.id.person_sms_config_all);
        showLocalSimInfo();
        local.setOnClickListener(v->{
            local.setBackgroundResource(R.color.colorWhite);
           all.setBackgroundResource(R.color.colorLine);
           //显示本机
            showLocalSimInfo();
        });
        all.setOnClickListener(v->{
            all.setBackgroundResource(R.color.colorWhite);
            local.setBackgroundResource(R.color.colorLine);
        });

    }
    /**
     * @do 显示本机sim卡信息
     * @author liuhua
     * @date 2020/4/28 10:45 PM
     */
    private void showLocalSimInfo(){
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
                textView.setText(String.format("卡%d",position+1));
                textView = view.findViewById(R.id.list_item_2);
                textView.setText(subscriptionInfo.getIccId());
                return view;
            }
        });
    }
}
