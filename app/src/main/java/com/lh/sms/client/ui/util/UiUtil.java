package com.lh.sms.client.ui.util;

import android.widget.Button;

import com.lh.sms.client.R;

public class UiUtil {
    /**
     * @do button启用禁用
     * @author liuhua
     * @date 2020/4/28 9:53 PM
     */
    public static void buttonEnable(Button button, boolean bool){
        if(bool) {
            button.setBackgroundResource(R.color.colorPrimary);
            button.setEnabled(true);
        }else{
            button.setBackgroundResource(R.color.primary_tran_5);
            button.setEnabled(false);
        }
    }
}
