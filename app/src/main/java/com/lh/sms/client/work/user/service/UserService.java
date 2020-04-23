package com.lh.sms.client.work.user.service;

import com.lh.sms.client.work.user.entity.UserInfo;

public class UserService {
    //用户登陆
    public UserInfo login(String username,String phone){
        UserInfo userInfo = new UserInfo();
        userInfo.setPhone(username);
        return userInfo;
    }
}
