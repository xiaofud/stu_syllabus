package com.hjsmallfly.syllabus.restful;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;

/**
 * Created by smallfly on 16-3-28.
 */
public interface UnLikeApi {
    @DELETE("/interaction/api/v2/like")
    Call<Void> unlike_this(@Header("id") int like_id, @Header("uid") int uid, @Header("token") String token);
}
