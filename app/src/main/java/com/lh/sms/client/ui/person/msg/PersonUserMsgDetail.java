package com.lh.sms.client.ui.person.msg;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextClassifier;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.R;
import com.lh.sms.client.data.constant.DataConstant;
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
import com.lh.sms.client.ui.person.sms.PersonSmsConfigDetail;
import com.lh.sms.client.work.msg.entity.Message;
import com.lh.sms.client.work.msg.enums.MessageStateEnum;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        LinearLayout linearLayout = findViewById(R.id.person_user_msg_detail_buttons);
        Map<Integer,String> buttons = JSONObject.parseObject(message.getButtons(), HashMap.class);
        if(!MessageStateEnum.WAIT.getValue().equals(message.getState())){
            //等待处理
            TextView tv = new TextView(this);
            tv.setText(String.format("你已选择了[ %s ]",buttons.get(message.getState())));
            tv.setTextSize(18);
            tv.setTypeface(null, Typeface.BOLD_ITALIC);
            linearLayout.addView(tv);
            return;
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20,5,20,5);
        for (Map.Entry<Integer, String> map : buttons.entrySet()) {
            Button button = new Button(PersonUserMsgDetail.this);
            button.setTextSize(16);
            button.setGravity(Gravity.CENTER);
            button.setText(map.getValue());
            button.getPaint().setFakeBoldText(true);
            button.setLayoutParams(lp);
            button.setTag(new Object[]{message.getId(),map.getKey()});
            button.setBackgroundResource(R.drawable.button_style_black);
            button.setOnClickListener(v->{
                Object[] objects = (Object[]) v.getTag();
                FormBody.Builder param = new FormBody.Builder().add("id",objects[0].toString()).add("state",objects[1].toString());
                HttpClientUtil.post(ApiConstant.MSG_UPDATE,param.build(),
                        new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).context(PersonUserMsgDetail.this)) {
                            @Override
                            public void callback(HttpResult httpResult) {
                                SqlData sqlData = ObjectFactory.get(SqlData.class);
                                Message saveMsg = sqlData.getObject(TablesEnum.MSG_LIST.getTable(), objects[0].toString(), Message.class);
                                saveMsg.setState(Integer.valueOf(objects[1].toString()));
                                sqlData.saveObject(TablesEnum.MSG_LIST.getTable(), objects[0].toString(), saveMsg);
                                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                                android.os.Message message = android.os.Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                                message.obj = new Object[]{PersonUserMsgDetail.this, saveMsg.getId()};
                                message.getData().putString(handleMessage.METHOD_KEY, "refreshShow");
                                handleMessage.sendMessage(message);
                                Integer count = sqlData.getObject(DataConstant.KEY_MSG_UN_READ_COUNT, Integer.class);
                                if(count!=null&&count>0) {
                                    sqlData.saveObject(DataConstant.KEY_MSG_UN_READ_COUNT, count-1);
                                }
                            }
                        });
            });
            linearLayout.addView(button);
        }
    }
    /**
     * @do 刷新显示
     * @author liuhua
     * @date 2020/5/11 7:42 PM
     */
    public void refreshShow(Long id){
        Message message = ObjectFactory.get(SqlData.class).getObject(TablesEnum.MSG_LIST.getTable(), id.toString(), Message.class);
        if(message==null){
            return;
        }
        LinearLayout linearLayout = findViewById(R.id.person_user_msg_detail_buttons);
        linearLayout.removeAllViews();
        Map<Integer,String> buttons = JSONObject.parseObject(message.getButtons(), HashMap.class);
        if(!MessageStateEnum.WAIT.getValue().equals(message.getState())){
            //等待处理
            TextView tv = new TextView(this);
            tv.setText(String.format("你已选择了[ %s ]",buttons.get(message.getState())));
            tv.setTextSize(18);
            tv.setTypeface(null, Typeface.BOLD_ITALIC);
            linearLayout.addView(tv);
            return;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
