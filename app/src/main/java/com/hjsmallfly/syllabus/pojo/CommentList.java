package com.hjsmallfly.syllabus.pojo;

/**
 * Created by smallfly on 16-3-28.
 * 评论列表
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentList {

    @SerializedName("comments")
    @Expose
    public List<Comment> comments = new ArrayList<Comment>();

}
