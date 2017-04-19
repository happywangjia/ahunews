package com.example.wangjia.news.utils;

/**
 * Created by wangjia on 2017/4/11.
 */

public class NewsItem {
    public String title;
    public String url;
    public String time;

    public NewsItem(String mtitle,String murl,String mtime) {
        title=mtitle;
        url=murl;
        time=mtime;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
