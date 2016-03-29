package com.hjsmallfly.syllabus.pojo;

/**
 * Created by smallfly on 16-3-29.
 *
 */
public class PostCommentTask {

//    post_parser.add_argument("post_id", required=True, type=int, location="json")
//    post_parser.add_argument("uid", required=True, type=int, location="json")
//    post_parser.add_argument("comment", required=True, location="json")
//    post_parser.add_argument("token", required=True, location="json")

    private String comment;
    private String token;
    private int post_id;
    private int uid;

    public PostCommentTask(int post_id, int uid, String comment, String token){
        this.post_id = post_id;
        this.uid = uid;
        this.comment = comment;
        this.token = token;
    }

}
