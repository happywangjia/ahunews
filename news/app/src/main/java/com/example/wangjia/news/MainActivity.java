package com.example.wangjia.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangjia.news.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class MainActivity extends AppCompatActivity {

    Button button_sms;
    ImageView input_delete;
    EditText input_username;
    EditText input_password;
    Button button_login;
    TextView tv_register;
    public final int SUCCESS = 1;
    public final int PARSER = 2;
    public static String username;
    public static String password;
    public static JSONObject cnt_json;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("ahu", MODE_WORLD_READABLE);
        editor = preferences.edit();
        String uName = preferences.getString("username", null);
        String uPsw = preferences.getString("password", null);
        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);
        input_delete = (ImageView) findViewById(R.id.input_delete);
        if (uName != null) {
            username = uName;
            input_username.setText(uName);
            input_delete.setVisibility(View.VISIBLE);
        }
        if (uName != null && uPsw != null) {
            password = uPsw;
            input_password.setText(uPsw);
            login();
        }
        input_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    input_delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    //Toast.makeText(MainActivity.this,"before+"+s.length(),Toast.LENGTH_SHORT).show();
                    input_delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    //         Toast.makeText(MainActivity.this,"after+"+s.length(),Toast.LENGTH_SHORT).show();
                    input_delete.setVisibility(View.VISIBLE);
                }
            }
        });

        button_login = (Button) findViewById(R.id.button_login);
        tv_register = (TextView) findViewById(R.id.tv_register);
        button_sms = (Button) findViewById(R.id.button_sms);
        input_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_username.setText("");
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = input_username.getText().toString().trim();
                password = input_password.getText().toString().trim();
                login();
            }
        });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        button_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent=new Intent(MainActivity.this,SmsLoginActivity.class);
                startActivity(smsIntent);
                finish();
            }
        });


    }

    public void login() {

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.length() < 2 || password.length() < 2) {
            Toast.makeText(MainActivity.this, "账号和密码至少2位", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost = getResources().getString(R.string.localhost);

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://" + localhost + "/ahu/login.php?username=" + URLEncoder.encode(username, "UTF-8") +
                            "&password=" + URLEncoder.encode(password);
                    URL url = new URL(strUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream is = connection.getInputStream();
                        String result = HttpUtils.readMyInputStream(is);
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }


            }
        }).start();
    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    ParseJson(msg.obj.toString());
                    break;
                case PARSER:
                    if (msg.obj.toString().equals("0")) {
                        editor.putString("username", username);
                        editor.putString("password", password);

                        //   Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        Intent toZhuIntent = new Intent(MainActivity.this, ZhuYeActivity.class);
//                        Bundle nextBundle = new Bundle();
//                        nextBundle.putString("username", username);
                        try {
                            String motto = cnt_json.getString("motto");
                            String icon = cnt_json.getString("icon");
                            String telephone = cnt_json.getString("tel");
                            String email = cnt_json.getString("email");
                            editor.putString("telephone", telephone);
                            editor.putString("email", email);

//                            nextBundle.putString("motto",motto);
//                            toZhuIntent.putExtras(nextBundle);
                            editor.putString("motto", motto);
                            editor.putString("icon", icon);
                            editor.commit();
                            MainActivity.this.startActivity(toZhuIntent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "账号密码不匹配", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;

            }
        }
    };

    public void ParseJson(String StringData) {
        try {
            JSONObject jsonObject = new JSONObject(StringData);
            int num = jsonObject.getInt("Num");
            Message msg = new Message();
            msg.what = PARSER;
            cnt_json = jsonObject;
            msg.obj = "" + num;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void register() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();

    }


}
