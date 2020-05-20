package com.lh.sms.client.ui.person.balance;

import android.content.Intent;
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
import com.lh.sms.client.work.wallet.entity.WalletDetail;
import com.lh.sms.client.work.wallet.enums.MoneyTypeEnum;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;

public class PersonBalanceDetail extends AppCompatActivity {
    //选择类型弹窗
    SelectDialog selectDialog = null;
    private List<WalletDetail> walletDetails = new ArrayList<>();
    private static final String TAG = "TransactionRecord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_balance_detail);
        //绑定事件
        bindEvent();
        List<String[]> param = new ArrayList<>();
        param.add(new String[]{"全部",""});
        param.add(new String[]{"收入","1"});
        param.add(new String[]{"支出","-1"});
        selectDialog = new SelectDialog(this,param);
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private BaseAdapter baseAdapter = null;
    private void bindEvent() {
        //退出
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        //打开选择
        findViewById(R.id.person_balance_transaction_record_select_type).setOnClickListener(v->{
            selectDialog.show();
        });
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return walletDetails.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = PersonBalanceDetail.this.getLayoutInflater();
                View view = convertView == null ? inflater.inflate(R.layout.balance_detail_list_item, null) : convertView;
                WalletDetail walletDetail = walletDetails.get(position);
                TextView textView = view.findViewById(R.id.list_item_title);
                textView.setText(MoneyTypeEnum.getNotice(walletDetail.getType()));
                textView = view.findViewById(R.id.list_item_text);
                textView.setText(String.format("%s%s",walletDetail.getOper()>0?"+":"-",walletDetail.getMoney().toString()));
                textView = view.findViewById(R.id.list_item_time);
                LocalDateTime localDateTime = LocalDateTime.fromDateFields(new Date(walletDetail.getCreateDate()));
                if(localDateTime.toLocalDate().equals(LocalDate.now())){
                    textView.setText(localDateTime.toString("今天HH:mm:ss"));
                }else if(localDateTime.toLocalDate().equals(LocalDate.now().minusDays(1))){
                    textView.setText(localDateTime.toString("昨天HH:mm:ss"));
                }else if(localDateTime.toLocalDate().equals(LocalDate.now().minusDays(2))){
                    textView.setText(localDateTime.toString("前天HH:mm:ss"));
                }else{
                    textView.setText(localDateTime.toString("yyyy-MM-dd HH:mm"));
                }
                view.setOnClickListener(v->{
                    //查看消息详情
                    Intent intent=new Intent(PersonBalanceDetail.this, PersonBalanceDetailDetail.class);
                    intent.putExtra("id",walletDetail.getId().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                });
                return view;
            }
        };
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(layout -> {
            getIntent().putExtra("page", 0);
            initData(layout);
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            layout.finishLoadMore();
            initData(layout);
        });
        ListView listView = findViewById(R.id.person_balance_detail_list);
        listView.setAdapter(baseAdapter);
        //显示数据
        initData(null);
    }
    /**
     * @do 刷新list显示
     * @author liuhua
     * @date 2020/5/10 9:03 PM
     */
    public void refreshList(){
        baseAdapter.notifyDataSetChanged();
    }
    /**
     * @do 初始化数据
     * @author liuhua
     * @date 2020/5/10 5:15 PM
     */
    private void initData(RefreshLayout layout){
        int page = getIntent().getIntExtra("page", 0);
        int pages = getIntent().getIntExtra("pages", 1);
        String oper = getIntent().getStringExtra("oper");
        if(pages<1){
            pages = 1;
        }
        if(page>=pages){
            //全部加载完毕
            if(layout!=null)
                layout.setNoMoreData(true);
            return;
        }
        //先从本地加载
        page++;
        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
        List<WalletDetail> list = ObjectFactory.get(SqlData.class).listObject(TablesEnum.WALLET_DETAIL_LIST.getTable(), WalletDetail.class, page, "`key` desc");
        if(page==1){
            walletDetails.clear();
        }
        Integer operInt = StringUtils.isBlank(oper)?null:Integer.valueOf(oper);
        for (WalletDetail walletDetail : list) {
            if(operInt!=null&&!walletDetail.getOper().equals(operInt)){
                continue;
            }
            if (!walletDetails.contains(walletDetail)) {
                walletDetails.add(walletDetail);
            }
        }
        Collections.sort(walletDetails);
        android.os.Message message = android.os.Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
        message.obj = new Object[]{PersonBalanceDetail.this};
        message.getData().putString(handleMessage.METHOD_KEY, "refreshList");
        handleMessage.sendMessage(message);
        if(layout!=null) {
            //刷新完成
            layout.finishRefresh(true);
            //加载完成
            layout.finishLoadMore(true);//传入false表示加载失败
        }
        //再从服务器请求并刷新
        FormBody.Builder param = new FormBody.Builder().add("page",String.valueOf(page)).add("rows","20");
        if(StringUtils.isNotBlank(oper)){
            param.add("oper",oper);
        }
        HttpClientUtil.post(ApiConstant.USER_WALLET_DETAIL,param.build(),
                new HttpAsynResult(HttpAsynResult.Config.builder().animation(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        if(!ResultCodeEnum.OK.getValue().equals(httpResult.getCode())){
                            if(layout!=null) {
                                layout.finishLoadMore(false);//传入false表示加载失败
                            }
                            return;
                        }
                        SqlData sqlData = ObjectFactory.get(SqlData.class);
                        if(layout==null){
                            walletDetails.clear();
                        }else{
                            layout.finishLoadMore(true);//传入false表示加载失败
                        }
                        JSONObject jsonObject = httpResult.getJSONObject();
                        int page = jsonObject.getIntValue("page");
                        getIntent().putExtra("pages",jsonObject.getIntValue("pages"));
                        getIntent().putExtra("page",page);
                        JSONArray rows = jsonObject.getJSONArray("rows");
                        if(page==1){
                            walletDetails.clear();
                            if(rows.size()<20){
                                //不足分页,清除本地所有
                                sqlData.deleteAll(TablesEnum.WALLET_DETAIL_LIST.getTable());
                            }
                        }
                        for (int i = 0; i < rows.size(); i++) {
                            WalletDetail walletDetail = rows.getObject(i, WalletDetail.class);
                            if (!walletDetails.contains(walletDetail)) {
                                walletDetails.add(walletDetail);
                            }
                            sqlData.saveObject(TablesEnum.WALLET_DETAIL_LIST.getTable(), walletDetail.getId().toString(), walletDetail);
                        }
                        Collections.sort(walletDetails);
                        android.os.Message message = android.os.Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                        message.obj = new Object[]{PersonBalanceDetail.this};
                        message.getData().putString(handleMessage.METHOD_KEY, "refreshList");
                        handleMessage.sendMessage(message);
                    }
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
        getIntent().putExtra("oper",type);
        getIntent().putExtra("page", 0);
        selectDialog.cancel();
        initData(null);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
