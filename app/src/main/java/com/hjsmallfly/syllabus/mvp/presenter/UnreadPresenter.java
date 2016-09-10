package com.hjsmallfly.syllabus.mvp.presenter;

import com.hjsmallfly.syllabus.mvp.contract.PostsContract;
import com.hjsmallfly.syllabus.mvp.contract.PostsDataSource;
import com.hjsmallfly.syllabus.mvp.contract.UnreadDataSource;
import com.hjsmallfly.syllabus.pojo.PostList;

/**
 * Created by smallfly on 16-9-10.
 *
 */
public class UnreadPresenter implements PostsContract.UnreadPresenter, PostsDataSource.postsCallback {

    private PostsContract.PostsView postsView;
    private UnreadDataSource unreadDataSource;

    public UnreadPresenter(PostsContract.PostsView postsView, UnreadDataSource unreadDataSource){
        this.postsView = postsView;
        this.unreadDataSource = unreadDataSource;
    }

    @Override
    public void getUnreadMessage(int userID, int type) {
        unreadDataSource.getUnreadMessage(userID, type, this);
    }

    @Override
    public void onPosts(int code, PostList postList) {
        if (code == OK)
            postsView.updatePosts(postList, true);
        else if (code == INTERNET_ERROR)
            postsView.showInternetError();
        else if (code == NO_MORE)
            postsView.showNoMore();
        else
            postsView.showNoMore();
    }
}
