package com.lh.sms.client.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lh.sms.client.R;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.work.user.entity.UserInfo;

import org.joda.time.LocalTime;

public class HomeFragment extends Fragment {
    private View root = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //初始化
        initEvent();
        return root;
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
        //显示余额

    }
}
