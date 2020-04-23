package com.lh.sms.client.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lh.sms.client.MainActivity;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.util.SecurityUtil;

import java.nio.charset.StandardCharsets;

public class SqlData {
    private SQLiteDatabase database;
    public SqlData(){
        //初始化数据库
        initDatabase();
    }
    /**
     * @do 创建数据库
     * @author liuhua
     * @date 2020/4/20 10:32 PM
     */
    private void initDatabase(){
        Context context = ObjectFactory.get(MainActivity.class);
        database=SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("sms_client"),null);
        //创建表
        initTables();
    }
    /**
     * @do 创建表
     * @author liuhua
     * @date 2020/4/20 10:32 PM
     */
    private void initTables(){
        //对象数据表
        database.execSQL("create table if not exists object_data (`key` varchar primary key,`value` varchar not null,`sign` varchar not null)");
    }

    /**
     * @do 保存对象
     * @author liuhua
     * @date 2020/4/20 10:38 PM
     */
    public boolean saveObject(Object object){
        if(object==null){
            return false;
        }
        String objStr = JSON.toJSONString(object, SerializerFeature.WriteClassName);
        String name = object.getClass().getName().replace(".","_");
        ContentValues values = new ContentValues();
        values.put("key", name);
        values.put("value", objStr);
        values.put("sign", SecurityUtil.md5(String.format("key=%s&value=%s&key=bieluangaola",name,objStr).getBytes(StandardCharsets.UTF_8)));
        return database.insertWithOnConflict("object_data",null,values,SQLiteDatabase.CONFLICT_REPLACE)>0;
    }
    /**
     * @do 获取对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public <T> T getObject(Class<T> clazz){
        String name = clazz.getName().replace(".","_");
        Cursor cursor = database.rawQuery("select `key`,`value`,`sign` from object_data where `key`=?",new String[]{name});
        if(cursor.getCount() <1){
            return null;
        }
        cursor.moveToFirst();
        String key = cursor.getString(cursor.getColumnIndex("key"));
        String value = cursor.getString(cursor.getColumnIndex("value"));
        String sign = cursor.getString(cursor.getColumnIndex("sign"));
        if(value==null||sign==null||!sign.equals(SecurityUtil.md5(String.format("key=%s&value=%s&key=bieluangaola",name,value).getBytes(StandardCharsets.UTF_8)))){
           return null;
        }
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        return JSON.parseObject(value.getBytes(StandardCharsets.UTF_8),clazz);
    }
}
