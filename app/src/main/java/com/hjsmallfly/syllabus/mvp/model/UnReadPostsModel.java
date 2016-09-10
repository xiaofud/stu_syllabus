package com.hjsmallfly.syllabus.mvp.model;

import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.mvp.contract.PostsDataSource;
import com.hjsmallfly.syllabus.mvp.contract.UnreadDataSource;
import com.hjsmallfly.syllabus.pojo.PostList;
import com.hjsmallfly.syllabus.restful.UnreadAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smallfly on 16-9-10.
 *
 */
public class UnReadPostsModel implements UnreadDataSource {

    private UnreadAPI unreadAPI;

    public UnReadPostsModel(){
        unreadAPI = SyllabusRetrofit.retrofit.create(UnreadAPI.class);
    }

    @Override
    public void getUnreadMessage(int userID, int type, final PostsDataSource.postsCallback callback) {
        Call<PostList> unreadCall = unreadAPI.getUnreadMessage(userID, type);
        unreadCall.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    callback.onPosts(PostsDataSource.postsCallback.OK, response.body());
                }else if (response.code() == 404)
                    callback.onPosts(PostsDataSource.postsCallback.NO_MORE, null);
                else
                    callback.onPosts(response.code(), null);
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                callback.onPosts(PostsDataSource.postsCallback.INTERNET_ERROR, null);
            }
        });
    }
}
