package com.hjsmallfly.syllabus.mvp.presenter;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.mvp.contract.PostContract;
import com.hjsmallfly.syllabus.mvp.model.PostModel;
import com.hjsmallfly.syllabus.pojo.Post;

/**
 * Created by smallfly on 16-9-10.
 */
public class PostPresenter implements PostContract.PostPresenter {

    private PostContract.PostView postView;
    private PostModel postModel;

//    private PushThumbUpApi thumbUpApi;
//    private UnLikeApi unLikeApi;
    private Post post;

    public PostPresenter(PostModel postModel , PostContract.PostView postView){
        this.postView = postView;
        this.postView.setPresenter(this);
        this.postModel = postModel;
        this.postModel.setPostPresenter(this);

    }

    public void onLikeButtonClicked() {
        postModel.like(post, MainActivity.user_id, MainActivity.token);

    }

    @Override
    public void onUnLikeButtonClicked() {
        postModel.unlike(post, MainActivity.user_id, MainActivity.token);
    }


    @Override
    public void setPost(Post post) {
        this.post = post;
        postView.displayPostBrief(post);
    }

    @Override
    public void onLikeReturn(int code, Post post, String message) {
        postView.onLikeReturn(code, post, message);
    }

    @Override
    public void onUnLikeReturn(int code, Post post, String message) {
        postView.onUnLikeReturn(code, post, message);
    }

    @Override
    public void displayDetailButtonClicked() {
        postView.displayPostDetail(post);
    }

}
