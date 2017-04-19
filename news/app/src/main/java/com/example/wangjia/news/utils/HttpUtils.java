package com.example.wangjia.news.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 将字节流转换为字符串的类
 * Created by wangjia on 2017/4/2.
 */

public class HttpUtils {
    public static String readMyInputStream(InputStream is){
        byte[] result;
        try{
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int len;
            while((len=is.read(buffer))!=-1){
                baos.write(buffer,0,len);
            }
            is.close();
            baos.close();
            result=baos.toByteArray();

        }catch (Exception e){
            e.printStackTrace();
            String errorStr="获取数据失败";
            return errorStr;
        }
        return new String(result);
    }


}
