package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.CreatedReturnValue;
import com.hjsmallfly.syllabus.pojo.ThumbUpTask;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by smallfly on 16-3-28.
 * 用于点赞的Api
 */
public interface PushThumbUpApi {
    @POST("/interaction/api/v2/like")
    Call<CreatedReturnValue> like_this(@Body ThumbUpTask thumbUpTask);
}
