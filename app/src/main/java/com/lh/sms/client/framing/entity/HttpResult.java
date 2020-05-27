package com.lh.sms.client.framing.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import okhttp3.Response;

@Data
public class HttpResult {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 消息
     */
    private String msg;

    /**
     * 数据对象
     */
    private Object data;
    /**
     * 响应
     */
    private Response response;
    /**
     * @do data转为对象
     * @author liuhua
     * @date 2020/4/25 4:22 PM
     */
    public JSONObject getJSONObject(){
        if(data==null||StringUtils.isBlank(data.toString())){
            return null;
        }
        return JSON.parseObject(data.toString());
    }
    /**
     * @do data转为对象
     * @author liuhua
     * @date 2020/4/25 4:22 PM
     */
    public <T> T getObject(Class<T> clazz){
        return JSON.parseObject(data.toString(),clazz);
    }
    /**
     * @do data转为对象
     * @author liuhua
     * @date 2020/4/25 4:22 PM
     */
    public JSONArray getJSONArray(){
        if(data==null||StringUtils.isBlank(data.toString())){
            return null;
        }
        return JSON.parseArray(data.toString());
    }
}
