package com.lh.sms.client.data.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lh.sms.client.MainActivity;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.util.SecurityUtil;
import com.lh.sms.client.work.msg.entity.Message;
import com.lh.sms.client.work.sms.entity.SmsProvide;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        //允许fastjson反序列化
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
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
        //初始化数据表
        for (TablesEnum tablesEnum : TablesEnum.values()) {
            database.execSQL(tablesEnum.getSql());
        }
    }
    //初始化数据
    private void initData(){
        //保存服务器请求地址
        saveObject(DataConstant.KEY_SERVICE_URL,"http://192.168.1.5:12010/sms/api");
        saveObject(DataConstant.KEY_SOCKET_DOMAIN,"http://192.168.1.5:12010");
        //保存三方资源请求地址
        String threeServiceUrl = "https://al.lliiuuhhuuaa.cn";
        saveObject("storage_domain",threeServiceUrl);
        //清除sessionID
        deleteObject(DataConstant.SESSION_ID);
    }
    /**
     * @do 保存对象
     * @author liuhua
     * @date 2020/4/25 4:26 PM
     */
    public boolean saveObject(String key,Object object){
        return saveObject("object_data",key,object);
    }
    /**
     * @do 保存对象
     * @author liuhua
     * @date 2020/4/25 4:26 PM
     */
    public boolean saveObject(String table,String key,Object object){
        if(object==null){
            return false;
        }
        String objStr = JSON.toJSONString(object, SerializerFeature.WriteClassName);
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", objStr);
        values.put("sign", SecurityUtil.md5(String.format("key=%s&value=%s&key=bieluangaola",key,objStr).getBytes(StandardCharsets.UTF_8)));
        return database.insertWithOnConflict(table,null,values,SQLiteDatabase.CONFLICT_REPLACE)>0;
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
        return getObject("object_data",name,clazz);
    }
    /**
     * @do 获取对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public <T> T getObject(String key,Class<T> clazz){
        return getObject("object_data",key,clazz);
    }
    /**
     * @do 获取对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public <T> T getObject(String table,String key,Class<T> clazz){
        Cursor cursor = database.rawQuery("select `key`,`value`,`sign` from "+table+" where `key`=?",new String[]{key});
        if(cursor.getCount() <1){
            return null;
        }
        cursor.moveToFirst();
        String value = cursor.getString(cursor.getColumnIndex("value"));
        String sign = cursor.getString(cursor.getColumnIndex("sign"));
        if(value==null||sign==null||!sign.equals(SecurityUtil.md5(String.format("key=%s&value=%s&key=bieluangaola",key,value).getBytes(StandardCharsets.UTF_8)))){
            return null;
        }
        return JSON.parseObject(value.getBytes(StandardCharsets.UTF_8),clazz);
    }
    /**
     * @do 删除对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public boolean deleteObject(Class clazz){
        String name = clazz.getName().replace(".","_");
        return deleteObject("object_data",name);
    }
    /**
     * @do 删除对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public boolean deleteObject(String table,Class clazz){
        String name = clazz.getName().replace(".","_");
        return deleteObject(table,name);
    }
    /**
     * @do 删除对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public boolean deleteObject(String key){
        return database.delete("object_data","`key`=?",new String[]{key})==1;
    }
    /**
     * @do 删除对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public boolean deleteObject(String table,String key){
        return deleteObject(table,key,"key");
    }
    /**
     * @do 删除对象
     * @author lh
     * @date 2020-04-15 15:54
     */
    public boolean deleteObject(String table,String key,String column){
        return database.delete(table,String.format("`%s`=?",column),new String[]{key})==1;
    }
    /**
     * @do 获取list全部对象
     * @author liuhua
     * @date 2020/5/10 1:01 PM
     */
    public <T> List<T> listObject(String table, Class<T> clazz) {
        return listObject(table,clazz,null,null);
    }
    /**
     * @do 获取list全部对象
     * @author liuhua
     * @date 2020/5/10 1:01 PM
     */
    public <T> List<T> listObject(String table, Class<T> clazz,Integer page,String sort) {
        String sql = "select `key`,`value`,`sign` from "+table;
        List<String> param = new ArrayList<>();
        if(sort!=null){
            sql += " order by "+sort;
        }
        if(page!=null){
            Integer count = 20;
            Integer start = (page-1)*count;
            sql += " limit ?,?";
            param.add(start.toString());
            param.add(count.toString());
        }
        List<T> list = new ArrayList<>();
        Cursor cursor = database.rawQuery(sql,param.toArray(new String[]{}));
        if(cursor.getCount() <1){
            return list;
        }
        cursor.moveToFirst();
        do {
            String value = cursor.getString(cursor.getColumnIndex("value"));
            String key = cursor.getString(cursor.getColumnIndex("key"));
            String sign = cursor.getString(cursor.getColumnIndex("sign"));
            if(value==null||sign==null||!sign.equals(SecurityUtil.md5(String.format("key=%s&value=%s&key=bieluangaola",key,value).getBytes(StandardCharsets.UTF_8)))){
                return null;
            }
            list.add(JSON.parseObject(value.getBytes(StandardCharsets.UTF_8),clazz));
        } while (cursor.moveToNext());
        return list;
    }
    /**
     * @do 获取list全部对象
     * @author liuhua
     * @date 2020/5/10 1:01 PM
     */
    public void deleteAll(String table) {
        database.execSQL("delete from "+table);
    }
    /**
     * @do 删除所有缓存数据
     * @author liuhua
     * @date 2020/5/11 7:58 PM
     */
    public void deleteAll() {
        for (TablesEnum tablesEnum : TablesEnum.values()) {
            deleteAll(tablesEnum.getTable());
        }
        //重新初始化数据
        initData();
    }
    /**
     * @do 获取sql连接
     * @author liuhua
     * @date 2020/5/12 1:54 PM
     */
    public SQLiteDatabase getDatabase() {
        return database;
    }
}
