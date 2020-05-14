package com.lh.sms.client.ui.person.template;

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
import com.lh.sms.client.work.app.service.AppConfigService;
import com.lh.sms.client.work.template.entity.SmsTemplate;
import com.lh.sms.client.work.template.enums.TemplateAuthStateEnum;
import com.lh.sms.client.work.template.service.TemplateService;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @do 应用配置
 * @author liuhua
 * @date 2020/5/13 7:15 PM
 */
public class PersonTemplateConfig extends AppCompatActivity {
    private List<SmsTemplate> templates = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_template_config);
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
            ObjectFactory.get(TemplateService.class).refreshTemplate(() -> {
                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                message.obj = new Object[]{PersonTemplateConfig.this};
                message.getData().putString(handleMessage.METHOD_KEY,"refreshData");
                handleMessage.sendMessage(message);
            });
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            layout.setNoMoreData(true);
        });
        //添加应用
        findViewById(R.id.person_template_plus).setOnClickListener(v->{
            Intent intent = new Intent(PersonTemplateConfig.this, PersonTemplateConfigDetailAdd.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return templates.size();
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
                LayoutInflater inflater = PersonTemplateConfig.this.getLayoutInflater();
                View view = convertView == null ? inflater.inflate(R.layout.template_list_item, null) : convertView;
                SmsTemplate smsTemplate = templates.get(position);
                TextView textView = view.findViewById(R.id.list_item_code);
                textView.setText(smsTemplate.getCode());
                textView = view.findViewById(R.id.list_item_state);
                textView.setText(YesNoEnum.isYes(smsTemplate.getState())?"已启用":"已禁用");
                textView = view.findViewById(R.id.list_item_auth);
                textView.setText(TemplateAuthStateEnum.OK.getValue().equals(smsTemplate.getAuthState())?"审核通过"
                        :TemplateAuthStateEnum.ERROR.getValue().equals(smsTemplate.getAuthState())?"审核失败":"正在审核");
                view.setOnClickListener(v->{
                    //查看消息详情
                    Intent intent=new Intent(PersonTemplateConfig.this, PersonTemplateConfigDetail.class);
                    intent.putExtra("smsTemplate", JSON.toJSONString(smsTemplate));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                });
                return view;
            }
        };
        ListView listView = findViewById(R.id.person_template_list);
        listView.setAdapter(baseAdapter);
        initData();
    }
    /**
     * @do 初始化数据
     * @author liuhua
     * @date 2020/4/28 10:45 PM
     */
    public void initData(){
        List<SmsTemplate> appConfigs = ObjectFactory.get(SqlData.class).listObject(TablesEnum.TEMPLATE_LIST.getTable(), SmsTemplate.class,null,"`key`");
        templates.clear();
        if(appConfigs!=null&&!appConfigs.isEmpty()){
            templates.addAll(appConfigs);
        }
        baseAdapter.notifyDataSetChanged();
        //刷新列表
        ObjectFactory.get(TemplateService.class).refreshTemplate(() -> {
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{PersonTemplateConfig.this};
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
        List<SmsTemplate> appConfigs = ObjectFactory.get(SqlData.class).listObject(TablesEnum.TEMPLATE_LIST.getTable(), SmsTemplate.class,null,"`key`");
        templates.clear();
        if(appConfigs!=null&&!appConfigs.isEmpty()){
            templates.addAll(appConfigs);
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
