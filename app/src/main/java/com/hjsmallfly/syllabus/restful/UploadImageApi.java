package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.BmobPhoto;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by smallfly on 16-3-27.
 * 用于上传文件的restful请求
 */
public interface UploadImageApi {
    // headers
    public final static String APPLICATION_ID_FIELD = "X-Bmob-Application-Id";
    public final static String API_KEY_FIELD = "X-Bmob-REST-API-Key";
    public final static String APPLICATION_ID_VALUE = "f4c7ea544bef5b7d35cab704b366afc9";
    public final static String API_KEY_FIELD_VALUE = "8e2761b40e8380f1ee98df57caad38d2";

    @Headers({
            APPLICATION_ID_FIELD + ": " + APPLICATION_ID_VALUE,
            API_KEY_FIELD + ": " + API_KEY_FIELD_VALUE,
            "Content-Type: image/jpeg"
    })
    @POST("/2/files/{filename}")
    Call<BmobPhoto> upload(@Body RequestBody requestBody, @Path("filename") String filename);
}
