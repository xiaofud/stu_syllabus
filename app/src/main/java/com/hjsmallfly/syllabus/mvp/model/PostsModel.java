package com.hjsmallfly.syllabus.mvp.model;

import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.mvp.contract.PostsDataSource;
import com.hjsmallfly.syllabus.pojo.PostList;
import com.hjsmallfly.syllabus.restful.GetPostsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smallfly on 16-9-10.
 */
public class PostsModel implements PostsDataSource {

//    public final int NUMBER_OF_POSTS_PER_PULL = 10;

    private GetPostsApi getPostsApi;

    public PostsModel(){
        getPostsApi = SyllabusRetrofit.retrofit.create(GetPostsApi.class);
    }

    @Override
    public void fetchPosts(int count, int before_id, final postsCallback callback) {
        Call<PostList> postListCall = getPostsApi.get_posts(count, before_id);
        postListCall.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()){
                    callback.onPosts(postsCallback.OK, response.body());
                }else if (response.code() == 404)
                    callback.onPosts(postsCallback.NO_MORE, null);
                else
                    callback.onPosts(response.code(), null);
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                callback.onPosts(postsCallback.INTERNET_ERROR, null);
            }
        });
    }
}
