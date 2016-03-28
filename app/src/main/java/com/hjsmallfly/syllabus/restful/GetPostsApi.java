package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.PostList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by smallfly on 16-3-27.
 * 获取最新的posts
 */
public interface GetPostsApi {
    // 2 为降序
    @GET("/interaction/api/v2/posts?sort_type=2")
    Call<PostList> get_posts(@Query("count") int count);
}
