package com.hjsmallfly.syllabus.pojo;

import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class Post {

    @SerializedName("comments")
    @Expose
    public List<PostComment> comments = new ArrayList<>();
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("photo_list_json")
    @Expose
    public String photoListJson;
    @SerializedName("post_time")
    @Expose
    public String postTime;
    @SerializedName("post_type")
    @Expose
    public Integer postType;
    @SerializedName("thumb_ups")
    @Expose
    public List<PostThumbUp> thumbUps = new ArrayList<>();
    @SerializedName("user")
    @Expose
    public PostUser postUser;

}
