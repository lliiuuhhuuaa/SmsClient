package com.lh.sms.client.work.storage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.entity.ThreadCallback;
import com.lh.sms.client.framing.util.ThreadPool;
import com.lh.sms.client.work.storage.enums.StorageEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class ImageUtil {
    /**
     * @do 下载图片
     * @author liuhua
     * @date 2020/6/9 7:54 PM
     */
    public static void loadImage(Context context,String id, ThreadCallback threadCallback) {
        //先检查是否存在缓存
        String key = StorageEnum.getKeyPath(id);
        File file = new File(context.getCacheDir(),key);
        if(file.exists()){
            threadCallback.callback(BitmapFactory.decodeFile(file.toString()));
            return;
        }
        String domain = ObjectFactory.get(SqlData.class).getObject(DataConstant.STORAGE_DOMAIN, String.class);
        ThreadPool.exec(() -> {
            FileOutputStream fileOutputStream = null;
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(domain.concat("/").concat(key)).openStream());
                if (bitmap != null) {
                    File parent = file.getParentFile();
                    if(!parent.exists()){
                        parent.mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    threadCallback.callback(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fileOutputStream!=null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
