package com.lh.sms.client.ui.person.msg;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.R;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.ResultCodeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.dialog.person.balance.SelectDialog;
import com.lh.sms.client.work.msg.entity.Message;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

public class PersonUserMsgDetail extends AppCompatActivity {
    private static final String TAG = "PersonUserMsgDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_user_msg_detail);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        //退出
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        String id = getIntent().getStringExtra("id");
        if(id==null){
            return;
        }
        Message message = ObjectFactory.get(SqlData.class).getObject(TablesEnum.MSG_LIST.getTable(), id, Message.class);
        if(message==null){
            return;
        }
        TextView textView = findViewById(R.id.person_user_msg_detail_title);
        textView.setText(message.getTitle());
        textView = findViewById(R.id.person_user_msg_detail_text);
        textView.setText(message.getText().replace("\\n","\n"));
        textView = findViewById(R.id.person_user_msg_detail_time);
        textView.setText(LocalDateTime.fromDateFields(new Date(message.getCreateDate())).toString("yyyy-MM-dd HH:mm:ss"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
