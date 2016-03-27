package com.hjsmallfly.syllabus.helpers;

import android.util.Log;

import com.hjsmallfly.syllabus.pojo.BmobPhoto;
import com.hjsmallfly.syllabus.restful.UploadImageApi;

import java.io.File;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by smallfly on 16-3-27.
 * 使用bmob的restful接口上传图片, 并生成缩略图, 返回原图, 和缩略图的URL
 */
public class ImageUploader {
    // 图片URL的前缀
    public final static String IMAGE_URL_PREFIX_WITH_TAILING_SLASH
            = "http://file.bmob.cn/";

    // baseUrl must end in /
    // 但是如果仅仅是host那么可以不用
    public final static String UPLOAD_API = "https://api.bmob.cn";

    // TAG_FOR_DEBUG
    public final static String DEBUG_TAG = "image_uploader";


    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(UPLOAD_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static UploadImageApi uploadImageApi = retrofit.create(UploadImageApi.class);


    public static void upload_image(MediaType mediaType, String filename ,File image_file, Callback<BmobPhoto> callBack){
        RequestBody requestBody = RequestBody.create(mediaType, image_file);
        Call<BmobPhoto> upload_call = uploadImageApi.upload(requestBody, filename);
        Log.d(DEBUG_TAG, upload_call.request().headers().toString());
        upload_call.enqueue(callBack);
    }

}
