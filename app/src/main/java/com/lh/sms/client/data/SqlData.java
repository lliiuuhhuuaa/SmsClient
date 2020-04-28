package com.lh.sms.client.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lh.sms.client.MainActivity;
import com.lh.sms.client.config.service.ConfigService;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.SecurityUtil;
import com.lh.sms.client.framing.util.ThreadPool;

import java.nio.charset.StandardCharsets;

import lombok.var;

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
        //初始化数据
        initData();
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
    //初始化数据
    private void initData(){
        //保存服务器请求地址
        saveObject(DataConstant.KEY_SERVICE_URL,"http://192.168.1.5:11010/sms/api");
        //保存三方资源请求地址
        String threeServiceUrl = "https://lh-sms.oss-cn-chengdu.aliyuncs.com";
        saveObject("storage_domain",threeServiceUrl);
    }
    /**
     * @do 保存对象
     * @author liuhua
     * @date 2020/4/25 4:26 PM
     */
    public boolean saveObject(String key,Object object){
        if(object==null){
            return false;
        }
        String objStr = JSON.toJSONString(object, SerializerFeature.WriteClassName);
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", objStr);
        values.put("sign", SecurityUtil.md5(String.format("key=%s&value=%s&key=bieluangaola",key,objStr).getBytes(StandardCharsets.UTF_8)));
        return database.insertWithOnConflict("object_data",null,values,SQLiteDatabase.CONFLICT_REPLACE)>0;
    }
    /**
     * @do 保存对象
     * @author liuhua
     * @date 2020/4/20 10:38 PM
     */
    public boolean saveObject(Object object){
        return saveObject(object.getClass().getName().replace(".","_"),object);
    }
    /**
     * @do 获取对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public <T> T getObject(Class<T> clazz){
        String name = clazz.getName().replace(".","_");
        return getObject(name,clazz);
    }
    /**
     * @do 获取对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public <T> T getObject(String key,Class<T> clazz){
        Cursor cursor = database.rawQuery("select `key`,`value`,`sign` from object_data where `key`=?",new String[]{key});
        if(cursor.getCount() <1){
            return null;
        }
        cursor.moveToFirst();
        String value = cursor.getString(cursor.getColumnIndex("value"));
        String sign = cursor.getString(cursor.getColumnIndex("sign"));
        if(value==null||sign==null||!sign.equals(SecurityUtil.md5(String.format("key=%s&value=%s&key=bieluangaola",key,value).getBytes(StandardCharsets.UTF_8)))){
            return null;
        }
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        return JSON.parseObject(value.getBytes(StandardCharsets.UTF_8),clazz);
    }
    /**
     * @do 删除对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public boolean deleteObject(Class clazz){
        String name = clazz.getName().replace(".","_");
        return deleteObject(name);
    }
    /**
     * @do 删除对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public boolean deleteObject(String key){
        return database.delete("object_data","`key`=?",new String[]{key})==1;
    }
}
