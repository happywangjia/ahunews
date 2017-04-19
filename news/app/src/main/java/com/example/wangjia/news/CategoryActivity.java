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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by wangjia on 2017/4/5.
 */

public class CategoryActivity extends AppCompatActivity {

    private final int SUCCESS = 1;
    private CheckBox checkAmuse;
    private CheckBox checkTechnology;
    private CheckBox checkCulture;
    private CheckBox checkLife;
    private CheckBox checkLearning;
    private CheckBox checkMilitary;
    private CheckBox checkJoke;
    public boolean isAmuse = false;
    public boolean isTechnology = false;
    public boolean isCulture = false;
    public boolean isLife = false;
    public boolean isLearning = false;
    public boolean isMilitary = false;
    public boolean isJoke = false;
    private Button bt_submit;
    public static String str = "";
    public String username;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category);
        preferences = getSharedPreferences("ahu", MODE_WORLD_READABLE);
        username = preferences.getString("username", null);
        //       username = getIntent().getExtras().getString("username");
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_toolbar);
//        toolbar.setLogo(R.drawable.ic_menu_camera);
        toolbar.setTitle("分类");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
        checkAmuse = (CheckBox) findViewById(R.id.id_cat_amuse);
        checkTechnology = (CheckBox) findViewById(R.id.id_cat_technology);
        checkLife = (CheckBox) findViewById(R.id.id_cat_life);
        checkLearning = (CheckBox) findViewById(R.id.id_cat_learning);
        checkCulture = (CheckBox) findViewById(R.id.id_cat_culture);
        checkMilitary = (CheckBox) findViewById(R.id.id_cat_military);
        checkJoke = (CheckBox) findViewById(R.id.id_cat_joke);
        bt_submit = (Button) findViewById(R.id.id_cat_submit);
        checkAmuse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAmuse = true;
                } else {
                    isAmuse = false;
                }
            }
        });
        checkTechnology.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isTechnology = true;
                } else {
                    isTechnology = false;
                }
            }
        });
        checkLife.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isLife = true;
                } else {
                    isLife = false;
                }
            }
        });
        checkLearning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isLearning = true;
                } else {
                    isLearning = false;
                }
            }
        });
        checkCulture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCulture = true;
                } else {
                    isCulture = false;
                }
            }
        });
        checkMilitary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isMilitary = true;
                } else {
                    isMilitary = false;
                }
            }
        });
        checkJoke.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isJoke = true;
                } else {
                    isJoke = false;
                }
            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });
    }

    private void doSubmit() {
        if (isAmuse) {
            str += "amuse=1&";
        } else {
            str += "amuse=0&";
        }
        if (isTechnology) {
            str += "technology=1&";
        } else {
            str += "technology=0&";
        }
        if (isLife) {
            str += "life=1&";
        } else {
            str += "life=0&";
        }
        if (isLearning) {
            str += "learning=1&";
        } else {
            str += "learning=0&";
        }
        if (isCulture) {
            str += "culture=1&";
        } else {
            str += "culture=0&";
        }
        if (isMilitary) {
            str += "military=1&";
        } else {
            str += "military=0&";
        }
        if (isJoke) {
            str += "joke=1";
        } else {
            str += "joke=0";
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost = getResources().getString(R.string.localhost);
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://" + localhost + "/ahu/category.php?username=" + URLEncoder.encode(username, "UTF-8") + "&" + str;
                    //      System.out.println(strUrl);
                    connection = null;
                    URL url = new URL(strUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        Message msg = new Message();
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS) {
                Toast.makeText(CategoryActivity.this, "类别选择成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };
}
