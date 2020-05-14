package com.lh.sms.client.ui.person.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lh.sms.client.R;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.ui.person.msg.PersonUserMsgDetail;
import com.lh.sms.client.work.app.entity.AppConfig;
import com.lh.sms.client.work.app.service.AppConfigService;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @do 应用配置
 * @author liuhua
 * @date 2020/5/13 7:15 PM
 */
public class PersonAppConfig extends AppCompatActivity {
    private List<AppConfig> apps = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_app_config);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private BaseAdapter baseAdapter = null;
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(layout -> {
            //刷新注册状态
            layout.finishRefresh(1000,true,true);
            ObjectFactory.get(AppConfigService.class).refreshAppConfig(() -> {
                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                message.obj = new Object[]{PersonAppConfig.this};
                message.getData().putString(handleMessage.METHOD_KEY,"refreshData");
                handleMessage.sendMessage(message);
            });
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            layout.setNoMoreData(true);
        });
        //添加应用
        findViewById(R.id.person_app_config_plus).setOnClickListener(v->{
            Intent intent = new Intent(PersonAppConfig.this, PersonAppConfigDetailAdd.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return apps.size();
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
                LayoutInflater inflater = PersonAppConfig.this.getLayoutInflater();
                View view = convertView == null ? inflater.inflate(R.layout.app_config_list_item, null) : convertView;
                AppConfig appConfig = apps.get(position);
                TextView textView = view.findViewById(R.id.list_item_app_id);
                textView.setText(appConfig.getAppId());
                textView = view.findViewById(R.id.list_item_state);
                textView.setText(YesNoEnum.isYes(appConfig.getState())?"正在使用":"已禁用");
                textView = view.findViewById(R.id.list_item_use_public);
                textView.setText(YesNoEnum.isYes(appConfig.getUsePublic())?"启用公共服务":"禁用公共服务");
                view.setOnClickListener(v->{
                    //查看消息详情
                    Intent intent=new Intent(PersonAppConfig.this, PersonAppConfigDetail.class);
                    intent.putExtra("appConfig", JSON.toJSONString(appConfig));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                });
                return view;
            }
        };
        ListView listView = findViewById(R.id.person_app_config_list);
        listView.setAdapter(baseAdapter);
        initData();
    }
    /**
     * @do 初始化数据
     * @author liuhua
     * @date 2020/4/28 10:45 PM
     */
    public void initData(){
        List<AppConfig> appConfigs = ObjectFactory.get(SqlData.class).listObject(TablesEnum.APP_LIST.getTable(), AppConfig.class,null,"`key`");
        apps.clear();
        if(appConfigs!=null&&!appConfigs.isEmpty()){
            apps.addAll(appConfigs);
        }
        baseAdapter.notifyDataSetChanged();
        //刷新列表
        ObjectFactory.get(AppConfigService.class).refreshAppConfig(() -> {
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{PersonAppConfig.this};
            message.getData().putString(handleMessage.METHOD_KEY, "refreshData");
            handleMessage.sendMessage(message);
        });
    }
    /**
     * @do 刷新显示数据
     * @author liuhua
     * @date 2020/4/28 10:45 PM
     */
    public void refreshData(){
        List<AppConfig> appConfigs = ObjectFactory.get(SqlData.class).listObject(TablesEnum.APP_LIST.getTable(), AppConfig.class,null,"`key`");
        apps.clear();
        if(appConfigs!=null&&!appConfigs.isEmpty()){
            apps.addAll(appConfigs);
        }
        baseAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }
}
