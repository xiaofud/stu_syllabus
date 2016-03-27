package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by smallfly on 16-3-27.
 * 获取用户
 */
public interface GetUserApi {

    @GET("/interaction/api/v2/compatible_user/{account}")
    Call<User> get_user(@Path("account") String account);
}
