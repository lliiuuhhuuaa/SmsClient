package com.lh.sms.client.work.user.service;

import android.os.Message;
import android.widget.Toast;

import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.constant.ApiConstant;
import com.lh.sms.client.framing.entity.HttpAsynResult;
import com.lh.sms.client.framing.entity.HttpResult;
import com.lh.sms.client.framing.entity.ThreadCallback;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.HttpClientUtil;
import com.lh.sms.client.ui.person.PersonFragment;
import com.lh.sms.client.ui.person.user.enums.SmsTypeEnum;
import com.lh.sms.client.work.user.entity.UserInfo;
import com.lh.sms.client.work.user.entity.UserInfoByUpdate;

import okhttp3.FormBody;

public class UserService {
    //用户登陆
    public UserInfo login(String username,String phone){
        UserInfo userInfo = new UserInfo();
        userInfo.setPhone(username);
        return userInfo;
    }
    /**
     * @do 未登陆状态
     * @author liuhua
     * @date 2020/4/28 9:22 PM
     */
    public void unLogin(){
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        sqlData.deleteAll();
        //清除用户登陆信息
        HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
        Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
        message.obj = new Object[]{ObjectFactory.get(PersonFragment.class)};
        message.getData().putString(HandleMsg.METHOD_KEY, "clearUserInfo");
        handleMessage.sendMessage(message);
    }
    /**
     * @do 更新用户信息
     * @author liuhua
     * @date 2020/6/8 10:09 PM
     */
    public void updateUserInfo(UserInfoByUpdate userInfoByUpdate, ThreadCallback threadCallback){
        FormBody.Builder builder = new FormBody.Builder();
        if(userInfoByUpdate.getPhoto()!=null){
            builder.add("photo",userInfoByUpdate.getPhoto());
        }
        if(userInfoByUpdate.getNickname()!=null){
            builder.add("nickname",userInfoByUpdate.getNickname());
        }
        if(userInfoByUpdate.getPhone()!=null){
            builder.add("phone",userInfoByUpdate.getPhone());
            builder.add("newCode",userInfoByUpdate.getNewCode());
            builder.add("oldCode",userInfoByUpdate.getOldCode());
        }
        HttpClientUtil.post(ApiConstant.UPDATE_USER_INFO,builder.build(),new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).login(true)){
            @Override
            public void callback(HttpResult httpResult) {
                SqlData sqlData = ObjectFactory.get(SqlData.class);
                UserInfo userInfo = sqlData.getObject(UserInfo.class);
                if(userInfo!=null){
                    if(userInfoByUpdate.getPhoto()!=null){
                        userInfo.setPhoto(userInfoByUpdate.getPhoto());
                    }
                    if(userInfoByUpdate.getNickname()!=null){
                        userInfo.setNickname(userInfoByUpdate.getNickname());
                    }
                    if(userInfoByUpdate.getPhone()!=null){
                        userInfo.setPhone(userInfoByUpdate.getPhone().replaceAll("^([0-9]{3}).*([0-9]{4})$","$1****$2"));
                    }
                    sqlData.saveObject(userInfo);
                }
                if(threadCallback!=null){
                    threadCallback.callback(userInfo);
                }
                AlertUtil.toast("保存成功", Toast.LENGTH_SHORT);
            }
        });
    }
    /**
     * @do 发送验证码
     * @author liuhua
     * @date 2020/6/10 8:13 PM
     */
    public void sendCode(String phone,String type,ThreadCallback threadCallback){
        //请求后台发送验证码s
        FormBody param = new FormBody.Builder().add("phone", phone).add("type", type).build();
        HttpClientUtil.post(ApiConstant.SEND_SMS_CODE, param,
                new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).login(SmsTypeEnum.VERIFY_OLD.getValue().equals(type)||SmsTypeEnum.VERIFY_NEW.getValue().equals(type))) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        if(threadCallback!=null){
                            threadCallback.callback(httpResult.getData());
                        }
                    }
                });
    }
    /**
     * @do 获取用户头像
     * @author liuhua
     * @date 2020/6/16 8:52 PM
     */
    public void getPhoto(String phone, ThreadCallback threadCallback) {
        FormBody param = new FormBody.Builder().add("phone", phone).build();
        HttpClientUtil.post(ApiConstant.GET_USER_PHOTO, param,
                new HttpAsynResult(HttpAsynResult.Config.builder().onlyOk(true).alertError(false).animation(false).login(false)) {
                    @Override
                    public void callback(HttpResult httpResult) {
                        if(httpResult.getData()!=null&&threadCallback!=null){
                            threadCallback.callback(httpResult.getData());
                        }
                    }
                });
    }
}
