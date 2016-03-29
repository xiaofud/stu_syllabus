package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.PostCommentTask;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by smallfly on 16-3-29.
 * 用于发送评论
 */
public interface PostCommentApi {
    @POST("/interaction/api/v2/comment")
    Call<Void> post_comment(@Body PostCommentTask postCommentTask);
}
