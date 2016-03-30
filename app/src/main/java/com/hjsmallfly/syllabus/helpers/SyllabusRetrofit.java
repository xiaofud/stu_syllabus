package com.hjsmallfly.syllabus.helpers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by smallfly on 16-3-27.
 * 存储了与自己服务器通信的Retrofit对象
 */
public class SyllabusRetrofit {

//    172.19.73.180
    public static final String SERVER_ADDRESS = "http://119.29.95.245:8080";
//    public static final String SERVER_ADDRESS = "http://172.19.73.180:8080";

    // 用于与自己服务器通信的全局 retrofit 对象
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVER_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
