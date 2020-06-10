package com.lh.sms.client.ui.person.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.sms.client.R;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.framing.ObjectFactory;
import com.lh.sms.client.framing.entity.ThreadCallback;
import com.lh.sms.client.framing.enums.HandleMsgTypeEnum;
import com.lh.sms.client.framing.enums.YesNoEnum;
import com.lh.sms.client.framing.handle.HandleMsg;
import com.lh.sms.client.ui.view.CircleImageView;
import com.lh.sms.client.work.storage.service.StorageService;
import com.lh.sms.client.work.storage.util.ImageUtil;
import com.lh.sms.client.work.user.entity.UserInfo;
import com.lh.sms.client.work.user.entity.UserInfoByUpdate;
import com.lh.sms.client.work.user.service.UserService;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PersonUserInfo extends AppCompatActivity {
    private Integer permissionsCode = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_user_info);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        findViewById(R.id.close_intent).setOnClickListener(v->{
            finish();
        });
        //去注册
//        findViewById(R.id.person_login_register).setOnClickListener(v->{
//            Intent intent=new Intent(this, PersonRegister.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//        });
        //退出
        findViewById(R.id.person_user_info_exit).setOnClickListener(v->{
            ObjectFactory.get(UserService.class).unLogin();
            finish();
        });
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        UserInfo userInfo = sqlData.getObject(UserInfo.class);
        if(userInfo!=null) {
            showUserInfo();
            //昵称
            findViewById(R.id.person_user_info_nickname_item).setOnClickListener(v -> {
                Intent intent = new Intent(this, PersonUserInfoUpdate.class);
                intent.putExtra("nickname", userInfo.getNickname());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
            //手机号
            findViewById(R.id.person_user_info_phone_item).setOnClickListener(v -> {
                Intent intent = new Intent(this, PersonUserInfoUpdatePhone.class);
                intent.putExtra("phone", userInfo.getPhone());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
            //密码
            findViewById(R.id.person_user_info_password_item).setOnClickListener(v -> {
                Intent intent = new Intent(this, PersonUserInfoUpdate.class);
                intent.putExtra("password", YesNoEnum.YES.getValue());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
    }
    /**
     * @do 显示用户信息
     * @author liuhua
     * @date 2020/4/26 10:55 PM
     */
    public void showUserInfo(){
        SqlData sqlData = ObjectFactory.get(SqlData.class);
        UserInfo userInfo = sqlData.getObject(UserInfo.class);
        if(userInfo==null){
            return;
        }
        TextView textView = findViewById(R.id.person_user_info_nickname);
        textView.setText(userInfo.getNickname());
        textView = findViewById(R.id.person_user_info_phone);
        textView.setText(userInfo.getPhone());
        ImageView imageView = findViewById(R.id.person_user_info_photo);
        if(StringUtils.isBlank(userInfo.getPhoto())){
            imageView.setImageResource(R.drawable.ic_head_black_64dp);
        }else{
            ImageUtil.loadImage(this,userInfo.getPhoto(), o -> {
                HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                message.obj = new Object[]{this, o};
                message.getData().putString(HandleMsg.METHOD_KEY, "showUrlImage");
                handleMessage.sendMessage(message);
            });
        }
        //选择图片上传
        findViewById(R.id.person_user_info_photo_item).setOnClickListener(v->{
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            //逐个判断你要的权限是否已经通过
            List<String> applyPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    applyPermissionList.add(permissions[i]);//添加还未授予的权限
                }
            }
            if(applyPermissionList.size()>0) {
                ActivityCompat.requestPermissions(PersonUserInfo.this, permissions, permissionsCode);
            }else{
                choosePhoto();
            }
        });
    }
    /**
     * @do 显示头像
     * @author liuhua
     * @date 2020/6/9 7:40 PM
     */
    public void showUrlImage(Bitmap bitmap){
        if(bitmap!=null) {
            ImageView imageView = findViewById(R.id.person_user_info_photo);
            imageView.setImageBitmap(bitmap);
        }

    }
    /**
     * 打开相册
     */
    private Integer CHOOSE_PHOTO = 10;
    private Integer CROP_PHOTO = 12;
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, CHOOSE_PHOTO);
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
                choosePhoto();
            } else {
                Toast.makeText(this,"没有读取文件的权限,无法上传头像",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File file = new File(getExternalCacheDir(), "image-cropped");
        if (requestCode == CHOOSE_PHOTO && resultCode == Activity.RESULT_OK&& null != data) {
            Uri uri = data.getData();//获取路径
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri,"image/*");
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            // 上面设为false的时候将MediaStore.EXTRA_OUTPUT即"output"关联一个Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                    file));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true); // no face detection
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CROP_PHOTO);
            } else {
                // 没有安装所需应用
                showImage(uri);
            }

        }else if(requestCode ==CROP_PHOTO && resultCode == RESULT_OK){
            Uri uri = Uri.fromFile(file);
            showImage(uri);

        }
    }
    /**
     * @do 显示图片并上传
     * @author liuhua
     * @date 2020/6/7 11:17 PM
     */
    private void showImage(Uri uri){
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            CircleImageView imageView = findViewById(R.id.person_user_info_photo);
            imageView.setImageBitmap(bmp);
            ObjectFactory.get(StorageService.class).upload(this, bmp, new ThreadCallback<String>(){
                @Override
                public void callback(String s) {
                    UserInfoByUpdate userInfoByUpdate = new UserInfoByUpdate();
                    userInfoByUpdate.setPhoto(s);
                    HandleMsg handleMessage = ObjectFactory.get(HandleMsg.class);
                    Message message = Message.obtain(handleMessage, HandleMsgTypeEnum.CALL_BACK.getValue());
                    message.obj = new Object[]{ObjectFactory.get(UserService.class), userInfoByUpdate};
                    message.getData().putString(HandleMsg.METHOD_KEY, "updateUserInfo");
                    handleMessage.sendMessage(message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        showUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
