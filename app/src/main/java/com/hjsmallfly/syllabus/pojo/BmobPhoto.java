package com.hjsmallfly.syllabus.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smallfly on 16-3-27.
 * 调用bmob api上传图片成功后的返回数据
 */
public class BmobPhoto {
//    {"cdn":"upyun","filename":"myPicture.jpg","url":"http://bmob-cdn-5340.b0.upaiyun.com/2016/09/08/6828e59640a35968800c36abaa2925e0.jpg"}

    @SerializedName("filename")
    @Expose
    public String filename;

    @SerializedName("cdn")
    @Expose
    public String cdn;

//    @SerializedName("group")
//    @Expose
//    public String group;
    @SerializedName("url")
    @Expose
    public String url;
}
