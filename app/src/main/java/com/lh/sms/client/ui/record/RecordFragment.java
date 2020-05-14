package com.lh.sms.client.ui.record;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.ui.dialog.person.balance.SelectDialog;
import com.lh.sms.client.work.log.entity.Logs;
import com.lh.sms.client.work.log.enums.LogLevelEnum;
import com.lh.sms.client.work.log.service.LogService;
import com.lh.sms.client.work.socket.service.SocketService;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RecordFragment extends Fragment {
    private View root;
    private List<Logs> logs = new ArrayList<>();
    //选择类型弹窗
    SelectDialog selectDialog = null;
    private static final String TAG = "RecordFragment";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_record, container, false);
        this.root = root;
        //绑定事件
        bindEvent();
        List<String[]> param = new ArrayList<>();
        param.add(new String[]{"全部",""});
        param.add(new String[]{"成功", LogLevelEnum.SUCCESS.getValue().toString()});
        param.add(new String[]{"信息",LogLevelEnum.INFO.getValue().toString()});
        param.add(new String[]{"警告",LogLevelEnum.WARN.getValue().toString()});
        param.add(new String[]{"错误",LogLevelEnum.ERROR.getValue().toString()});
        selectDialog = new SelectDialog(getContext(),this,param);
        return root;
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private BaseAdapter baseAdapter = null;
    private void bindEvent() {
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return logs.size();
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
                LayoutInflater inflater = RecordFragment.this.getLayoutInflater();
                View view = convertView == null ? inflater.inflate(R.layout.record_list_item, null) : convertView;
                Logs log = logs.get(position);
//                TextView textView = view.findViewById(R.id.record_list_item_time);
//                if(!MessageStateEnum.WAIT.getValue().equals(message.getState())){
//                    textView.setCompoundDrawables(null,null,null,null);
//                }
//                textView.setText(message.getTitle());
                TextView textView = view.findViewById(R.id.record_list_item_text);
                textView.setText(log.getText());
                textView = view.findViewById(R.id.record_list_item_time);
                Drawable drawable = null;
                if(LogLevelEnum.SUCCESS.getValue().equals(log.getLevel())){
                    drawable = getResources().getDrawable(R.drawable.ic_success_16dp, null);
                }else if(LogLevelEnum.WARN.getValue().equals(log.getLevel())){
                    drawable = getResources().getDrawable(R.drawable.ic_warn_16dp, null);
                }else if(LogLevelEnum.ERROR.getValue().equals(log.getLevel())){
                    drawable = getResources().getDrawable(R.drawable.ic_error_16dp, null);
                }
                LocalDateTime localDateTime = LocalDateTime.fromDateFields(new Date(log.getTime()));
                if(localDateTime.toLocalDate().equals(LocalDate.now())){
                    textView.setText(localDateTime.toString("今天HH:mm:ss"));
                }else if(localDateTime.toLocalDate().equals(LocalDate.now().minusDays(1))){
                    textView.setText(localDateTime.toString("昨天HH:mm:ss"));
                }else if(localDateTime.toLocalDate().equals(LocalDate.now().minusDays(2))){
                    textView.setText(localDateTime.toString("前天HH:mm:ss"));
                }else{
                    textView.setText(localDateTime.toString("yyyy-MM-dd HH:mm"));
                }
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                return view;
            }
        };
        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(layout -> {
            getActivity().getIntent().putExtra("page", 0);
            initData(layout);
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            layout.finishLoadMore();
            initData(layout);
        });
        //打开选择
        root.findViewById(R.id.record_select_type).setOnClickListener(v->{
            selectDialog.show();
        });
        ListView listView = root.findViewById(R.id.record_list);
        listView.setAdapter(baseAdapter);
        //显示数据
        initData(null);
    }
    /**
     * @do 初始化数据
     * @author liuhua
     * @date 2020/5/10 5:15 PM
     */
    private void initData(RefreshLayout layout){
        int page = getActivity().getIntent().getIntExtra("page", 0);
        int level = getActivity().getIntent().getIntExtra("level", -1);
        page++;
        List<Logs> list = ObjectFactory.get(LogService.class).listLog(page,level<0?null:level);
        if(list==null||list.isEmpty()){
            if(layout!=null){
                layout.setNoMoreData(true);
            }
            if(page==1){
                logs.clear();
                baseAdapter.notifyDataSetChanged();
            }
            return;
        }
        if(list.size()<20){
            if(layout!=null){
                layout.setNoMoreData(true);
            }
        }
        if(page==1){
            logs.clear();
        }
        logs.addAll(list);
        baseAdapter.notifyDataSetChanged();
        getActivity().getIntent().putExtra("page", page);
        if(layout!=null) {
            //刷新完成
            layout.finishRefresh(true);
            //加载完成
            layout.finishLoadMore(true);//传入false表示加载失败
        }
    }
    /**
     * 选择类型
     * @param view
     */
    public void searchByType(View view) {
        TextView textView = (TextView) view;
        TextView viewById = root.findViewById(R.id.person_user_msg_type);
        viewById.setText(textView.getText());
        String type = (String) view.getTag();
        viewById.setTag(type);
        Log.d(TAG, "searchByType: "+type);
        int level = StringUtils.isBlank(type)?-1:Integer.valueOf(type);
        getActivity().getIntent().putExtra("level",level);
        getActivity().getIntent().putExtra("page", 0);
        selectDialog.cancel();
        initData(null);
    }
}
