package com.example.wangjia.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wangjia.news.utils.HttpUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by wangjia on 2017/4/1.
 */

public class RegisterActivity extends AppCompatActivity {
    EditText reg_username;
    EditText reg_password;
    EditText reg_next_password;
    Button button_reg_register;
    public final int SUCCESS=1;
    public final int PASER=4;
    public final int REG_SUCCESS=2;
    public final int REG_FAIL=3;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        reg_username = (EditText) findViewById(R.id.reg_username);
        reg_password = (EditText) findViewById(R.id.reg_password);
        reg_next_password = (EditText) findViewById(R.id.reg_next_password);
        button_reg_register = (Button) findViewById(R.id.button_reg_register);
        button_reg_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    public void register() {
        String username = reg_username.getText().toString().trim();
        String pass = reg_password.getText().toString().trim();
        String nextPass = reg_next_password.getText().toString().trim();
        if (username.isEmpty() || pass.isEmpty() || nextPass.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "请输入数据", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.equals(nextPass) == false) {
            reg_password.setText("");
            reg_next_password.setText("");
            Toast.makeText(RegisterActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
            return;
        }
        if(username.length()<6||pass.length()<6){
            reg_username.setText("");
            reg_password.setText("");
            reg_next_password.setText("");
            Toast.makeText(RegisterActivity.this, "用户名和密码长度限制在6~18", Toast.LENGTH_SHORT).show();
            return;
        }

        doRegister(username, pass);

    }

    public void doRegister(final String username, final String password) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost=getResources().getString(R.string.localhost);
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://"+localhost+"/ahu/register.php?username="+ URLEncoder.encode(username,"UTF-8")+"&password="+password;
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
    public void ParseJson(String StringData){
        try{
            JSONObject jsonObject=new JSONObject(StringData);
            int num=jsonObject.getInt("Num");
            Message msg=new Message();
            msg.what=PASER;
            msg.obj=""+num;
            handler.sendMessage(msg);

//            JSONArray jsonArray=new JSONArray(StringData);
//
//            for(int i=0;i<jsonArray.length();i++){
//                JSONObject jsonObject=jsonArray.getJSONObject(i);
//                int num=jsonObject.getInt("Num");
//            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case SUCCESS:
                    ParseJson(msg.obj.toString());
                    break;
                case PASER:
                    if(msg.obj.toString().equals("0")){
                        Toast.makeText(RegisterActivity.this,"注册成功，请登录",Toast.LENGTH_SHORT).show();
                        preferences=getSharedPreferences("ahu",MODE_WORLD_READABLE);
                        editor=preferences.edit();
                        editor.putString("username",reg_username.getText().toString().trim());
                        editor.commit();
                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        RegisterActivity.this.startActivity(intent);
                        finish();
                        break;
                    }else{
                        reg_username.setText("");
                        reg_password.setText("");
                        reg_next_password.setText("");
                        Toast.makeText(RegisterActivity.this,"账号已存在",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                        break;
            }
        }
    };


}
