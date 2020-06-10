package com.lh.sms.client.work.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class VersionUtil {
    /**
     * @do 获取文件MD5
     * @author liuhua
     * @date 2020/6/5 7:56 下午
     */
    public static String getFileMd5(File file){
        if(file==null||!file.exists()){
            return null;
        }
        MessageDigest digest = null;
        FileInputStream inputStream = null;
        byte[] bytes = new byte[2048];
        try {
            inputStream = new FileInputStream(file);
            digest = MessageDigest.getInstance("MD5");
            int len;
            while ((len=inputStream.read(bytes))>0) {
                digest.update(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return String.format("%x", new BigInteger(digest.digest()).abs());
    }
}
