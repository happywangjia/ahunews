package com.example.wangjia.news;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.example.wangjia.news.R;

/**
 * Created by wangjia on 2017/4/5.
 */

public class CommentActivity extends AppCompatActivity {

    SharedPreferences preferences;
    public static String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
        preferences=getSharedPreferences("ahu",MODE_WORLD_READABLE);
        username=preferences.getString("username",null);
        Toolbar toolbar= (Toolbar) findViewById(R.id.comment_toolbar);
  //      toolbar.setLogo(R.drawable.ic_menu_camera);
        toolbar.setTitle("评论");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
    }
}
