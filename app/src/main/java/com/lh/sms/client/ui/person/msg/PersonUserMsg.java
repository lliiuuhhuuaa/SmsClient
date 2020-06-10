package com.lh.sms.client.ui.person.msg;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.lh.sms.client.ui.plug.HorizontalScroll;
import com.lh.sms.client.work.msg.entity.Message;
import com.lh.sms.client.work.msg.enums.MessageStateEnum;
import com.lh.sms.client.work.msg.service.MessageService;
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

public class PersonUserMsg extends AppCompatActivity {
    //选择类型弹窗
    SelectDialog selectDialog = null;
    private List<Message> messages = new ArrayList<>();
    private static final String TAG = "PersonUserMsg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_user_msg);
        //绑定事件
        bindEvent();
        List<String[]> param = new ArrayList<>();
        param.add(new String[]{"全部",""});
        param.add(new String[]{"未处理","0"});
        param.add(new String[]{"已处理","1"});
        param.add(new String[]{"已忽略","2"});
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
        ListView listView = findViewById(R.id.person_user_msg_list);
        //初始化左划插件
        HorizontalScroll horizontalScroll = new HorizontalScroll(listView);
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return messages.size();
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
                LayoutInflater inflater = PersonUserMsg.this.getLayoutInflater();
                View view = convertView == null ? inflater.inflate(R.layout.user_msg_list_item, null) : convertView;
                Message message = messages.get(position);
                TextView textView = view.findViewById(R.id.list_item_title);
                if(MessageStateEnum.WAIT.getValue().equals(message.getState())){
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_new_16dp, null);
                    drawable.setBounds(0, 0, 64,64);
                    textView.setCompoundDrawables(null,null,drawable,null);
                }
                //左划删除操作,并绑定item点击事件
                HorizontalScrollView horizontalScrollView = horizontalScroll.initScroll(view, v -> {
                    //查看消息详情
                    Intent intent=new Intent(PersonUserMsg.this, PersonUserMsgDetail.class);
                    intent.putExtra("id",message.getId().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                });
                view.findViewById(R.id.right_delete).setOnClickListener(v->{
                    horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                    messages.remove(position);
                    refreshList();
                    ObjectFactory.get(MessageService.class).updateState(message.getId(),-1,PersonUserMsg.this,null);
                });
                textView.setText(message.getTitle());
                textView = view.findViewById(R.id.list_item_text);
                textView.setText(message.getText());
                textView = view.findViewById(R.id.list_item_time);
                view.setTag(message.getId());
                LocalDateTime localDateTime = LocalDateTime.fromDateFields(new Date(message.getCreateDate()));
                if(localDateTime.toLocalDate().equals(LocalDate.now())){
                    textView.setText(localDateTime.toString("今天HH:mm:ss"));
                }else if(localDateTime.toLocalDate().equals(LocalDate.now().minusDays(1))){
                    textView.setText(localDateTime.toString("昨天HH:mm:ss"));
                }else if(localDateTime.toLocalDate().equals(LocalDate.now().minusDays(2))){
                    textView.setText(localDateTime.toString("前天HH:mm:ss"));
                }else{
                    textView.setText(localDateTime.toString("yyyy-MM-dd HH:mm"));
                }
                return view;
            }
        };
        listView.setAdapter(baseAdapter);
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(layout -> {
            getIntent().putExtra("page", 0);
            initData(layout);
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            layout.finishLoadMore();
            initData(layout);
        });
        //打开选择
        findViewById(R.id.person_user_msg_select_type).setOnClickListener(v->{
            selectDialog.show();
        });

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
        String state = getIntent().getStringExtra("state");
        Integer stateInt = StringUtils.isBlank(state)?null:Integer.valueOf(state);
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
        List<Message> list = ObjectFactory.get(SqlData.class).listObject(TablesEnum.MSG_LIST.getTable(), Message.class, page, "`key` desc");
        if(page==1){
            messages.clear();
        }
        for (Message message : list) {
            if(stateInt!=null&&!message.getState().equals(stateInt)){
                continue;
            }
            if (!messages.contains(message)) {
                messages.add(message);
            }
        }
        Collections.sort(messages);
        android.os.Message message = android.os.Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
        message.obj = new Object[]{PersonUserMsg.this};
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
        if(StringUtils.isNotBlank(state)){
            param.add("state",state);
        }
        HttpClientUtil.post(ApiConstant.MSG_LIST,param.build(),
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
                            messages.clear();
                        }else{
                            layout.finishLoadMore(true);//传入false表示加载失败
                        }
                        JSONObject jsonObject = httpResult.getJSONObject();
                        int page = jsonObject.getIntValue("page");
                        getIntent().putExtra("pages",jsonObject.getIntValue("pages"));
                        getIntent().putExtra("page",page);
                        JSONArray rows = jsonObject.getJSONArray("rows");
                        if(page==1){
                            messages.clear();
                            if(rows.size()<20){
                                //不足分页,清除本地所有
                                sqlData.deleteAll(TablesEnum.MSG_LIST.getTable());
                            }
                        }
                        for (int i = 0; i < rows.size(); i++) {
                            Message message = rows.getObject(i, Message.class);
                            if (!messages.contains(message)) {
                                messages.add(message);
                            }
                            sqlData.saveObject(TablesEnum.MSG_LIST.getTable(), message.getId().toString(), message);
                        }
                        Collections.sort(messages);
                        android.os.Message message = android.os.Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                        message.obj = new Object[]{PersonUserMsg.this};
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
        TextView viewById = findViewById(R.id.person_user_msg_type);
        viewById.setText(textView.getText());
        String type = (String) view.getTag();
        viewById.setTag(type);
        Log.d(TAG, "searchByType: "+type);
        getIntent().putExtra("state",type);
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
