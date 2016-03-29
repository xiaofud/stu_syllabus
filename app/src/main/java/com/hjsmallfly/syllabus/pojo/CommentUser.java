package com.hjsmallfly.syllabus.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smallfly on 16-3-29.
 * 评论的用户
 */
public class CommentUser {

    @SerializedName("nickname")
    @Expose
    public String nickname;

    @SerializedName("account")
    @Expose
    public String account;

}
