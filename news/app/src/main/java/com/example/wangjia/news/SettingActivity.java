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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangjia.news.utils.CircleImg;

import java.net.URL;
import java.util.Set;

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
        icon = preferences.getString("icon", null);
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
                    imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
