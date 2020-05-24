package com.lh.sms.client.ui.util;

import android.widget.Button;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.enums.YesNoEnum;

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
            button.setTag(YesNoEnum.YES.getValue());
        }else{
            button.setBackgroundResource(R.color.colorPrimaryGray);
            button.setEnabled(false);
            button.setTag(YesNoEnum.NO.getValue());
        }
    }
}
