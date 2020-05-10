package com.lh.sms.client.ui.person.balance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.ui.dialog.person.balance.SelectDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class PersonBalanceTransactionRecord extends AppCompatActivity {
    //选择类型弹窗
    SelectDialog selectDialog = null;
    private static final String TAG = "TransactionRecord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_balance_transaction_record);
        //绑定事件
        bindEvent();
        List<String[]> param = new ArrayList<>();
        param.add(new String[]{"全部",""});
        param.add(new String[]{"收入","plus"});
        param.add(new String[]{"支出","minus"});
        selectDialog = new SelectDialog(this,param);
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
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(layout -> {
            layout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            layout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
        });
        //打开选择
        findViewById(R.id.person_balance_transaction_record_select_type).setOnClickListener(v->{
            selectDialog.show();
        });
    }

    /**
     * 选择类型
     * @param view
     */
    public void searchByType(View view) {
        TextView textView = (TextView) view;
        TextView viewById = findViewById(R.id.person_balance_transaction_record_type);
        viewById.setText(textView.getText());
        String type = (String) view.getTag();
        viewById.setTag(type);
        Log.d(TAG, "searchByType: "+type);
        selectDialog.cancel();
    }
}
