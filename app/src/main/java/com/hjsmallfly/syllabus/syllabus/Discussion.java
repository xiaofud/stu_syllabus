package com.hjsmallfly.syllabus.syllabus;

import java.util.Calendar;

/**
 * Created by STU_nwad on 2015/10/11.
 * 用于记录Discussion类
 */
public class Discussion {
//    "discussions": [
//    {
//        "content": "Hello,World",
//            "publisher": "14xfdeng",
//            "time": 999999
//    }
//    ]

    public String content;
    public String publisher;
    public String publisher_nickname;
    public long pub_time;

    public int id;  // 在数据库中的id

    public String transfer_time(){
        return calculate_diff_time();
    }

    public String calculate_diff_time(){
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pub_time * 1000);
        long current_time =  now.getTimeInMillis() / 1000;
        long seconds = current_time - pub_time;
        long minutes = seconds / 60 ;
        long hours = minutes / 60;
        // 超过一天就显示具体日期了
        if (hours >= 24){
            // 月份是从 0 开始的
            return (calendar.get(Calendar.MONTH) + 1 ) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
                    + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        }else if (hours >=1 ){
            return hours + " 小时前";
        }else if (minutes >= 1)
            return minutes + " 分钟前";
        else if (seconds <= 5)
            return "刚刚";
        else
            return seconds + " 秒前";
    }

    @Override
    public String toString(){
        return "发布者 " + publisher + "\n发布时间 " + transfer_time()  + "\n内容" + content;
    }

}
