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
        Calendar calendar =  Calendar.getInstance();
        calendar.setTimeInMillis(pub_time * 1000);  // 这个是毫秒
        // 月份是从 0 开始的
        return calendar.get(Calendar.YEAR) + "-" +  (calendar.get(Calendar.MONTH) + 1 ) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
                + " " + calendar.get(Calendar.HOUR_OF_DAY) + " 时 " + calendar.get(Calendar.MINUTE) + " 分" ;
    }

    @Override
    public String toString(){
        return "发布者 " + publisher + "\n发布时间 " + transfer_time()  + "\n内容" + content;
    }

}
