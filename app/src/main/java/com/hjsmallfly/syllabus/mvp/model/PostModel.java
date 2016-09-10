package com.hjsmallfly.syllabus.mvp.model;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.mvp.contract.PostContract;
import com.hjsmallfly.syllabus.pojo.CreatedReturnValue;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.pojo.PostThumbUp;
import com.hjsmallfly.syllabus.pojo.ThumbUpTask;
import com.hjsmallfly.syllabus.pojo.User;
import com.hjsmallfly.syllabus.restful.PushThumbUpApi;
import com.hjsmallfly.syllabus.restful.UnLikeApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smallfly on 16-9-10.
 * 用于处理Post的事务
 */
public class PostModel {

    private PostContract.PostPresenter postPresenter;
    private PushThumbUpApi thumbUpApi;
    private UnLikeApi unLikeApi;

    public PostModel(){
        thumbUpApi = SyllabusRetrofit.retrofit.create(PushThumbUpApi.class);
        unLikeApi = SyllabusRetrofit.retrofit.create(UnLikeApi.class);
    }

    public void setPostPresenter(PostContract.PostPresenter presenter){
        this.postPresenter = presenter;
    }

    public void like(final Post post, int userID, String token){
        ThumbUpTask task = new ThumbUpTask(post.id, userID, token);
        Call<CreatedReturnValue> thumbUpCall = thumbUpApi.like_this(task);
        thumbUpCall.enqueue(new Callback<CreatedReturnValue>() {
            @Override
            public void onResponse(Call<CreatedReturnValue> call, Response<CreatedReturnValue> response) {
                if (response.isSuccessful()) {
                    int id = response.body().id;
                    post.thumbUps.add(new PostThumbUp(id, 1));
                    postPresenter.onLikeReturn(0, post, "点赞成功");
                } else if (response.code() == 401) {
                    postPresenter.onLikeReturn(response.code(), post, "登录超时, 请同步一下课表");
                } else if (response.code() == 403) {
                    postPresenter.onLikeReturn(response.code(), post, "已经赞过了");
                }
            }

            @Override
            public void onFailure(Call<CreatedReturnValue> call, Throwable t) {
                postPresenter.onLikeReturn(1, post,"网络错误");
            }
        });
    }

    public void unlike(final Post post, int userID, String token){
        Call<Void> unlike_it = unLikeApi.unlike_this(get_like_id(userID, post), userID, token);
        unlike_it.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    PostModel.remove_like(MainActivity.user_id, post);
                    postPresenter.onUnLikeReturn(0, post, "取消点赞成功");
                }else if (response.code() == 401){
                    postPresenter.onUnLikeReturn(response.code(), post, "登录超时, 请同步一下课表");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                postPresenter.onUnLikeReturn(1, post, "网络连接超时");
            }
        });
    }

    public static int get_like_id(int uid, Post post){
        for(int i = 0 ; i < post.thumbUps.size() ; ++i){
            if (post.thumbUps.get(i).uid == uid)
                return post.thumbUps.get(i).id;
        }
        return -1;
    }

    public static void remove_like(int uid, Post post){
        for(int i = 0 ; i < post.thumbUps.size() ; ++i){
            if (post.thumbUps.get(i).uid == uid)
                post.thumbUps.remove(i);
        }
    }

}
