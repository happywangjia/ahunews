package com.example.wangjia.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangjia.news.fragment.AmuseFragment;
import com.example.wangjia.news.utils.CircleImg;
import com.example.wangjia.news.utils.ViewPagerIndicator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ZhuYeActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener{
    public TextView zhu_username;
    public TextView zhu_motto;
    public static String username;
    public static String icon;
    public static String motto;
    public final int BACKDELAY=1;
    private static boolean isExit=false;
    private LinearLayout layout_news;
    HashMap<String,String> map=new HashMap<>();
    SharedPreferences preferences;
    public CircleImg imageView;


//-------------------------------------------
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;
    private List<String> mTitles= Arrays.asList("推荐","娱乐","科技","生活","学习","文化","军事","笑话");
    private List<AmuseFragment> mContents=new ArrayList<AmuseFragment>();
    private FragmentPagerAdapter mAdapter;

    public void getIcon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url=new URL(icon);
                    imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
//---------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_zhu_ye);
        init();
//        username=getIntent().getExtras().getString("username");
//        motto=getIntent().getExtras().getString("motto");
        preferences=getSharedPreferences("ahu",MODE_WORLD_READABLE);
        username=preferences.getString("username",null);
        motto=preferences.getString("motto",null);
        icon=preferences.getString("icon",null);



//        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setLogo(R.drawable.ic_menu_camera);
//        toolbar.setTitle("新闻");
//        setSupportActionBar(toolbar);


        DrawerLayout drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.inflateHeaderView(R.layout.nav_header_zhu_ye);

        zhu_username= (TextView) header.findViewById(R.id.zhu_username);
        zhu_motto= (TextView) header.findViewById(R.id.zhu_motto);
        zhu_username.setText(username);
        zhu_motto.setText(motto);
        imageView= (CircleImg) header.findViewById(R.id.id_icon);


        layout_news= (LinearLayout) findViewById(R.id.id_layout_news);

//---------------------------------------------
        initViews();
        initDatas();
        mIndicator.setVisibleTabCount(4);
        mIndicator.setTabItemTitles(mTitles);

        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager,0);
        getIcon();
//----------------------------------------------------------

    }
    //推荐","娱乐","科技","生活","学习","文化","军事","笑话
    public void init(){
        map.put("推荐","recommend");
        map.put("娱乐","amuse");
        map.put("科技","technology");
        map.put("生活","life");
        map.put("学习","learning");
        map.put("文化","culture");
        map.put("军事","military");
        map.put("笑话","joke");
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case BACKDELAY:
                    super.handleMessage(msg);
                    isExit=false;
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zhu_ye,menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_news){

        }else if(id==R.id.nav_category){
            Intent newsIntent=new Intent(ZhuYeActivity.this,CategoryActivity.class);
//            Bundle newsBundle=new Bundle();
//            newsBundle.putString("username",username);
//            newsIntent.putExtras(newsBundle);
            startActivity(newsIntent);

        }else if(id==R.id.nav_collect){
            Intent collectIntent=new Intent(ZhuYeActivity.this,CollectActivity.class);
//            Bundle collectBundle=new Bundle();
//            collectBundle.putString("username",username);
//            collectIntent.putExtras(collectBundle);
            startActivity(collectIntent);

        }else if(id==R.id.nav_comment){
            Intent commentIntent=new Intent(ZhuYeActivity.this,CommentActivity.class);
//            Bundle commentBundle=new Bundle();
//            commentBundle.putString("username",username);
//            commentIntent.putExtras(commentBundle);
            startActivity(commentIntent);

        } else if(id==R.id.nav_manage){
            Intent setIntent=new Intent(ZhuYeActivity.this,SettingActivity.class);
//            Bundle setBundle=new Bundle();
//            setBundle.putString("username",username);
//            setIntent.putExtras(setBundle);
            startActivity(setIntent);
        }
        DrawerLayout drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }
    private void exit(){
        if(!isExit){
            isExit=true;
            Toast.makeText(getApplicationContext(),"再按一次退出程序",Toast.LENGTH_SHORT).show();
            Message delayMsg=new Message();
            delayMsg.what=BACKDELAY;
            handler.sendMessageDelayed(delayMsg,2000);
        }else{
            finish();
            System.exit(0);
        }
    }

    //---------------------------------------
    private void initDatas(){
        for(String title:mTitles){
            AmuseFragment  fragment=AmuseFragment.newInstance(map.get(title),username);
            mContents.add(fragment);
        }
        mAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };


    }
    private void initViews(){
        mViewPager= (ViewPager)layout_news.findViewById(R.id.id_viewpager);
        mIndicator= (ViewPagerIndicator)layout_news.findViewById(R.id.id_indicator);

    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences=getSharedPreferences("ahu",MODE_WORLD_READABLE);
        username=preferences.getString("username",null);
        motto=preferences.getString("motto",null);
        icon=preferences.getString("icon",null);
        zhu_username.setText(username);
        zhu_motto.setText(motto);
        getIcon();
    }
}
