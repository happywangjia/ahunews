package com.example.wangjia.news;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wangjia.news.utils.Base64Tools;
import com.example.wangjia.news.utils.HttpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by wangjia on 2017/4/13.
 */

public class ContentCommentActivity extends AppCompatActivity {

    public final int SUCCESS=1;
    public EditText editText;
    public Button bt_submit;
    public static String username;
    public static String title;
    String text;
    SharedPreferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_comment);
        editText= (EditText) findViewById(R.id.id_comment_edit);
        bt_submit= (Button) findViewById(R.id.id_comment_submit);
 //       username=getIntent().getExtras().getString("username");
        preferences = getSharedPreferences("ahu", MODE_WORLD_READABLE);
        username=preferences.getString("username",null);
        title=getIntent().getExtras().getString("title");



        Toolbar toolbar= (Toolbar) findViewById(R.id.content_comment_toolbar);
        toolbar.setTitle("评论");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(v.getId()==R.id.id_comment_submit){
                        text=editText.getText().toString().trim();
                        doComment(text);
                    }
            }
        });
    }

    /**
     * 注意：url中不能含有中文，故中文使用base64转码，在后台使用base64解码
     *
     *
     * @param text
     */

    private void doComment(String text) {
        final String comment_text=text;
        new Thread(new Runnable() {
            @Override
            public void run() {

                BufferedReader reader = null;
                HttpURLConnection connection = null;
                try {

                    String localhost=getResources().getString(R.string.localhost);
                    String strUrl="http://"+localhost+"/ahu/addComment.php?" +
                            "title="+ URLEncoder.encode(title,"UTF-8")+"&comment="
                            +URLEncoder.encode(comment_text,"UTF-8")+"&username="+URLEncoder.encode(username,"UTF-8");
         //           System.out.println(strUrl);
                    URL url = new URL(strUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    int code=connection.getResponseCode();
                    if(code==200){
                        InputStream is=connection.getInputStream();
                        String result= HttpUtils.readMyInputStream(is);
                        Message msg=new Message();
                        msg.obj=result;
                        msg.what=SUCCESS;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==SUCCESS){
                Toast.makeText(ContentCommentActivity.this,"添加评论成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };
}
