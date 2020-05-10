package com.lh.sms.client.ui.dialog.person.balance;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.entity.HttpAsynResult;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.AppCompatButton;

public class SelectDialog extends Dialog {
    private List<String[]> items = null;
    private Context context;
    public SelectDialog(Context context, List<String[]>items) {
        super(context, R.style.SelectDialog);
        this.items = items;
        this.context = context;
    }

    public SelectDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_dialog);
        LinearLayout linearLayout = findViewById(R.id.select_box);
        for (String[] item : items) {
            Button button = new Button(context);
            button.setWidth(80);
            button.setPadding(5,5,5,5);
            button.setText(item[0]);
            button.setTag(item[1]);
            button.setBackgroundResource(R.color.click_grey);
            button.setTextSize(18f);
            button.setOnClickListener(v->{
                try {
                    Method method = context.getClass().getMethod("searchByType", View.class);
                    method.invoke(context,v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            linearLayout.addView(button);
        }
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y=-16;
        dialogWindow.setAttributes(lp);
    }
}
