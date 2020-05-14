package com.lh.sms.client.framing.util;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.SystemConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.enums.ResultCodeEnum;
import com.lh.sms.client.ui.person.msg.PersonUserMsg;
import com.lh.sms.client.ui.person.user.PersonLogin;
import com.lh.sms.client.work.user.service.UserService;

import java.io.IOException;
import java.sql.SQLData;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * http请求工具
 *
 * @author lh
 */
public class HttpClientUtil {
    private static final String TAG = "HttpClientUtil";
    public enum RequestTypeEnum {
        POST("post"), // post
        GET("get"); // get
        @Getter
        private String value;

        RequestTypeEnum(String value) {
            this.value = value;
        }
    }

    /**
     * @do map转formBody
     * @author lh
     * @date 2020-01-02 10:50
     */
    private static FormBody mapToFormBody(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                builder.add(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * @do get请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult get(String url) {
        return execute(url, null, null, RequestTypeEnum.GET, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do get请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult get(String url, Map<String, String> params) {
        FormBody formBody = null;
        if (params != null && params.size() == 1) {
            formBody = mapToFormBody(params);
        }
        return execute(url, formBody, null, RequestTypeEnum.GET, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do get请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult get(String url, Map<String, String> params, HttpAsynResult callback) {
        FormBody formBody = null;
        if (params != null && params.size() == 1) {
            formBody = mapToFormBody(params);
        }
        return execute(url, formBody, callback, RequestTypeEnum.GET, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do get请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult get(String url, FormBody formBody) {
        return execute(url, formBody, null, RequestTypeEnum.GET, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do get请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult get(String url, FormBody formBody, HttpAsynResult httpAsynResult) {
        return execute(url, formBody, httpAsynResult, RequestTypeEnum.GET, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url) {
        return execute(url, new FormBody.Builder().build(), null, RequestTypeEnum.POST, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }
    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, HttpAsynResult callback) {
        return execute(url, new FormBody.Builder().build(), callback, RequestTypeEnum.POST, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }
    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, Map<String, String> params) {
        FormBody formBody = null;
        if (params != null && params.size() == 1) {
            formBody = mapToFormBody(params);
        }
        return execute(url, formBody, null, RequestTypeEnum.POST, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, Map<String, String> params, HttpAsynResult callback) {
        FormBody formBody = null;
        if (params != null && params.size() == 1) {
            formBody = mapToFormBody(params);
        }
        return execute(url, formBody, callback, RequestTypeEnum.POST, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, Map<String, String> params, MediaType mediaType) {
        FormBody formBody = null;
        if (params != null && params.size() == 1) {
            formBody = mapToFormBody(params);
        }
        return execute(url, formBody, null, RequestTypeEnum.POST, mediaType);
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, Map<String, String> params, HttpAsynResult callback, MediaType mediaType) {
        FormBody formBody = null;
        if (params != null && params.size() == 1) {
            formBody = mapToFormBody(params);
        }
        return execute(url, formBody, callback, RequestTypeEnum.POST, mediaType);
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, FormBody formBody) {
        return execute(url, formBody, null, RequestTypeEnum.POST, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, FormBody formBody, HttpAsynResult callback) {
        return execute(url, formBody, callback, RequestTypeEnum.POST, MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"));
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, FormBody formBody, MediaType mediaType) {
        return execute(url, formBody, null, RequestTypeEnum.POST, mediaType);
    }

    /**
     * @do post请求
     * @author lh
     * @date 2020-01-02 11:36
     */
    public static HttpResult post(String url, FormBody formBody, HttpAsynResult callback, MediaType mediaType) {
        return execute(url, formBody, callback, RequestTypeEnum.POST, mediaType);
    }

    /**
     * @do 执行http请求
     * @author lh
     * @date 2020-01-02 11:04
     */
    public static HttpResult execute(String url, FormBody formBody, HttpAsynResult callback, RequestTypeEnum requestTypeEnum, MediaType mediaType) {
        SweetAlertDialog sweetAlertDialog = null;
        if(callback!=null&&callback.getConfig().isAnimation()){
            //显示动画
            sweetAlertDialog = AlertUtil.alertProcess(callback.getConfig().getContext());
        }
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        if(!url.startsWith("http:")&&!url.startsWith("https:")) {
            String domain = sqlData.getObject(DataConstant.KEY_SERVICE_URL, String.class);
            url = String.format("%s%s%s",domain,url.startsWith("/")?"":"/",url);
        }
        String tk = sqlData.getObject(DataConstant.KEY_USER_TK,String.class);
        String requestUrl = url;
        OkHttpClient okHttpClient = new OkHttpClient();
        //构造请求体
        Request.Builder builder = new Request.Builder().url(requestUrl);
        if(tk!=null){
            builder.addHeader("tk",tk);
        }else if(callback!=null&&callback.getConfig().isLogin()){
            //未登陆
            if(sweetAlertDialog!=null){
                //关闭动画
                AlertUtil.close(sweetAlertDialog);
            }
            if(callback.getConfig().getContext()!=null) {
                Intent intent = new Intent(callback.getConfig().getContext(), PersonLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                callback.getConfig().getContext().startActivity(intent);
            }
            return null;
        }
        setSessionId(builder);
        //post请求
        if (RequestTypeEnum.POST.equals(requestTypeEnum)) {
            builder.post(formBody);
        }
        Request request = builder.build();
        if (callback == null) {
            try {
                //同步调用
                Response response = okHttpClient.newCall(request).execute();
                //保存sessionId
                saveSessionId(response);
                if(sweetAlertDialog!=null){
                    //关闭动画
                    AlertUtil.close(sweetAlertDialog);
                }
                if (!response.isSuccessful()) {
                    //请求错误
                    Log.e(TAG, String.format("请求url:%s,状态码:%s,错误信息:%s", requestUrl, response.code(), response.body() == null ? null : response.body().string()));
                    System.out.println(response);
                    return null;
                }
                //转为返回对象
                return JSONObject.parseObject(response.body().string(), HttpResult.class);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, String.format("请求url:%s", requestUrl), e);
                return null;
            }
        }
        //异步调用
        SweetAlertDialog finalSweetAlertDialog = sweetAlertDialog;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(finalSweetAlertDialog !=null){
                    //关闭动画
                    AlertUtil.close(finalSweetAlertDialog);
                }
                if(callback.getConfig().isOnlyOk()){
                    if(callback.getConfig().isAlertError()) {
                        AlertUtil.alertError(callback.getConfig().getContext(),e.getMessage());
                    }
                    return;
                }
                HttpResult httpResult = new HttpResult();
                httpResult.setCode(ResultCodeEnum.ERROR.getValue());
                httpResult.setMsg(e.getMessage());
                callback.callback(httpResult);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(finalSweetAlertDialog !=null){
                    //关闭动画
                    AlertUtil.close(finalSweetAlertDialog);
                }
                //保存sessionId
                saveSessionId(response);
                HttpResult httpResult = null;
                if (!response.isSuccessful()) {
                    //请求错误
                    Log.e(TAG, String.format("请求url:%s,状态码:%s,错误信息:%s", requestUrl, response.code(), response.body() == null ? null : response.body().string()));
                    if(callback.getConfig().isOnlyOk()){
                        if(callback.getConfig().isAlertError()) {
                            AlertUtil.alertError(callback.getConfig().getContext(),response.message());
                        }
                        return;
                    }
                    httpResult = new HttpResult();
                    httpResult.setCode(response.code());
                    httpResult.setMsg(response.message());
                    callback.callback(httpResult);
                }
                try {
                    //转为返回对象
                    httpResult = JSONObject.parseObject(response.body().string(), HttpResult.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(callback.getConfig().isOnlyOk()&&(httpResult==null||!ResultCodeEnum.OK.getValue().equals(httpResult.getCode()))){
                    //这里处理多种结果
                    if(callback.getConfig().isAlertError()) {
                        AlertUtil.alertError(callback.getConfig().getContext(),httpResult == null ? SystemConstant.HANDLE_ERROR : httpResult.getMsg());
                    }
                    if(ResultCodeEnum.NO_AUTH.getValue().equals(httpResult.getCode())){
                        //未登陆
                        ObjectFactory.get(UserService.class).unLogin();
                    }
                    return;
                }
                callback.callback(httpResult);
            }
        });
        return null;
    }
    /**
     * @do 保存sessionId
     * @author liuhua
     * @date 2020/5/13 9:35 PM
     */
    private static void saveSessionId(Response response){
        String sessionId = response.header("Set-Cookie");
        if(sessionId==null){
            return;
        }
        sessionId = sessionId.replaceAll(".*(JSESSIONID=[0-9A-F]{32}).*","$1");
        System.out.println(sessionId);
        ObjectFactory.get(SqlData.class).saveObject(DataConstant.SESSION_ID,sessionId);
    }
    /**
     * @do 设置sessionId
     * @author liuhua
     * @date 2020/5/13 9:38 PM
     */
    private static void setSessionId(Request.Builder builder){
        String sessionId = ObjectFactory.get(SqlData.class).getObject(DataConstant.SESSION_ID, String.class);
        if(sessionId!=null) {
            builder.addHeader("cookie", sessionId);
        }
    }
}