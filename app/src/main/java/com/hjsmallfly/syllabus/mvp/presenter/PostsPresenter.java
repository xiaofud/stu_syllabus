package com.hjsmallfly.syllabus.mvp.presenter;

import com.hjsmallfly.syllabus.mvp.contract.PostsContract;
import com.hjsmallfly.syllabus.mvp.contract.PostsDataSource;
import com.hjsmallfly.syllabus.pojo.PostList;

/**
 * Created by smallfly on 16-9-10.
 */
public class PostsPresenter implements PostsContract.PostsPresenter, PostsDataSource.postsCallback {

    private PostsContract.PostsView postsView;
    private PostsDataSource postsDataSource;

    private boolean refresh;

    public PostsPresenter(PostsContract.PostsView postsView, PostsDataSource postsDataSource){
        this.postsView = postsView;
        this.postsView.setPresenter(this);
        this.postsDataSource = postsDataSource;
    }

    @Override
    public void loadMorePosts(int count, int before_id, boolean refresh) {
        this.refresh = refresh;
        postsDataSource.fetchPosts(count, before_id, this);
    }

    @Override
    public void onPosts(int code, PostList postList) {
        if (code == OK)
            this.postsView.updatePosts(postList, refresh);
        else if (code == NO_MORE)
            this.postsView.showNoMore();
        else if (code == INTERNET_ERROR)
            this.postsView.showInternetError();
        else
            this.postsView.showUnknownError(code);
    }
}
