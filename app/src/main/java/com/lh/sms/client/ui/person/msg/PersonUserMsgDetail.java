package com.lh.sms.client.ui.person.msg;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.R;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.work.msg.entity.Message;
import com.lh.sms.client.work.msg.enums.MessageStateEnum;
import com.lh.sms.client.work.msg.service.MessageService;

import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

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
        textView.setText(Html.fromHtml(message.getText().replace("\\n","\n")));
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
            button.setTextColor(Color.WHITE);
            button.setBackgroundResource(R.drawable.button_style_black);
            button.setOnClickListener(v->{
                Object[] objects = (Object[]) v.getTag();
                ObjectFactory.get(MessageService.class).updateState((Long)objects[0],Integer.valueOf(objects[1].toString()),PersonUserMsgDetail.this,() -> {
                    HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                    android.os.Message hm = android.os.Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                    hm.obj = new Object[]{PersonUserMsgDetail.this, objects[0]};
                    hm.getData().putString(handleMessage.METHOD_KEY, "refreshShow");
                    handleMessage.sendMessage(hm);
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
