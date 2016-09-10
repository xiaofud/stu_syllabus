package com.hjsmallfly.syllabus.mvp.contract;

import com.hjsmallfly.syllabus.pojo.Post;

/**
 * Created by smallfly on 16-9-10.
 *
 */
public interface SinglePostContract {

    interface PostPresenter{
        void onLikeButtonClicked();  // 点赞
        void onUnLikeButtonClicked();  // 取消点赞
        void setPost(Post post);
        void displayDetailButtonClicked();
        void onOptionConfirmed(int type);
        void onLongClicked();
    }

    interface PostView{
        int OPTION_DELETE = 0;
        int OPTION_COPY = 1;
        void onLikeReturn(int code, Post post, String message);
        void onUnLikeReturn(int code, Post post, String message);
        void onDeleteReturn(int code, Post post, String message);
        void setPresenter(PostPresenter presenter);
        void displayPostBrief(Post post);
        void displayPostDetail(Post post);
        void confirmOption(Post post);
        PostPresenter getPresenter();
    }

}
