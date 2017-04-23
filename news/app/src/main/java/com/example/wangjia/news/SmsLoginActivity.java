package com.example.wangjia.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by wangjia on 2017/4/23.
 */

public class SmsLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_submit;
    private TextView tv_get_code;
    private EditText et_phone;
    private EditText et_code;
    private TextView tv_sms;
    private TextView tv_register;
    public static final int SUCCESS = 1;
    public static String telephone;
    public static JSONObject cnt_json;
    public final int PARSER = 2;
    public static String motto;
    public static String email;
    public static String icon;
    public static final int CODE_ERROR=5;
    public static final int SEND_SUCCESS=6;
    public static final int SEND_FAIL=7;
    public static String username;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_sms_login);
        SharedPreferences preferences = getSharedPreferences("ahu", MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("telephone", null);
        editor.putString("email", null);
        editor.putString("motto", null);
        editor.putString("icon", null);
        editor.putString("username", null);
        editor.commit();
        SMSSDK.initSDK(this, "1d425e4d1156f", "c7eb3b9880cdf1ac875f69d530a75184");
        Toolbar toolbar = (Toolbar) findViewById(R.id.sms_login_toolbar);
        //      toolbar.setLogo(R.drawable.ic_menu_camera);
        toolbar.setTitle("短信验证");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
        bt_submit = (Button) findViewById(R.id.bt_submit_code);
        tv_get_code = (TextView) findViewById(R.id.tv_get_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        tv_sms = (TextView) findViewById(R.id.tv_sms_user);
        tv_register = (TextView) findViewById(R.id.tv_back_register);
        bt_submit.setOnClickListener(this);
        tv_get_code.setOnClickListener(this);
        tv_sms.setOnClickListener(this);
        tv_register.setOnClickListener(this);

        SMSSDK.registerEventHandler(new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                switch (event) {
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            skipMain();
                        } else {
                            handler.sendEmptyMessage(CODE_ERROR);
                        }
                        break;
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            handler.sendEmptyMessage(SEND_SUCCESS);
                        } else {
                            handler.sendEmptyMessage(SEND_FAIL);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void skipMain() {
        SharedPreferences preferences = getSharedPreferences("ahu", MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("telephone", telephone);
        editor.putString("email", email);
        editor.putString("motto", motto);
        editor.putString("icon", icon);
        editor.putString("username", username);
        editor.commit();
        Intent intent = new Intent(SmsLoginActivity.this, ZhuYeActivity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit_code:
                submitCode();
                break;
            case R.id.tv_get_code:
                //      tv_get_code.requestFocus();
                Toast.makeText(SmsLoginActivity.this, "获取验证码", Toast.LENGTH_SHORT).show();
                getCode();
                break;
            case R.id.tv_sms_user:
                Intent uIntent = new Intent(SmsLoginActivity.this, MainActivity.class);
                startActivity(uIntent);
                finish();
                break;
            case R.id.tv_back_register:
                Intent rIntent = new Intent(SmsLoginActivity.this, RegisterActivity.class);
                startActivity(rIntent);
                finish();
                break;
            default:
                break;

        }
    }

    private void submitCode() {
        String code = et_code.getText().toString().trim();
        if (code.length() != 4) {
            Toast.makeText(SmsLoginActivity.this, "验证码长度为4", Toast.LENGTH_SHORT).show();
            return;
        }
        SMSSDK.submitVerificationCode("86", telephone, code);

    }

    public void getCode() {
        String tel = et_phone.getText().toString().trim();
        if (isTelephone(tel) == false) return;
        telephone = tel;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost = getResources().getString(R.string.localhost);

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://" + localhost + "/ahu/telLogin.php?tel=" + telephone;
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
                        try {
                            motto = cnt_json.getString("motto");
                            icon = cnt_json.getString("icon");
                            telephone = cnt_json.getString("tel");
                            email = cnt_json.getString("email");
                            username = cnt_json.getString("username");
                            SMSSDK.getVerificationCode("86", telephone);
                            timer.start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SmsLoginActivity.this, "该手机号未注册", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CODE_ERROR:
                    Toast.makeText(SmsLoginActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                    break;
                case SEND_SUCCESS:
                    Toast.makeText(SmsLoginActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                    break;
                case SEND_FAIL:
                    Toast.makeText(SmsLoginActivity.this,"验证码发送失败",Toast.LENGTH_SHORT).show();
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

    private boolean isTelephone(String tel) {
        if (tel.isEmpty()) return false;
        Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
        Matcher matcher = regex.matcher(tel);
        if (matcher.matches())
            return true;
        Toast.makeText(SmsLoginActivity.this, "手机号码格式有误", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //防止使用短信验证 产生内存溢出问题
        SMSSDK.unregisterAllEventHandler();
    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_get_code.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            tv_get_code.setEnabled(true);
            tv_get_code.setText("获取验证码");
        }
    };

}
