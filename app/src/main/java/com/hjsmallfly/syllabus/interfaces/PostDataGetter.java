package com.hjsmallfly.syllabus.interfaces;

/**
 * Created by smallfly on 15-11-27.
 * 用于处理提交了post请求后得到的数据
 */
public interface PostDataGetter {
    void handle_post_response(String response);
}
