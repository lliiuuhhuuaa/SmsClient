package com.lh.sms.client.ui.person.balance;

import android.os.Bundle;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.work.wallet.entity.WalletDetail;
import com.lh.sms.client.work.wallet.enums.MoneyTypeEnum;

import org.joda.time.LocalDateTime;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class PersonBalanceDetailDetail extends AppCompatActivity {
    private static final String TAG = "PersonBalanceDetailDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_balance_detail_detail);
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
        WalletDetail walletDetail = ObjectFactory.get(SqlData.class).getObject(TablesEnum.WALLET_DETAIL_LIST.getTable(), id, WalletDetail.class);
        if(walletDetail==null){
            return;
        }
        TextView textView = findViewById(R.id.person_balance_detail_type);
        textView.setText(MoneyTypeEnum.getNotice(walletDetail.getType()));
        textView = findViewById(R.id.person_balance_detail_money);
        textView.setText(String.format("%s%s",walletDetail.getOper()>0?"+":"-",walletDetail.getMoney().toString()));
        textView = findViewById(R.id.person_balance_detail_before);
        textView.setText(walletDetail.getBefore().toString());
        textView = findViewById(R.id.person_balance_detail_after);
        textView.setText(walletDetail.getAfter().toString());
        textView = findViewById(R.id.person_balance_detail_time);
        textView.setText(LocalDateTime.fromDateFields(new Date(walletDetail.getCreateDate())).toString("yyyy-MM-dd HH:mm:ss"));
        textView = findViewById(R.id.person_balance_detail_remark);
        textView.setText(walletDetail.getRemark());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
