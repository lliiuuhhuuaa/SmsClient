package com.lh.sms.client.ui.person.sms;

import android.os.Bundle;
import android.widget.TextView;

import com.lh.sms.client.R;

import androidx.appcompat.app.AppCompatActivity;

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
        local.setOnClickListener(v->{
            local.setBackgroundResource(R.color.colorWhite);
           all.setBackgroundResource(R.color.colorLine);
        });
        all.setOnClickListener(v->{
            all.setBackgroundResource(R.color.colorWhite);
            local.setBackgroundResource(R.color.colorLine);
        });
    }
}
