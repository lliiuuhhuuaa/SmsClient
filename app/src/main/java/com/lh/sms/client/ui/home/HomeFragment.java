package com.lh.sms.client.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.R;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.work.sms.service.SmsProvideService;
import com.lh.sms.client.work.user.entity.UserInfo;

import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.math.RoundingMode;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private View root = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //初始化
        initEvent();
        //初始化主页数据
        showData();
        initData();
        return root;
    }
    /**
     * @do 显示主页数据
     * @author liuhua
     * @date 2020/6/6 10:05 AM
     */
    public void showData() {
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        BigDecimal money = sqlData.getObject("stat_totalEarnings", BigDecimal.class);
        TextView textView = null;
        if(money!=null) {
            textView = root.findViewById(R.id.home_total_earnings);
            textView.setText(money.setScale(3, RoundingMode.FLOOR).toString());
        }
        money = sqlData.getObject("stat_monthEarnings", BigDecimal.class);
        if(money!=null) {
            textView = root.findViewById(R.id.home_month_earnings);
            textView.setText(money.setScale(3, RoundingMode.FLOOR).toString());
        }
        money = sqlData.getObject("stat_totalExpense", BigDecimal.class);
        if(money!=null) {
            textView = root.findViewById(R.id.home_total_expense);
            textView.setText(money.setScale(3, RoundingMode.FLOOR).toString());
        }
        money = sqlData.getObject("stat_monthExpense", BigDecimal.class);
        if(money!=null) {
            textView = root.findViewById(R.id.home_month_expense);
            textView.setText(money.setScale(3, RoundingMode.FLOOR).toString());
        }
        JSONArray jsonArray =  sqlData.getObject("stat_sendCount", JSONArray.class);
        if(jsonArray!=null){
            int totalCount = 0;
            for (int i = jsonArray.size() - 1; i >= 0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(YesNoEnum.isYes(jsonObject.getInteger("usePublic"))) {
                    textView = root.findViewById(R.id.home_send_count_public);
                    Integer count = jsonObject.getInteger("count");
                    textView.setText(count.toString());
                    totalCount+=count;
                }else{
                    textView = root.findViewById(R.id.home_send_count_private);
                    Integer count = jsonObject.getInteger("count");
                    textView.setText(count.toString());
                    totalCount+=count;
                }
            }
            textView = root.findViewById(R.id.home_send_count);
            textView.setText(String.valueOf(totalCount));
        }
        jsonArray =  sqlData.getObject("stat_useCount", JSONArray.class);
        if(jsonArray!=null){
            int totalCount = 0;
            for (int i = jsonArray.size() - 1; i >= 0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(YesNoEnum.isYes(jsonObject.getInteger("usePublic"))) {
                    textView = root.findViewById(R.id.home_use_count_public);
                    Integer count = jsonObject.getInteger("count");
                    textView.setText(count.toString());
                    totalCount+=count;
                }else{
                    textView = root.findViewById(R.id.home_use_count_private);
                    Integer count = jsonObject.getInteger("count");
                    textView.setText(count.toString());
                    totalCount+=count;
                }
                textView = root.findViewById(R.id.home_use_count);
                textView.setText(String.valueOf(totalCount));
            }
        }
        LinearLayout linearLayout = root.findViewById(R.id.home_online_parent);
        //在线统计
        jsonArray =  sqlData.getObject("stat_onlineTime", JSONArray.class);
        if(jsonArray==null||jsonArray.isEmpty()){
            linearLayout.setVisibility(View.INVISIBLE);
        }else{
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout = root.findViewById(R.id.home_online_child);
            linearLayout.removeAllViews();
            Integer max = null;
            for (int i = jsonArray.size() - 1; i >= 0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                View view = LayoutInflater.from(this.getContext()).inflate(R.layout.home_online_item, null);
                TextView textView1 = view.findViewById(R.id.list_item_icc_id);
                textView1.setText(jsonObject.getString("iccId"));
                textView1 = view.findViewById(R.id.list_item_time);
                Long time = jsonObject.getLong("time");
                time = time/60;
                if(max==null){
                    max = time.intValue()+60;
                }
                textView1.setText(time+"分钟");
                ProgressBar progressBar = view.findViewById(R.id.list_item_progress);
                progressBar.setMax(max);
                progressBar.setProgress(time.intValue());
                linearLayout.addView(view);
            }
        }
    }
    /**
     * @do 初始化主页数据
     * @author liuhua
     * @date 2020/6/6 10:05 AM
     */
    private void initData() {
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        if(!YesNoEnum.YES.getValue().equals(sqlData.getObject(DataConstant.KEY_IS_LOGIN, Integer.class))){
            return;
        }
        ObjectFactory.get(SmsProvideService.class).refreshStat(()->{
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{HomeFragment.this};
            message.getData().putString(handleMessage.METHOD_KEY,"showData");
            handleMessage.sendMessage(message);
        });
    }
    /**
     * @do 初始化
     * @author liuhua
     * @date 2020/6/1 10:25 PM
     */
    private void initEvent() {
        LocalTime localTime = LocalTime.now();
        TextView textView = root.findViewById(R.id.home_title_time);
        int hour = localTime.getHourOfDay();
        if(hour>6&&hour<=12){
            textView.setText("早上好呀");
        }else if(hour>12&&hour<19){
            textView.setText("下午好呀");
        }else{
            textView.setText("晚上好呀");
        }
        //显示名称
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        UserInfo userInfo = sqlData.getObject(UserInfo.class);
        if(userInfo!=null) {
            textView = root.findViewById(R.id.home_title_name);
            textView.setText(userInfo.getNickname());
        }

    }
}
