package com.lh.sms.client.framing.entity;

/**
 * @do 异步返回结果
 * @author liuhua
 * @date 2020/4/25 2:35 PM
 */
public interface ThreadCallback<T>{
    /**
     * @do 回调
     * @author liuhua
     * @date 2020/6/8 10:05 PM
     */
    void callback(T t);
}
