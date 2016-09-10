package com.hjsmallfly.syllabus.mvp.contract;

import com.hjsmallfly.syllabus.pojo.PostList;

/**
 * Created by smallfly on 16-9-10.
 */
public interface PostsDataSource {

    void fetchPosts(int count, int before_id, postsCallback callback);

    interface postsCallback{
        int NO_MORE = 404;
        int OK = 0;
        int INTERNET_ERROR = 1;
        void onPosts(int code, PostList postList);
    }

}
