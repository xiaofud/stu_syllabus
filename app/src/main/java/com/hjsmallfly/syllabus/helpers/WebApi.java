package com.hjsmallfly.syllabus.helpers;

/**
 * Created by smallfly on 15-11-21.
 * 用于设置调用 web service 的地址
 */
public class WebApi {
    // 需要被初始化为 strings 的值
    static String server_address = "";

    public static String get_server_address(){
        return server_address;
    }

    public static void set_server_address(String address){
        server_address = address;
    }

}
