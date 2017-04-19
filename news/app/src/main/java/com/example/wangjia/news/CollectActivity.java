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

import com.example.wangjia.news.utils.CollectItem;
import com.example.wangjia.news.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by wangjia on 2017/4/5.
 */

public class CollectActivity extends AppCompatActivity {
    public static String username;
    public ListView listview;
    ArrayList<CollectItem> list=new ArrayList<>();
    public static final int SUCCESS=1;
    public static final int PARSEJSON=2;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);
        Toolbar toolbar= (Toolbar) findViewById(R.id.collect_toolbar);
  //      toolbar.setLogo(R.drawable.ic_menu_gallery);
        toolbar.setTitle("收藏");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
        preferences=getSharedPreferences("ahu",MODE_WORLD_READABLE);
        username=preferences.getString("username",null);
        listview= (ListView) findViewById(R.id.collect_listview);
        getData();


    }
    public void getData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost=getResources().getString(R.string.localhost);
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://"+localhost+"/ahu/showCollect.php?username="+URLEncoder.encode(username,"UTF-8");
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
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==SUCCESS){
                ParseJson(msg.obj.toString());
            }else if(msg.what==PARSEJSON){
                getListView(list);
            }
        }
    };

    public void getListView(ArrayList<CollectItem> mlist){
        final ArrayList<CollectItem> list=mlist;
        BaseAdapter adapter=new BaseAdapter() {
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
                CollectItem item=list.get(position);
                System.out.println(item.toString());
                String title=item.title;
                String time=item.time;
                //        System.out.println(title+"   "+time);
                String tmp=title;
                if(title.length()>20){
                    tmp=title.substring(0,20);
                    tmp+="...";
                }
                LinearLayout line=new LinearLayout(CollectActivity.this);
                line.setOrientation(LinearLayout.VERTICAL);
                TextView tv1=new TextView(CollectActivity.this);
                tv1.setText(tmp);
                tv1.setLines(1);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv1.setGravity(Gravity.LEFT);
                TextView tv2=new TextView(CollectActivity.this);
                tv2.setText(time);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
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
                CollectItem item=list.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("title",item.title);
                bundle.putString("url",item.url);
                bundle.putString("category",item.category);
                bundle.putString("username",username);
                Intent intent=new Intent(CollectActivity.this, NewsContentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void ParseJson(String StringData) {
        try{
            JSONObject jsonObject=new JSONObject(StringData);
            list.clear();
            int num=jsonObject.getInt("cnt");
            for(int i=0;i<num;i++){
                JSONObject json=jsonObject.getJSONObject(""+i);
                String title=json.getString("title");
                String time=json.getString("time");
                String url=json.getString("url");
                String category=json.getString("category");
                //         System.out.println(title+"  "+time+"  "+url);
                CollectItem item=new CollectItem(title,url,time,category);
                list.add(item);
            }
            Message msg=new Message();
            msg.what=PARSEJSON;
            msg.obj=list;
            handler.sendMessage(msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
