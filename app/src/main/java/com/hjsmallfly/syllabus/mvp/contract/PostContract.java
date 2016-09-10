package com.hjsmallfly.syllabus.mvp.contract;

import com.hjsmallfly.syllabus.pojo.Post;

/**
 * Created by smallfly on 16-9-10.
 */
public interface PostContract {

    interface PostPresenter{
        void onLikeButtonClicked();  // 点赞
        void onUnLikeButtonClicked();  // 取消点赞
        void setPost(Post post);
        void onLikeReturn(int code, Post post, String message);
        void onUnLikeReturn(int code, Post post, String message);
        void displayDetailButtonClicked();
    }

    interface PostView{
        void onLikeReturn(int code, Post post, String message);
        void onUnLikeReturn(int code, Post post, String message);
        void setPresenter(PostPresenter presenter);
        void displayPostBrief(Post post);
        void displayPostDetail(Post post);
        PostPresenter getPresenter();
    }

}
