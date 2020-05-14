package com.lh.sms.client.work.user.service;

import android.os.Message;

import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.ui.person.PersonFragment;
import com.lh.sms.client.work.user.entity.UserInfo;

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
}
