package com.hjsmallfly.syllabus.restful;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;

/**
 * Created by smallfly on 16-3-28.
 */
public interface DeletePostApi {
    @DELETE("/interaction/api/v2/post")
    Call<Void> delete_post(@Header("id") int post_id, @Header("uid") int uid, @Header("token") String token);
}
