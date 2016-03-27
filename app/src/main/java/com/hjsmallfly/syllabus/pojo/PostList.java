package com.hjsmallfly.syllabus.pojo;

/**
 * Created by smallfly on 16-3-27.
 *
 */
import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class PostList {
    @SerializedName("post_list")
    @Expose
    public List<Post> postList = new ArrayList<>();

}