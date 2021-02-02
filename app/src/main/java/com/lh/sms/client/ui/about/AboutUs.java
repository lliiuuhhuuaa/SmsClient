package com.lh.sms.client.ui.about;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.data.constant.DataConstant;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.ApplicationUtil;
import com.lh.sms.client.ui.dialog.SmAlertDialog;
import com.lh.sms.client.ui.person.template.PersonTemplateConfigDetail;
import com.lh.sms.client.ui.view.ShowWebView;
import com.lh.sms.client.work.app.entity.AppVersion;
import com.lh.sms.client.work.app.service.AppUpdateService;
import com.lh.sms.client.work.app.service.AppVersionService;

import java.security.MessageDigest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUs extends AppCompatActivity {
    private static final String TAG = "AboutUs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        //绑定事件
        bindEvent();
    }

    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    long lastClickTime = 0;
    long clickCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void bindEvent() {
        //退出
        findViewById(R.id.close_intent).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.logo_img).setOnClickListener(v -> {
            if (lastClickTime > System.currentTimeMillis() - 1000) {
                clickCount++;
            } else {
                clickCount = 0;
            }
            lastClickTime = System.currentTimeMillis();
            if (clickCount < 10) {
                return;
            }
            clickCount = 0;
            Signature[] signatures = null;
            try {
                PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                signatures = packageInfo.signingInfo.getApkContentsSigners();
            } catch (Exception e) {
                try {
                    PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
                    signatures = packageInfo.signatures;
                } catch (Exception e1) {
                }
            }
            if (signatures != null) {
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                    md.update(signatures[0].toByteArray());
                    byte[] ep = md.digest();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < ep.length; i++) {
                        if ((ep[i] & 0xff) < 0x10) {
                            sb.append("0");
                        }
                        sb.append(Long.toString(ep[i] & 0xff, 16));
                    }
                    SqlData sqlData = ObjectFactory.get(SqlData.class);
                    boolean open = YesNoEnum.isYes(sqlData.getObject(DataConstant.KEY_HIDE_MODE,Integer.class));
                    md.reset();
                    SmAlertDialog smAlertDialog = new SmAlertDialog(this);
                    smAlertDialog.setTitleText((open?"关闭":"开启")+"隐藏模式");
                    smAlertDialog.setContentText(sb.toString());
                    smAlertDialog.setConfirmText("复制并"+(open?"关闭":"开启"));
                    smAlertDialog.setConfirmListener(v1 -> {
                        smAlertDialog.cancel();
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(PersonTemplateConfigDetail.CLIPBOARD_SERVICE);
                        // 将ClipData内容放到系统剪贴板里。
                        if(cm!=null) {
                            cm.setPrimaryClip(ClipData.newPlainText("Label", sb.toString()));
                            AlertUtil.toast(this, "神秘代码已复制到粘贴板", Toast.LENGTH_SHORT);
                        }
                        if(open){
                            sqlData.deleteObject(DataConstant.KEY_HIDE_MODE);
                        }else {
                            sqlData.saveObject(DataConstant.KEY_HIDE_MODE, YesNoEnum.YES.getValue());
                        }
                        AlertUtil.toast(this, "隐藏模式已"+(open?"关闭":"开启"), Toast.LENGTH_LONG);
                    });
                    AlertUtil.alertOther(smAlertDialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        //版本名称
        String versionName = ApplicationUtil.getVersionName(this);
        TextView textView = findViewById(R.id.about_version_name);
        textView.setText("版本号 " + versionName);
        //显示版本
        refreshInfo(false);
        //检查是否有新版本
        ObjectFactory.get(AppVersionService.class).checkNewVersion(() -> {
            HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
            Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
            message.obj = new Object[]{AboutUs.this, false};
            message.getData().putString(handleMessage.METHOD_KEY, "refreshInfo");
            handleMessage.sendMessage(message);
        });
        findViewById(R.id.about_version_install).setOnClickListener(v -> {
            //显示版本
            refreshInfo(true);
        });
        //服务协议
        findViewById(R.id.agreement).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShowWebView.class);
            intent.putExtra("url", "file:///android_asset/agreement.html");
            intent.putExtra("title", "服务协议");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        //隐私协议
        findViewById(R.id.privacy).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShowWebView.class);
            intent.putExtra("url", "file:///android_asset/privacy.html");
            intent.putExtra("title", "隐私协议");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });


    }

    /**
     * @do 刷新版本信息
     * @author liuhua
     * @date 2020/5/24 10:48 PM
     */
    public void refreshInfo(Boolean showAlert) {
        TextView versionCheck = findViewById(R.id.about_version_check);
        long currVersion = ApplicationUtil.getVersion(this);
        AppVersion appVersion = ObjectFactory.get(SqlData.class).getObject(AppVersion.class);
        if (appVersion == null) {
            return;
        }
        if (appVersion.getVersion() <= currVersion) {
            versionCheck.setText("已是最新版本");
            versionCheck.setTextColor(Color.GRAY);
        } else {
            versionCheck.setText(String.format("发现新版本:%s", appVersion.getVersionName()));
            versionCheck.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        }
        if (showAlert) {
            //弹窗app更新提示
            AppUpdateService appUpdateService = ObjectFactory.get(AppUpdateService.class);
            if (appUpdateService.startCheckUpdate()) {
                appUpdateService.alert();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
