package com.hjsmallfly.syllabus.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smallfly on 16-3-28.
 * 服务器成功处理post请求后返回的id
 */
public class CreatedReturnValue {
    @SerializedName("id")
    @Expose
    public int id;
}
