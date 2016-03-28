package com.hjsmallfly.syllabus.pojo;

/**
 * Created by smallfly on 16-3-27.
 * 用于发送新的post
 */
public class PushPostTask {

    protected String content;
    protected String description;
    protected int uid;
    protected String token;
    protected int post_type;
    protected String photo_list_json;

    public PushPostTask(String content, String description, int uid, String token, int post_type,
                        String photo_list_json){
        this.content = content;
        this.description = description;
        this.uid = uid;
        this.token = token;
        this.post_type = post_type;
        this.photo_list_json = photo_list_json;
    }

}
