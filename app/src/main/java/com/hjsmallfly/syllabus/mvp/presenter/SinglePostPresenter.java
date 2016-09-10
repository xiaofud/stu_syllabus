package com.hjsmallfly.syllabus.mvp.presenter;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.mvp.contract.SinglePostContract;
import com.hjsmallfly.syllabus.mvp.contract.SinglePostDataSource;
import com.hjsmallfly.syllabus.mvp.model.SinglePostModel;
import com.hjsmallfly.syllabus.pojo.Post;

/**
 * Created by smallfly on 16-9-10.
 *
 */
public class SinglePostPresenter implements SinglePostContract.PostPresenter, SinglePostDataSource.PostCallback {

    private SinglePostContract.PostView postView;
    private SinglePostModel postModel;
    private Post post;

    public SinglePostPresenter(SinglePostModel postModel , SinglePostContract.PostView postView){
        this.postView = postView;
        this.postView.setPresenter(this);
        this.postModel = postModel;

    }

    @Override
    public void onLikeButtonClicked() {
        postModel.like(post, MainActivity.user_id, MainActivity.token, this);
    }

    @Override
    public void onUnLikeButtonClicked() {
        postModel.unlike(post, MainActivity.user_id, MainActivity.token, this);
    }



    @Override
    public void setPost(Post post) {
        this.post = post;
        postView.displayPostBrief(post);
    }


    @Override
    public void displayDetailButtonClicked() {
        postView.displayPostDetail(post);
    }

    @Override
    public void onOptionConfirmed(int type) {
        if (type == SinglePostContract.PostView.OPTION_DELETE){
            postModel.delete(post, MainActivity.user_id, MainActivity.token, this);
        }
    }

    @Override
    public void onLongClicked() {
        postView.confirmOption(post);
    }

    @Override
    public void onLikePost(int code, Post post, String message) {
        postView.onLikeReturn(code, post, message);
    }

    @Override
    public void onUnlikePost(int code, Post post, String message) {
        postView.onUnLikeReturn(code, post, message);
    }

    @Override
    public void onDelete(int code, Post post, String message) {
        postView.onDeleteReturn(code, post, message);
    }
}
