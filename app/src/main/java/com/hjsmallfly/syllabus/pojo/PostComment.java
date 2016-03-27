package com.hjsmallfly.syllabus.pojo;

// 用于保存post的每个comment信息

//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class PostComment {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("uid")
    @Expose
    public Integer uid;

}
