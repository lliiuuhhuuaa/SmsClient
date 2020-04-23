package com.lh.sms.client.ui.dialog.person.balance;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.lh.sms.client.R;

public class SelectDialog extends Dialog {
    public SelectDialog(Context context) {
        super(context, R.style.SelectDialog);
    }

    public SelectDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_dialog);
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y=-16;
        dialogWindow.setAttributes(lp);
    }
}
