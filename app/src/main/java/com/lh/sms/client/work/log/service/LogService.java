package com.lh.sms.client.work.log.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;

import com.lh.sms.client.MainActivity;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ActivityManager;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.ui.person.sms.PersonSmsConfig;
import com.lh.sms.client.ui.record.RecordFragment;
import com.lh.sms.client.work.log.entity.Logs;
import com.lh.sms.client.work.log.enums.LogLevelEnum;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class LogService {
    /**
     * @do 日志(信息)
     * @author liuhua
     * @date 2020/5/12 12:36 PM
     */
    public void info(String text,Object ... param){
        saveLog(param==null?text:String.format(text,param),LogLevelEnum.INFO.getValue());
    }

    /**
     * @do 日志(警告)
     * @author liuhua
     * @date 2020/5/12 12:36 PM
     */
    public void warn(String text,Object ... param){
        saveLog(param==null?text:String.format(text,param),LogLevelEnum.WARN.getValue());
    }
    /**
     * @do 日志(错误)
     * @author liuhua
     * @date 2020/5/12 12:36 PM
     */
    public void error(String text,Object ... param){
        saveLog(param==null?text:String.format(text,param),LogLevelEnum.ERROR.getValue());
    }
    /**
     * @do 日志(成功)
     * @author liuhua
     * @date 2020/5/12 12:36 PM
     */
    public void success(String text,Object ... param){
        saveLog(param==null?text:String.format(text,param),LogLevelEnum.SUCCESS.getValue());
    }
    /**
     * @do 保存日志
     * @author liuhua
     * @date 2020/5/12 2:04 PM
     */
    private int saveLog(String text,Integer level){
        SQLiteDatabase database = ObjectFactory.get(SqlData.class).getDatabase();
        //只保存1个月记录
        database.delete(TablesEnum.LOG_LIST.getTable(),"time<?",new String[]{String.valueOf(LocalDateTime.now().minusMonths(1).toDate().getTime())});
        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();
        values.put("text", text);
        values.put("time",time );
        values.put("level", level);
        int count = Long.valueOf(database.insert(TablesEnum.LOG_LIST.getTable(),null,values)).intValue();
        if(ActivityManager.getInstance().getCurrentActivity().getClass().equals(MainActivity.class)){
            Logs log = new Logs();
            log.setText(text);
            log.setLevel(level);
            log.setTime(time);
            RecordFragment recordFragment = ObjectFactory.get(RecordFragment.class);
            if(recordFragment!=null){
                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                message.obj = new Object[]{recordFragment,log};
                message.getData().putString(handleMessage.METHOD_KEY, "addLog");
                handleMessage.sendMessage(message);
            }
        }
        return count;
    }
    /**
     * @do 获取日志列表
     * @author liuhua
     * @date 2020/5/12 1:54 PM
     */
    public List<Logs> listLog(int page, Integer level) {
        SQLiteDatabase database = ObjectFactory.get(SqlData.class).getDatabase();
        String sql = String.format("select * from %s %s order by id desc limit ?,?",TablesEnum.LOG_LIST.getTable(),level!=null?"where level="+level:"");
        Integer count = 20;
        Integer start = (page-1)*count;
        List<Logs> list = new ArrayList<>();
        Cursor cursor = database.rawQuery(sql,new String[]{start.toString(),count.toString()});
        if(cursor.getCount() <1){
            return list;
        }
        cursor.moveToFirst();
        do {
            Long id = cursor.getLong(cursor.getColumnIndex("id"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            Long time = cursor.getLong(cursor.getColumnIndex("time"));
            Integer level2 = cursor.getInt(cursor.getColumnIndex("level"));
            Logs log = new Logs();
            log.setId(id);
            log.setLevel(level2);
            log.setText(text);
            log.setTime(time);
            list.add(log);
        } while (cursor.moveToNext());
        return list;
    }
}
