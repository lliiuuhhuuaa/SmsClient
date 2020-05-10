package com.lh.sms.client;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.fastjson.parser.ParserConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lh.sms.client.config.service.ConfigService;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.framing.util.AlertUtil;
import com.lh.sms.client.framing.util.ThreadPool;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        init();
        //请求权限
        requestPermission();
    }
    /**
     * @do 初始化
     * @author liuhua
     * @date 2020/4/21 8:14 PM
     */
    private void init() {
        ObjectFactory.push(this);
        //初始化本地数据库
        ObjectFactory.push(new SqlData());
        //初始化消息处理器
        ObjectFactory.push(new HandleMsg());
        //线程处理
        ThreadPool.createNewThread(() -> {
            //更新本地配置
            ObjectFactory.get(ConfigService.class).updateConfig();
        });

    }
    /**
     * @do 请求权限
     * @author liuhua
     * @date 2020/3/13 5:23 PM
     */
    private void requestPermission() {
        List<PermissionItem> permissionItems = new ArrayList<>();
        permissionItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, "手机信息(请求发送短信时需要识别sim卡信息)", R.drawable.permission_ic_phone));
        permissionItems.add(new PermissionItem(Manifest.permission.SEND_SMS, "发送短信(发送短信时使用)", R.drawable.permission_ic_sms));
        HiPermission.create(MainActivity.this)
                .title("权限申请")
                .msg("为了拥有更好的体验,请允许以下权限")
                .permissions(permissionItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        AlertUtil.toast(MainActivity.this,"因未给予权限,发送信息服务不可用", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }
}
