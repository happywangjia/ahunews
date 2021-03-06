package com.example.wangjia.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wangjia.news.R;
import com.example.wangjia.news.utils.HttpUtils;
import com.example.wangjia.news.utils.NewsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wangjia on 2017/4/5.
 */

public class CommentActivity extends AppCompatActivity {

    SharedPreferences preferences;

    public ListView listview;
    public final int SUCCESS=1;
    public final int PARSEJSON=2;
    public static String username;
    ArrayList<NewsItem> list=new ArrayList<>();
    public static HashMap<String,String> map=new HashMap<>();
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
        init();
        listview= (ListView) findViewById(R.id.comment_listview);
        getData();

    }

    public void getListView(ArrayList<NewsItem> mlist) {
        final ArrayList<NewsItem> list = mlist;
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                NewsItem item = list.get(position);
                System.out.println(item.toString());
                String title = item.title;
                String time = item.time;
                //        System.out.println(title+"   "+time);
                String tmp = title;
                if (title.length() > 20) {
                    tmp = title.substring(0, 20);
                    tmp += "...";
                }
                LinearLayout line = new LinearLayout(CommentActivity.this);
                line.setOrientation(LinearLayout.VERTICAL);
                TextView tv1 = new TextView(CommentActivity.this);
                tv1.setText(tmp);
                tv1.setLines(1);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                tv1.setGravity(Gravity.LEFT);
                TextView tv2 = new TextView(CommentActivity.this);
                tv2.setText(time);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                tv2.setLines(1);
                tv2.setGravity(Gravity.RIGHT);
                line.addView(tv1);
                line.addView(tv2);
                return line;
            }
        };
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem item = list.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", item.title);
                bundle.putString("url", item.url);
                bundle.putString("category", item.category);
                bundle.putString("username", username);
                Intent intent = new Intent(CommentActivity.this, NewsContentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
    private void init() {
        map.put("recommend","推荐");
        map.put("amuse","娱乐");
        map.put("technology","科技");
        map.put("life","生活");
        map.put("learning","学习");
        map.put("culture","文化");
        map.put("military","军事");
        map.put("joke","笑话");

    }

    public void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost = getResources().getString(R.string.localhost);
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://" + localhost + "/ahu/commentList.php?username=" + URLEncoder.encode(username, "UTF-8");
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
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS) {
                ParseJson(msg.obj.toString());
            } else if (msg.what == PARSEJSON) {
                getListView(list);
            }
        }
    };

    private void ParseJson(String StringData) {
        try {
            JSONObject jsonObject = new JSONObject(StringData);
            list.clear();
            int num = jsonObject.getInt("cnt");
            for (int i = 0; i < num; i++) {
                JSONObject json = jsonObject.getJSONObject("" + i);
                String title = json.getString("title");
                String time = json.getString("time");
                String url = json.getString("url");
                String category=json.getString("category");
                //         System.out.println(title+"  "+time+"  "+url);
                NewsItem item = new NewsItem(title,map.get(category), url, time);
                list.add(item);
            }
            Message msg = new Message();
            msg.what = PARSEJSON;
            msg.obj = list;
            handler.sendMessage(msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
