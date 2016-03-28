package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.PushPostTask;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by smallfly on 16-3-27.
 *
 */
public interface PushPostApi {

//    # 话题, 用户自发的
//            POST_TYPE_TOPIC = 1
//
//    # 宣传活动性质的(公众号推文)
//    POST_TYPE_ACTIVITY = 2  # 如果是这种类型的话, 那么客户端处理的时候就要注意把content作为文章的URL

    public static final int POST_TYPE_TOPIC = 1;
    public static final int POST_TYPE_ACTIVITY = 2;

    @POST("/interaction/api/v2/post")
    Call<Void> post(@Body PushPostTask pushPostTask);

}
