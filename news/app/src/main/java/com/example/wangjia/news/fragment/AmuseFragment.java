package com.example.wangjia.news.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wangjia.news.NewsContentActivity;
import com.example.wangjia.news.R;
import com.example.wangjia.news.utils.Base64Tools;
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

/**
 * Created by wangjia on 2017/4/11.
 */

public class AmuseFragment extends Fragment{
    public static String username;
    ListView listview;
    public final int SUCCESS=1;
    public final int PARSEJSON=2;
    ArrayList<NewsItem> list=new ArrayList<>();
    View view;
    String category;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_amuse,null);
        listview= (ListView) view.findViewById(R.id.amuse_listview);
        Bundle bundle=getArguments();
        category=bundle.getString("category");
        username=bundle.getString("username");
        getData();
        return view;
    }
    public void getListView(ArrayList<NewsItem> mlist){
        final ArrayList<NewsItem> list=mlist;
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
                NewsItem item=list.get(position);
                System.out.println(item.toString());
                String title=item.title;
                String time=item.time;
        //        System.out.println(title+"   "+time);
                String tmp=title;
                if(title.length()>20){
                    tmp=title.substring(0,20);
                    tmp+="...";
                }
                LinearLayout line=new LinearLayout(getContext());
                line.setOrientation(LinearLayout.VERTICAL);
                TextView tv1=new TextView(getContext());
                tv1.setText(tmp);
                tv1.setLines(1);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                tv1.setGravity(Gravity.LEFT);
                TextView tv2=new TextView(getContext());
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
                NewsItem item=list.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("title",item.title);
                bundle.putString("url",item.url);
                bundle.putString("category",category);
                bundle.putString("username",username);
                Intent intent=new Intent(getContext(), NewsContentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }


    public static AmuseFragment newInstance(String category,String uName){
        Bundle bundle=new Bundle();
        bundle.putString("category",category);
        bundle.putString("username",uName);
        AmuseFragment fragment=new AmuseFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void getData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost=getResources().getString(R.string.localhost);
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://"+localhost+"/ahu/amuse_fragment.php?category="
                            + URLEncoder.encode(category,"UTF-8")+"&username="+URLEncoder.encode(username,"UTF-8");
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
       //         System.out.println(title+"  "+time+"  "+url);
                NewsItem item=new NewsItem(title,url,time);
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
