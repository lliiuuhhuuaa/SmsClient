package com.lh.sms.client.ui.dialog;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ActivityManager;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.ui.loading.LoadingView;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SmAlertDialog extends Dialog {
    private Context context;
    /**
     * 是否显示取消按钮
     */
    private boolean cancelButton = true;
    /**
     * 是否显示确认按钮
     */
    private boolean confirmButton = true;
    private CharSequence title;
    private CharSequence content;
    private View contentView;
    private CharSequence cancelText;
    private CharSequence confirmText;
    private View.OnClickListener cancelClickListener;
    private View.OnClickListener confirmClickListener;
    /**
     * 是否加载动画
     */
    private boolean loading = false;
    private LoadingView loadingView;
    public SmAlertDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }
    public SmAlertDialog(Context context,boolean loading) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.loading = loading;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        if(this.loading){
            //加载动画
            loadingView = new LoadingView(context);
            super.setContentView(loadingView);
            return;
        }
        setContentView(R.layout.alert_dialog);
        initEvent();

    }
    /**
     * @do 初始化事件
     * @author liuhua
     * @date 2020/5/28 8:03 PM
     */
    private void initEvent(){
        TextView textView = findViewById(R.id.alert_dialog_button_cancel);
        textView.setOnClickListener(v-> this.cancel());
    }
    /**
     * @do 设置标题
     * @author liuhua
     * @date 2020/5/28 8:20 PM
     */
    public SmAlertDialog setTitleText(String title) {
        this.title = title;
        return this;
    }
    /**
     * @do 设置内容
     * @author liuhua
     * @date 2020/5/28 8:17 PM
     */
    public SmAlertDialog setContentText(@Nullable CharSequence text) {
       this.content = text;
        return this;
    }
    /**
     * @do 设置自定义内容
     * @author liuhua
     * @date 2020/5/28 8:17 PM
     */
    public void setContentView(@NonNull View view) {
        this.contentView = view;
    }

    /**
     * @do 设置是否显示取消按钮
     * @author liuhua
     * @date 2020/5/28 7:54 PM
     */
    public SmAlertDialog setCancelText(boolean show) {
        this.cancelButton = show;
        return this;
    }
    /**
     * @do 设置取消按钮文字
     * @author liuhua
     * @date 2020/5/28 7:55 PM
     */
    public SmAlertDialog setCancelText(@NonNull CharSequence text) {
        this.cancelButton = true;
        this.cancelText = text;
        return this;
    }
    /**
     * @do 设置取消按钮点击事件
     * @author liuhua
     * @date 2020/5/28 7:55 PM
     */
    public SmAlertDialog setCancelListener(@NonNull View.OnClickListener clickListener) {
        this.cancelButton = true;
        this.cancelClickListener = clickListener;
        return this;
    }
    /**
     * @do 设置是否显示确认按钮
     * @author liuhua
     * @date 2020/5/28 7:54 PM
     */
    public SmAlertDialog setConfirmText(boolean show) {
        this.confirmButton = show;
        return this;
    }
    /**
     * @do 设置确认按钮文字
     * @author liuhua
     * @date 2020/5/28 7:54 PM
     */
    public SmAlertDialog setConfirmText(@NonNull CharSequence text) {
        this.confirmButton = true;
        this.confirmText = text;
        return this;
    }
    /**
     * @do 设置取消按钮点击事件
     * @author liuhua
     * @date 2020/5/28 7:55 PM
     */
    public SmAlertDialog setConfirmListener(@NonNull View.OnClickListener clickListener) {
        this.confirmButton = true;
        this.confirmClickListener = clickListener;
        return this;
    }
    @Override
    public void show() {
        if (!((Activity)context).isFinishing() && !this.isShowing()) {
            super.show();
        }
        if(this.loading){
            if(content!=null) {
                TextView textView = loadingView.findViewById(R.id.loading_text);
                textView.setText(content);
            }
            return;
        }
        if(title!=null) {
            TextView textView = findViewById(R.id.alert_dialog_title_text);
            textView.setText(title);
        }
        if(content!=null){
            TextView textView = findViewById(R.id.alert_dialog_content_text);
            textView.setText(content);
        }
        if(cancelButton){
            TextView textView = findViewById(R.id.alert_dialog_button_cancel);
            textView.setVisibility(View.VISIBLE);
            if(cancelText!=null) {
                textView.setText(cancelText);
            }
            if(cancelClickListener!=null) {
                textView.setOnClickListener(cancelClickListener);
            }
        }
        if(confirmButton){
            TextView textView = findViewById(R.id.alert_dialog_button_ok);
            textView.setVisibility(View.VISIBLE);
            if(confirmText !=null) {
                textView.setText(confirmText);
            }
            if(confirmClickListener !=null) {
                textView.setOnClickListener(confirmClickListener);
            }
        }
        if(contentView!=null){
            findViewById(R.id.alert_dialog_content_text).setVisibility(View.GONE);
            LinearLayout linearLayout = findViewById(R.id.alert_dialog_content);
            linearLayout.addView(contentView);
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if(this.loading){
            loadingView.clearAnimation();
        }
    }
}
