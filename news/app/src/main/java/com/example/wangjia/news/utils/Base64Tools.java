package com.example.wangjia.news.utils;


import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by wangjia on 2017/4/14.
 */

public class Base64Tools {
    public static String Base64(String str) throws UnsupportedEncodingException {
        String result=new String(Base64.encode(str.getBytes("UTF-8"),Base64.DEFAULT));
        return result;
    }


}
