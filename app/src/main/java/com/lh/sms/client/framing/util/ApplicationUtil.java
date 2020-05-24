package com.lh.sms.client.framing.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * @do 应用工具
 * @author liuhua
 * @date 2020/5/24 10:23 PM
 */
public class ApplicationUtil {
    /**
     * @do 获取版本号
     * @author liuhua
     * @date 2020/5/24 10:22 PM
     */
    public static long getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        long code = 0l;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                code = info.getLongVersionCode();
            }else{
                code = info.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }
    /**
     * @do 获取版本名称
     * @author liuhua
     * @date 2020/5/24 10:26 PM
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(getVersion(context));
    }
}
