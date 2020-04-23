package com.lh.sms.client.framing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectFactory {
    //存入对象
    private static Map<Class,Object> classObjectMap = new ConcurrentHashMap<>();
    /**
     * @do 放入对象
     * @author liuhua
     * @date 2020/3/12 7:10 PM
     */
    public static void push(Object ... objects){
        for (Object object : objects) {
            classObjectMap.put(object.getClass(),object);
        }
    }
    /**
     * @do 取出对象
     * @author liuhua
     * @date 2020/3/12 7:13 PM
     */
    public static <T> T get(Class<T> clazz){
        Object o = classObjectMap.get(clazz);
        if(o!=null){
            return (T) o;
        }
        //对象不存在,创建新对象
        try {
            o = clazz.newInstance();
            classObjectMap.put(clazz,o);
            return (T)o;
        } catch (Exception e) {
            e.printStackTrace();
           return null;
        }
    }
    /**
     * @do 删除对象
     * @author liuhua
     * @date 2020/3/12 7:22 PM
     */
    public static void remove(Class clazz){
        classObjectMap.remove(clazz);
    }
}
