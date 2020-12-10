package com.example.androidapp_hw9.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }
    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 取得当前时间戳（精确到秒）
     * @return
     */
    public static String timeStamp(){
        long time = System.currentTimeMillis();
        String t = String.valueOf(time/1000);
        return t;
    }

    public static String dateHelper(String publishedAt) {
        String publish = publishedAt.replace('T', ' ').replace('Z', ' ');
        String timeStamp_now = timeStamp();
        String timeStamp_pub = date2TimeStamp(publish, "yyyy-MM-dd HH:mm:ss");
        int diff = Integer.parseInt(timeStamp_now) - Integer.parseInt(timeStamp_pub);

        String res = "";

        if(diff < 0) {
            return "less than a day ago";
        }

        if(diff < 3600) {
            // minute
            res = diff/60 + " minutes ago";
        }else {
            // days

            if (diff / 86400 == 0) {
                res = "less than a day ago";
            } else {
                res = diff/86400 + " days ago";
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String dString = "2020-12-3T07:53:34Z";
        String newD = dString.replace('T', ' ').replace('Z', ' ');
        System.out.println(newD);

        String timeStamp_now = timeStamp();
        String timeStamp2 = date2TimeStamp(newD, "yyyy-MM-dd HH:mm:ss");

        int diff = Integer.parseInt(timeStamp_now) - Integer.parseInt(timeStamp2);
        System.out.println(diff);

        if(diff < 3600) {
        	// minute
        	System.out.println(diff/60 + " minutes ago");
        }else {
        	// days

        	if (diff / 86400 == 0) {
        		System.out.println("less than a day ago");
        	} else {
        		System.out.println(diff/86400 + " days ago");
        	}
        }
    }
}