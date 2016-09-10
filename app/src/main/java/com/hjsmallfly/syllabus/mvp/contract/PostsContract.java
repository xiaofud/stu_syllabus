package com.hjsmallfly.syllabus.mvp.contract;

import com.hjsmallfly.syllabus.pojo.PostList;

/**
 * Created by smallfly on 16-9-10.
 */
public interface PostsContract {

    interface PostsPresenter{
        void loadMorePosts(int count, int before_id, boolean refresh);
    }

    interface UnreadPresenter{
        void getUnreadMessage(int userID, int type);
//        void readMessage(int )
    }

    interface PostsView{
        void setPresenter(PostsPresenter presenter);
        void updatePosts(PostList postList, boolean refresh);
        void showInternetError();
        void showNoMore();
        void showUnknownError(int code);
    }

}
