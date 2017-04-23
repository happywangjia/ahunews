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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by wangjia on 2017/4/11.
 */

public class NewsContentActivity extends AppCompatActivity {

    public HashMap<String,String> map=new HashMap<>();
    public WebView webView;
    public static String title;
    public static String url;
    public static String category;
    public static String username;
    public static final int SUCCESS=1;
    public static final int PARSER=2;
    public static JSONObject cnt_json;
    SharedPreferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_content);
        init();
        preferences = getSharedPreferences("ahu", MODE_WORLD_READABLE);
        username=preferences.getString("username",null);
        url=getIntent().getExtras().getString("url");
        title=getIntent().getExtras().getString("title");
        category=getIntent().getExtras().getString("category");
        Toolbar toolbar= (Toolbar) findViewById(R.id.content_toolbar);
        toolbar.setTitle(map.get(category));
        String tmp=title;
        if(title.length()>10){
            tmp=title.substring(0,10);
            tmp+="...";
        }
        toolbar.setSubtitle(tmp);
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);



        webView= (WebView) findViewById(R.id.id_news_content);
        webView.loadUrl(url);
        WebSettings settings=webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        settings.setUseWideViewPort(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(url);
//                return true;
//            }
//        });

        ShareSDK.initSDK(NewsContentActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.loadUrl(url);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick= new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_share:
                    showShare();
                    break;
                case R.id.action_collect:
                    doCollect();
                    break;
                case R.id.action_comment:
                    doComment();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void doComment() {
        Intent intent=new Intent(NewsContentActivity.this,ContentCommentActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("title",title);
        bundle.putString("username",username);
        intent.putExtras(bundle);
        startActivity(intent);

    }



    private void doCollect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost=getResources().getString(R.string.localhost);
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://"+localhost+"/ahu/addCollect.php?username="+ URLEncoder.encode(username,"UTF-8")+"&title="+URLEncoder.encode(title,"UTF-8");
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
                ParseJson(msg.obj.toString());

            }else if(msg.what==PARSER){
                if (msg.obj.toString().equals("0")) {
                    Toast.makeText(NewsContentActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(NewsContentActivity.this,"取消收藏成功",Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content,menu);
        return true;
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
    /**
     * onkeyshare的界面分享
     *
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(title);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("这条新闻推荐给大家");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

// 启动分享GUI
        oks.show(this);
    }
}
