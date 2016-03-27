package com.hjsmallfly.syllabus.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smallfly on 16-3-27.
 * 调用bmob api上传图片成功后的返回数据
 */
public class BmobPhoto {
    @SerializedName("filename")
    @Expose
    public String filename;
    @SerializedName("group")
    @Expose
    public String group;
    @SerializedName("url")
    @Expose
    public String url;
}
