package com.lh.sms.client.ui.person.sys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

import com.lh.sms.client.R;
import com.lh.sms.client.SmRunningService;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.work.sms.entity.SmsProvide;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class PersonSystemConfig extends AppCompatActivity {
    private Integer permissionsCode = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_sys_config);
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
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        //显示通知栏
        Integer state = sqlData.getObject(DataConstant.SHOW_NOTICE, Integer.class);
        Switch aSwitch = findViewById(R.id.person_sys_config_show_notice);
        aSwitch.setChecked(state==null||YesNoEnum.isYes(state));
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sqlData.saveObject(DataConstant.SHOW_NOTICE,isChecked?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
            if(isChecked) {
                Objects.requireNonNull(ObjectFactory.get(SmRunningService.class)).show();
            }else{
                Objects.requireNonNull(ObjectFactory.get(SmRunningService.class)).close();
            }
        });
        //自动检测版本
        state = sqlData.getObject(DataConstant.AUTO_UPDATE, Integer.class);
        aSwitch = findViewById(R.id.person_sys_config_auto_update);
        aSwitch.setChecked(state==null||YesNoEnum.isYes(state));
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sqlData.saveObject(DataConstant.AUTO_UPDATE,isChecked?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
