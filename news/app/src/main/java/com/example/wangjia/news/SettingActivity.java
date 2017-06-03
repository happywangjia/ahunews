package com.example.wangjia.news;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangjia.news.utils.CircleImg;
import com.example.wangjia.news.utils.FileUtil;
import com.example.wangjia.news.utils.HttpUtils;
import com.example.wangjia.news.utils.NetUtil;
import com.example.wangjia.news.utils.SelectPicPopupWindow;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangjia on 2017/4/5.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_username;
    private TextView tv_logout;
    private EditText et_motto;
    private EditText et_telephone;
    private EditText et_email;
    private Button bt_submit;
    private Button bt_cancel;
    private SelectPicPopupWindow menuWindow;
    private static ProgressDialog pd;
    private TextView tv_reset;

    private String resultStr = "";    // 服务端返回结果集
    private static String serverPath;
    private String urlpath = null;            // 图片本地路径
    private static final int REQUESTCODE_PICK = 0;        // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记
    private static String IMAGE_FILE_NAME;
    private static final int GET_DRAWABLE = 4;
    private static final int CHAGNG_RESULT = 5;
    public static JSONObject cnt_json;
    public static final int PARSER = 7;
    public static final int RESET=8;

    private CircleImg imageView;
    String icon;
    String username;
    String motto;
    String telephone;
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        //      toolbar.setLogo(R.drawable.ic_menu_camera);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences("ahu", MODE_PRIVATE);
        editor = preferences.edit();
        motto = preferences.getString("motto", null);
        icon = preferences.getString("icon", null);
        username = preferences.getString("username", null);
        telephone = preferences.getString("telephone", null);
        email = preferences.getString("email", null);
        imageView = (CircleImg) findViewById(R.id.id_set_icon);
        et_motto = (EditText) findViewById(R.id.id_set_motto);
        et_telephone = (EditText) findViewById(R.id.id_set_telephone);
        et_email = (EditText) findViewById(R.id.id_set_email);
        bt_submit = (Button) findViewById(R.id.id_set_submit);
        bt_cancel = (Button) findViewById(R.id.id_set_cancel);
        tv_username = (TextView) findViewById(R.id.id_set_username);
        tv_logout = (TextView) findViewById(R.id.id_logout);
        tv_reset = (TextView) findViewById(R.id.id_reset);
        tv_reset.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        tv_username.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        imageView.setOnClickListener(this);
        IMAGE_FILE_NAME = username + ".jpg";
        getData();
        String localhost = getResources().getString(R.string.localhost);
        serverPath = "http://" + localhost + "/ahu/changInfo.php";
    }

    public void getData() {
        getIcon();
        et_motto.setText(motto);
        if (!telephone.equals("null"))
            et_telephone.setText(telephone);
        else
            et_telephone.setText("");
        if (!email.equals("null"))
            et_email.setText(email);
        else
            et_email.setText("");
        tv_username.setText(username);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_set_username:
//                Toast.makeText(SettingActivity.this, "个人信息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_logout:
                doLogout();
                break;
            case R.id.id_set_cancel:
                getData();
//                Toast.makeText(SettingActivity.this, "cancel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_set_submit:
                doSubmit();
//                Toast.makeText(SettingActivity.this, "submit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_set_icon:
                changIcon();
                break;
            case R.id.id_reset:
                resetPsw();
                break;
            default:
                break;
        }
    }

    private void resetPsw() {
        preferences = getSharedPreferences("ahu", MODE_PRIVATE);
        email = preferences.getString("email", null);
        if (email == null) {
            Toast.makeText(SettingActivity.this, "请先绑定邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String localhost = getResources().getString(R.string.localhost);

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String strUrl = "http://" + localhost + "/ahu/mail/sendmail.php?email=" + URLEncoder.encode(email, "UTF-8");
                    URL url = new URL(strUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        handler.sendEmptyMessage(RESET);
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

    private void doSubmit() {
        if (isTelephone(et_telephone.getText().toString().trim()) == false) {
            return;
        }
        if (isEmail(et_email.getText().toString().toString().trim()) == false) {
            return;
        }

        pd = ProgressDialog.show(SettingActivity.this, null, "正在提交修改，请稍候...");
        new Thread(uploadImageRunnable).start();
    }

    private boolean isEmail(String trim) {
        if (trim.isEmpty()) return true;
        String check = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(trim);
        System.out.println(trim+"  "+matcher.matches());
        if (matcher.matches())
            return true;
        Toast.makeText(SettingActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isTelephone(String trim) {
        if (trim.isEmpty()) return true;
        Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
        Matcher matcher = regex.matcher(trim);
        if (matcher.matches())
            return true;
        Toast.makeText(SettingActivity.this, "手机号码格式有误", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void changIcon() {
        menuWindow = new SelectPicPopupWindow(SettingActivity.this, itemsOnClick);
        menuWindow.showAtLocation(findViewById(R.id.id_set_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            urlpath = FileUtil.saveFile(SettingActivity.this, username + ".jpg", photo);
            imageView.setImageDrawable(drawable);


        }
    }


    private void doLogout() {

        //    editor.putString("username",null);
        editor.putString("password", null);
        editor.commit();
        Intent logoutIntent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(logoutIntent);
        finish();

    }

    public void getIcon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(icon);
                    Drawable drawable = Drawable.createFromStream(url.openStream(), "icon.jpg");
//                    imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
                    Message msg = new Message();
                    msg.what = GET_DRAWABLE;
                    msg.obj = drawable;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return false;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {

//            if (TextUtils.isEmpty(serverPath)) {
//                Toast.makeText(SettingActivity.this, "还没有设置上传服务器的路径！", Toast.LENGTH_SHORT).show();
//                return;
//            }

            Map<String, String> textParams;
            Map<String, File> fileparams;

            try {
                // 创建一个URL对象
                URL url = new URL(serverPath);
                textParams = new HashMap<String, String>();
                fileparams = new HashMap<String, File>();
                // 要上传的图片文件
                if (urlpath != null) {
                    File file = new File(urlpath);
                    fileparams.put("file", file);
                }
                textParams.put("username", username);
                textParams.put("motto", et_motto.getText().toString().trim());
                textParams.put("tel", et_telephone.getText().toString().trim());
                textParams.put("email", et_email.getText().toString().trim());
                // 利用HttpURLConnection对象从网络中获取网页数据
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
                conn.setConnectTimeout(5000);
                // 设置允许输出（发送POST请求必须设置允许输出）
                conn.setDoOutput(true);
                // 设置使用POST的方式发送
                conn.setRequestMethod("POST");
                // 设置不使用缓存（容易出现问题）
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");//设置编码
                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
                conn.setRequestProperty("ser-Agent", "Fiddler");
                // 设置contentType
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                OutputStream os = conn.getOutputStream();
                DataOutputStream ds = new DataOutputStream(os);
                NetUtil.writeStringParams(textParams, ds);
                NetUtil.writeFileParams(fileparams, ds);
                NetUtil.paramsEnd(ds);
                // 对文件流操作完,要记得及时关闭
                os.close();
                // 服务器返回的响应吗
                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                // 对响应码进行判断
                if (code == 200) {// 返回的响应码200,是成功
                    // 得到网络返回的输入流
                    InputStream is = conn.getInputStream();
                    resultStr = NetUtil.readString(is);
                    //              System.out.println(resultStr);
                    Message msg_result = new Message();
                    msg_result.what = CHAGNG_RESULT;
                    msg_result.obj = resultStr;
                    handler.sendMessage(msg_result);
                } else {
                    Toast.makeText(SettingActivity.this, "请求URL失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DRAWABLE:
                    imageView.setImageDrawable((Drawable) msg.obj);
                    break;
                case CHAGNG_RESULT:
                    pd.dismiss();
                    //    Toast.makeText(SettingActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    ParseJson(msg.obj.toString());


                    break;
                case PARSER:
                    String num = msg.obj.toString();
                    //            System.out.println("wwwwwwwww"+et_telephone.getText().toString().toString().equals(""));
                    if (num.equals("0")) {
                        preferences = getSharedPreferences("ahu", MODE_PRIVATE);
                        editor = preferences.edit();

                        if (et_telephone.getText().toString().trim().equals("") == false)
                            editor.putString("telephone", et_telephone.getText().toString().trim());
                        if (et_email.getText().toString().trim().equals("") == false)
                            editor.putString("email", et_email.getText().toString().trim());
                        editor.putString("motto", et_motto.getText().toString().trim());
                        if (urlpath != null) {
                            editor.putString("icon", "http://121.42.218.244/ahu/icon/" + username + ".jpg");
                        }
                        editor.commit();
                    } else if (num.equals("1")) {
                        Toast.makeText(SettingActivity.this, "该手机号已绑定其他账号", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingActivity.this, "该邮箱已绑定其他账号", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESET:
                    Toast.makeText(SettingActivity.this,"重置密码邮件已发至邮箱，请查看",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public void ParseJson(String StringData) {
        try {
            JSONObject jsonObject = new JSONObject(StringData);
            int num = jsonObject.getInt("num");
            Message msg = new Message();
            msg.what = PARSER;
            cnt_json = jsonObject;
            msg.obj = "" + num;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
