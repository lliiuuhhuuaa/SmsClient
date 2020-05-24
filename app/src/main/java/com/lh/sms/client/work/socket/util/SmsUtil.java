package com.lh.sms.client.work.socket.util;

import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.ResultCodeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.work.socket.entity.SendSmsByReply;
import com.lh.sms.client.work.socket.entity.SendSmsBySocket;
import com.lh.sms.client.work.socket.entity.SocketMessage;
import com.lh.sms.client.work.user.entity.UserInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * @author lh
 * @do
 * @date 2020-04-02 15:58
 */
public class SmsUtil {
    /**
     * @do 检查签名
     * @author liuhua
     * @date 2020/5/21 9:08 PM
     */
    public static boolean checkSign(SendSmsBySocket sendSmsBySocket) {
        UserInfo userInfo = ObjectFactory.get(SqlData.class).getObject(UserInfo.class);
        if(userInfo==null||userInfo.getSmsClientKey()==null){
            return false;
        }
        return sendSmsBySocket.getSign()!=null&&sendSmsBySocket.getSign().equals(sign(sendSmsBySocket, userInfo.getSmsClientKey()));
    }
    /**
     * @do 签名
     * @author lh
     * @date 2020-04-03 16:19
     */
    public static String sign(SendSmsBySocket sendSmsBySocket, String key) {
        return md5(String.format(Locale.CHINA,"appId=%s&iccId=%s&orderId=%s&phone=%s&text=%s&timestamp=%d&key=%s",sendSmsBySocket.getAppId(),sendSmsBySocket.getIccId(),
                sendSmsBySocket.getOrderId(),sendSmsBySocket.getPhone(),sendSmsBySocket.getText(),sendSmsBySocket.getTimestamp(),key));
    }
    /**
     * MD5 32为加密
     *
     * @param str 明文
     *            字符集编码
     * @return 32位密文
     */
    public static String md5(String str) {
        byte[] unencodedStr;
        StringBuffer buf = new StringBuffer();
        if (str == null) {
            return "";
        }
        unencodedStr = str.getBytes(StandardCharsets.UTF_8);
        return md5Eencrypt(unencodedStr);
    }

    /**
     * @param str
     * @return
     * @time 2019年1月22日
     * @desc MD5 32为加密
     */
    public static String md5Eencrypt(byte[] str) {
        StringBuffer buf = new StringBuffer();
        try {
            if (str == null) {
                return "";
            }
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.reset();
            md.update(str);
            byte[] encodedPassword = md.digest();

            for (int i = 0; i < encodedPassword.length; i++) {
                if ((encodedPassword[i] & 0xff) < 0x10) {
                    buf.append("0");
                }
                buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
            }
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        return buf.toString();
    }
    /**
     * @do 获取错误返回数据
     * @author liuhua
     * @date 2020/5/21 9:37 PM
     */
    public static SocketMessage errorBody(SendSmsBySocket sendSmsBySocket, boolean receive, String msg) {
        //获取用户信息
        UserInfo userInfo = ObjectFactory.get(SqlData.class).getObject(UserInfo.class);
        if(userInfo==null){
            return null;
        }
        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setCode(ResultCodeEnum.OK.getValue());
        socketMessage.setMsg(msg);
        SendSmsByReply sendSmsByReply = new SendSmsByReply();
        sendSmsByReply.setOrderId(sendSmsBySocket.getOrderId());
        sendSmsByReply.setReceive(receive?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
        sendSmsByReply.setMsg(msg);
        sendSmsByReply.setState(YesNoEnum.NO.getValue());
        sendSmsByReply.setTimestamp(System.currentTimeMillis());
        sendSmsByReply.setSign(md5(String.format(Locale.CHINA,"msg=%s&orderId=%s&receive=%d&state=%d&timestamp=%d&key=%s",sendSmsByReply.getMsg(),
                sendSmsByReply.getOrderId(),sendSmsByReply.getReceive(),sendSmsByReply.getState(),sendSmsByReply.getTimestamp(),userInfo.getSmsClientKey())));
        socketMessage.setBody(sendSmsByReply);
        return socketMessage;
    }
    /**
     * @do 成功返回消息
     * @author liuhua
     * @date 2020/5/21 9:59 PM
     */
    public static Object okBody(SendSmsBySocket sendSmsBySocket, boolean receive) {
        //获取用户信息
        UserInfo userInfo = ObjectFactory.get(SqlData.class).getObject(UserInfo.class);
        if(userInfo==null){
            return null;
        }
        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setCode(ResultCodeEnum.OK.getValue());
        socketMessage.setMsg("ok");
        SendSmsByReply sendSmsByReply = new SendSmsByReply();
        sendSmsByReply.setOrderId(sendSmsBySocket.getOrderId());
        sendSmsByReply.setMsg(socketMessage.getMsg());
        sendSmsByReply.setReceive(receive?YesNoEnum.YES.getValue():YesNoEnum.NO.getValue());
        sendSmsByReply.setState(YesNoEnum.YES.getValue());
        sendSmsByReply.setTimestamp(System.currentTimeMillis());
        sendSmsByReply.setSign(md5(String.format(Locale.CHINA,"msg=%s&orderId=%s&receive=%d&state=%d&timestamp=%d&key=%s",sendSmsByReply.getMsg(),
                sendSmsByReply.getOrderId(),sendSmsByReply.getReceive(),sendSmsByReply.getState(),sendSmsByReply.getTimestamp(),userInfo.getSmsClientKey())));
        socketMessage.setBody(sendSmsByReply);
        return socketMessage;
    }
}
