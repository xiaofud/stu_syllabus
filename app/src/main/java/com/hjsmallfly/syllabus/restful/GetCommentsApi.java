package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.CommentList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by smallfly on 16-3-28.
 *
 */
public interface GetCommentsApi {
    @GET("/interaction/api/v2/post_comments?field=post_id")
    Call<CommentList> get_comments(@Query("value") int post_id);
}
