package com.lh.sms.client.ui.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ActivityManager;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.FileUtils;
import com.lh.sms.client.ui.dialog.SmAlertDialog;
import com.lh.sms.client.ui.person.user.PersonUserInfo;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ShowWebView extends AppCompatActivity {
    private static final String TAG = "ShowWebView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        //绑定事件
        bindEvent();
    }
    private String mCameraFilePath = null;
    private ValueCallback<Uri> mUploadCallBack = null;
    private ValueCallback<Uri[]> mUploadCallBackAboveL = null;
    private int REQUEST_CODE_FILE_CHOOSER = 608;
    private int permissionsCode = 101;
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void bindEvent() {
        SmAlertDialog smAlertDialog = AlertUtil.alertProcess("正在加载中...");
        //退出
        TextView viewById = findViewById(R.id.close_intent);
        viewById.setOnClickListener(v->{
            finish();
        });
        String title = getIntent().getStringExtra("title");
        viewById.setText(title);
        //显示协议内容
        WebView webView = findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient(){
            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mUploadCallBackAboveL = filePathCallback;
                showFileChooser(fileChooserParams.getAcceptTypes());
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                AlertUtil.close(smAlertDialog);
            }
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if(errorResponse.getStatusCode()==404) {
                    webView.loadUrl("file:///android_asset/500.html?code=" + errorResponse.getStatusCode());
                }
                AlertUtil.close(smAlertDialog);
            }
        });
        webView.addJavascriptInterface(new Call(),"JsBridge");
        webView.loadUrl(getIntent().getStringExtra("url"));
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCachePath(new File(ActivityManager.getInstance().getCurrentActivity().getCacheDir(),"html").getPath());
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
//支持js
        settings.setJavaScriptEnabled(true);
   //     settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//自动缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
//支持获取手势焦点
        webView.requestFocusFromTouch();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
    /**
     * 打开选择文件/相机
     * @param acceptTypes
     */
    private void showFileChooser(String ... acceptTypes) {
        //检查权限
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        //逐个判断你要的权限是否已经通过
        List<String> applyPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                applyPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }
        if(applyPermissionList.size()>0) {
            ActivityCompat.requestPermissions(this, permissions, permissionsCode);
        }else{
            //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
            Intent intent = null;
            if(ArrayUtils.contains(acceptTypes,"image/*")){
                intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }else{
                intent = new Intent(Intent.ACTION_PICK);
            }
            startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILE_CHOOSER) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null && !TextUtils.isEmpty(mCameraFilePath)) {
                // 看是否从相机返回
                File cameraFile = new File(mCameraFilePath);
                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                }
            }
            if (result != null) {
                String path = FileUtils.getPath(this, result);
                if (!TextUtils.isEmpty(path)) {
                    File f = new File(path);
                    if (f.exists() && f.isFile()) {
                        Uri newUri = Uri.fromFile(f);
                        if (mUploadCallBackAboveL != null) {
                            if (newUri != null) {
                                mUploadCallBackAboveL.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                                mUploadCallBackAboveL = null;
                                return;
                            }
                        }
                    }
                }
            }
            clearUploadMessage();
            return;
        }
    }
    /**
     * @do 申请权限回调方法
     * @author liuhua
     * @date 2020/6/7 4:37 PM
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == permissionsCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFileChooser("image/*");
            } else {
                Toast.makeText(this,"没有读取文件的权限,无法选择文件",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /**
     * webview没有选择文件也要传null，防止下次无法执行
     */
    private void clearUploadMessage() {
        if (mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL.onReceiveValue(null);
            mUploadCallBackAboveL = null;
        }
        if (mUploadCallBack != null) {
            mUploadCallBack.onReceiveValue(null);
            mUploadCallBack = null;
        }
    }
    /**
     * 回调类
     */
    public class Call{
        /**
         * 关闭窗口
         */
        @JavascriptInterface
        public void close(){
            finish();
        }
    }
}

