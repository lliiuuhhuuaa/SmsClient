package com.lh.sms.client.work.storage.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.widget.Toast;

import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.entity.ThreadCallback;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.work.storage.entity.UpLoadInfo;
import com.lh.sms.client.work.storage.enums.StorageEnum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StorageService {
    /**
     * @do 上传文件
     * @author liuhua
     * @date 2020/6/8 8:09 PM
     */
    public void upload(Context context, Bitmap bmp, ThreadCallback threadCallback) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String contentType = "image/jpeg";
        FormBody formBody = new FormBody.Builder().add("type",contentType).add("pt", StorageEnum.PHOTO.getPt()).add("size",String.valueOf(bytes.length)).build();
        HttpClientUtil.post(ApiConstant.STORAGE_PRETREATMENT,formBody,new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).context(context).login(true)){
            @Override
            public void callback(HttpResult httpResult) {
                UpLoadInfo upLoadInfo = httpResult.getObject(UpLoadInfo.class);
                if(upLoadInfo!=null&&upLoadInfo.getToken()!=null){
                    upLoadInfo.setContentType(contentType);
                    HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                    Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                    message.obj = new Object[]{StorageService.this, upLoadInfo,new Object[]{bytes,threadCallback}};
                    message.getData().putString(HandleMsg.METHOD_KEY, "updateToStorage");
                    handleMessage.sendMessage(message);
                }
            }
        });
    }
    /**
     * @do 上传文件到云存诸
     * @author liuhua
     * @date 2020/6/8 8:24 PM
     */
    public void updateToStorage(UpLoadInfo upLoadInfo,Object[] args){
        HttpClientUtil.put(upLoadInfo.getToken(), MultipartBody.create(MediaType.parse(upLoadInfo.getContentType()),(byte[]) args[0]), new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).login(false).file(true)) {
            @Override
            public void callback(Response response) {
                if(!response.isSuccessful()){
                    AlertUtil.toast(response.message(), Toast.LENGTH_SHORT);
                    return;
                }
                ThreadCallback callback = (ThreadCallback) args[1];
                if(callback!=null){
                    callback.callback(upLoadInfo.getName());
                }
            }
        });
    }
}
