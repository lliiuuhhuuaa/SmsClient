package com.lh.sms.client.framing;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @do app管理
 * @author liuhua
 * @date 2020/5/27 9:13 PM
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initLifeCycle();
    }
    /**
     * @do 监听activity生命周期
     * @author liuhua
     * @date 2020/5/27 9:14 PM
     */
    private void initLifeCycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                ActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

}
