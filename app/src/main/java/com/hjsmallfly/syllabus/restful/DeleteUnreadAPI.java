package com.hjsmallfly.syllabus.restful;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;

/**
 * Created by smallfly on 16-9-10.
 */
public interface DeleteUnreadAPI {

    @DELETE("/interaction/api/v2/unread")
    Call<Void> deleteUnread(@Header("uid")int uid, @Header("pid") int pid, @Header("token") String token);

}
