package com.hjsmallfly.syllabus.pojo;

/**
 * Created by smallfly on 16-3-28.
 * 用于发送点赞请求
 */
public class ThumbUpTask {
//    post_parser.add_argument("post_id", required=True, type=int, location="json")
//    post_parser.add_argument("uid", required=True, type=int, location="json")
//    post_parser.add_argument("token", required=True, location="json")
    private int post_id;
    private int uid;
    private String token;

    public ThumbUpTask(int post_id, int uid, String token){
        this.post_id = post_id;
        this.uid = uid;
        this.token = token;
    }

}
