package com.hjsmallfly.syllabus.mvp.contract;

import com.hjsmallfly.syllabus.pojo.Post;

/**
 * Created by smallfly on 16-9-10.
 *
 */
public interface SinglePostDataSource {

    interface PostCallback{
        void onLikePost(int code, Post post, String message);
        void onUnlikePost(int code, Post post, String message);
        void onDelete(int code, Post post, String message);
    }

    void like(Post post, int userID, String token, PostCallback callback);
    void unlike(Post post, int userID, String token, PostCallback callback);
    void delete(Post post, int userID, String token, PostCallback callback);

}
