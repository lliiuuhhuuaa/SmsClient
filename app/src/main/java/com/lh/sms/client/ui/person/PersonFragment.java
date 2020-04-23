package com.lh.sms.client.ui.person;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lh.sms.client.R;
import com.lh.sms.client.data.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.ui.person.balance.PersonBalance;
import com.lh.sms.client.ui.person.bill.PersonBillRecord;
import com.lh.sms.client.ui.person.sms.PersonSmsConfig;
import com.lh.sms.client.ui.person.user.PersonLogin;
import com.lh.sms.client.work.user.entity.UserInfo;

public class PersonFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_person, container, false);
        //绑定activity中转事件
        bindIntentEvent(root);
        return root;
    }
    /**
     * @do 绑定跳转事件
     * @author liuhua
     * @date 2020/4/16 7:54 PM
     */
    private void bindIntentEvent(View root) {
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
            UserInfo userInfo = ObjectFactory.get(SqlData.class).getObject(UserInfo.class);
            Intent intent=null;
            if(userInfo==null){
                //未登陆
                intent = new Intent(root.getContext(), PersonLogin.class);
            }else{
                intent = new Intent(root.getContext(), PersonSmsConfig.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }
}
