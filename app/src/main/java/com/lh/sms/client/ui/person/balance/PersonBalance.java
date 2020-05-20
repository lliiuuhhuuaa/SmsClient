package com.lh.sms.client.ui.person.balance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lh.sms.client.R;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.work.wallet.service.WalletService;

import java.math.BigDecimal;

public class PersonBalance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_balance);
        //绑定事件
        bindEvent();
        //初始化数据
        initData();
    }

    /**
     * @do 初始化数据
     * @author liuhua
     * @date 2020/5/20 8:16 PM
     */
    private void initData() {
        //获取用户余额
        showUserBalance();
        //刷新余额
        ObjectFactory.get(WalletService.class).refreshWallet(()->{
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            android.os.Message message = android.os.Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{PersonBalance.this};
            message.getData().putString(HandleMsg.METHOD_KEY, "showUserBalance");
            handleMessage.sendMessage(message);
        });
    }

    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.person_balance_transaction_record).setOnClickListener(v -> {
            Intent intent = new Intent(this, PersonBalanceDetail.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

    }

    /**
     * @do 显示用户余额
     * @author liuhua
     * @date 2020/4/26 10:55 PM
     */
    public void showUserBalance() {
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        //获取用户余额
        BigDecimal balance = sqlData.getObject(DataConstant.KEY_USER_BALANCE, BigDecimal.class);
        if (balance != null) {
            TextView textView = findViewById(R.id.person_balance_user_balance);
            textView.setText(balance.toString());
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }

}
