package com.hjsmallfly.syllabus.mvp.contract;

import com.hjsmallfly.syllabus.pojo.PostList;

/**
 * Created by smallfly on 16-9-10.
 */
public interface UnreadDataSource {

    void getUnreadMessage(int userID, int type, PostsDataSource.postsCallback callback);

}
