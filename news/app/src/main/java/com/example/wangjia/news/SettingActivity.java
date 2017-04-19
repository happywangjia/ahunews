package com.example.wangjia.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.Set;

/**
 * Created by wangjia on 2017/4/5.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_username;
    private TextView tv_logout;
    private ImageView imageView;
    String icon;
    String username;
    String motto;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        preferences=getSharedPreferences("ahu",MODE_PRIVATE);
        editor=preferences.edit();
        Toolbar toolbar= (Toolbar) findViewById(R.id.setting_toolbar);
  //      toolbar.setLogo(R.drawable.ic_menu_camera);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
        motto=preferences.getString("motto",null);
        icon=preferences.getString("icon",null);
        username=preferences.getString("username",null);
        imageView= (ImageView) findViewById(R.id.id_set_icon);
        getIcon();



        tv_username= (TextView) findViewById(R.id.id_set_username);
        tv_username.setText(username);
        tv_logout= (TextView) findViewById(R.id.id_logout);
        tv_logout.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.id_set_username:
                Toast.makeText(SettingActivity.this,"个人信息",Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_logout:
                doLogout();
                Toast.makeText(SettingActivity.this,"切换账号",Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }

    private void doLogout() {

    //    editor.putString("username",null);
        editor.putString("password",null);
        editor.commit();
        Intent logoutIntent=new Intent(SettingActivity.this,MainActivity.class);
        startActivity(logoutIntent);
        finish();

    }

    public void getIcon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url=new URL(icon);
                    imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
