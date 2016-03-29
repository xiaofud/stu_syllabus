package com.hjsmallfly.syllabus.pojo;

/**
 * Created by smallfly on 16-3-28.
 * 评论
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("comment")
    @Expose
    public String comment;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("post_id")
    @Expose
    public Integer postId;
    @SerializedName("post_time")
    @Expose
    public String postTime;
    @SerializedName("uid")
    @Expose
    public Integer uid;
    @SerializedName("user")
    @Expose
    public CommentUser user;

}
