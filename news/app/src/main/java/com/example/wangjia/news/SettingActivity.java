package com.example.wangjia.news;

import android.os.Message;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangjia.news.utils.CircleImg;
import com.example.wangjia.news.utils.FileUtil;
import com.example.wangjia.news.utils.SelectPicPopupWindow;

import java.io.File;
import java.net.URL;

/**
 * Created by wangjia on 2017/4/5.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_username;
    private TextView tv_logout;
    private EditText et_motto;
    private EditText et_telephone;
    private EditText et_email;
    private Button bt_submit;
    private Button bt_cancel;
    private SelectPicPopupWindow menuWindow;
    private static ProgressDialog pd;



    private String urlpath;			// 图片本地路径
    private static final int REQUESTCODE_PICK = 0;		// 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;		// 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;	// 图片裁切标记
    private static String IMAGE_FILE_NAME;
    private static final int GET_DRAWABLE=4;

    private CircleImg imageView;
    String icon;
    String username;
    String motto;
    String telephone;
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        preferences = getSharedPreferences("ahu", MODE_PRIVATE);
        editor = preferences.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        //      toolbar.setLogo(R.drawable.ic_menu_camera);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
        motto = preferences.getString("motto", null);
        icon=preferences.getString("icon",null);
        username = preferences.getString("username", null);
        telephone = preferences.getString("telephone", null);
        email = preferences.getString("email", null);
        imageView = (CircleImg) findViewById(R.id.id_set_icon);
        et_motto = (EditText) findViewById(R.id.id_set_motto);
        et_telephone = (EditText) findViewById(R.id.id_set_telephone);
        et_email = (EditText) findViewById(R.id.id_set_email);
        bt_submit = (Button) findViewById(R.id.id_set_submit);
        bt_cancel = (Button) findViewById(R.id.id_set_cancel);
        tv_username = (TextView) findViewById(R.id.id_set_username);
        tv_logout = (TextView) findViewById(R.id.id_logout);
        tv_logout.setOnClickListener(this);
        tv_username.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        imageView.setOnClickListener(this);
        IMAGE_FILE_NAME=username+".jpg";
        getData();
    }

    public void getData() {
        getIcon();
        et_motto.setText(motto);
        if (!telephone.equals("null"))
            et_telephone.setText(telephone);
        else
            et_telephone.setText("");
        if (!email.equals("null"))
            et_email.setText(email);
        else
            et_email.setText("");
        tv_username.setText(username);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_set_username:
                Toast.makeText(SettingActivity.this, "个人信息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_logout:
                doLogout();
                break;
            case R.id.id_set_cancel:
                getData();
                Toast.makeText(SettingActivity.this, "cancel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_set_telephone:

                Toast.makeText(SettingActivity.this, "submit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_set_icon:
                changIcon();
                break;
            default:
                break;
        }
    }

    private void changIcon() {
        menuWindow=new SelectPicPopupWindow(SettingActivity.this,itemsOnClick);
        menuWindow.showAtLocation(findViewById(R.id.id_set_layout),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            urlpath = FileUtil.saveFile(SettingActivity.this, username+".jpg", photo);
            imageView.setImageDrawable(drawable);

            // 新线程后台上传服务端
         //   pd = ProgressDialog.show(SettingActivity.this, null, "正在上传图片，请稍候...");
         //   new Thread(uploadImageRunnable).start();
        }
    }




    private void doLogout() {

        //    editor.putString("username",null);
        editor.putString("password", null);
        editor.commit();
        Intent logoutIntent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(logoutIntent);
        finish();

    }

    public void getIcon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(icon);
                    Drawable drawable=Drawable.createFromStream(url.openStream(),"icon.jpg");
//                    imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
                    Message msg=new Message();
                    msg.what=GET_DRAWABLE;
                    msg.obj=drawable;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                return false;
            default:
                return super.onKeyDown(keyCode,event);
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DRAWABLE:
                    imageView.setImageDrawable((Drawable) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


}
