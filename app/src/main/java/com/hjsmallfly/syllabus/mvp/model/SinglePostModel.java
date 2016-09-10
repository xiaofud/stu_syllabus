package com.hjsmallfly.syllabus.mvp.model;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.mvp.contract.SinglePostDataSource;
import com.hjsmallfly.syllabus.pojo.CreatedReturnValue;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.pojo.PostThumbUp;
import com.hjsmallfly.syllabus.pojo.ThumbUpTask;
import com.hjsmallfly.syllabus.restful.DeletePostApi;
import com.hjsmallfly.syllabus.restful.PushThumbUpApi;
import com.hjsmallfly.syllabus.restful.UnLikeApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smallfly on 16-9-10.
 * 用于处理Post的事务
 */
public class SinglePostModel implements SinglePostDataSource {

    // -------- API --------
    private PushThumbUpApi thumbUpApi;
    private UnLikeApi unLikeApi;
    private DeletePostApi deletePostApi;

    public SinglePostModel(){
        thumbUpApi = SyllabusRetrofit.retrofit.create(PushThumbUpApi.class);
        unLikeApi = SyllabusRetrofit.retrofit.create(UnLikeApi.class);
        deletePostApi = SyllabusRetrofit.retrofit.create(DeletePostApi.class);
    }



    @Override
    public void like(final Post post, int userID, String token, final PostCallback callback) {
        ThumbUpTask task = new ThumbUpTask(post.id, userID, token);
        Call<CreatedReturnValue> thumbUpCall = thumbUpApi.like_this(task);
        thumbUpCall.enqueue(new Callback<CreatedReturnValue>() {
            @Override
            public void onResponse(Call<CreatedReturnValue> call, Response<CreatedReturnValue> response) {
                if (response.isSuccessful()) {
                    int id = response.body().id;
                    post.thumbUps.add(new PostThumbUp(id, MainActivity.user_id));
                    callback.onLikePost(0, post, "点赞成功");
                } else if (response.code() == 401) {
                    callback.onLikePost(response.code(), post, "登录超时, 请同步一下课表");
                } else if (response.code() == 403) {
                    callback.onLikePost(response.code(), post, "已经赞过了");
                }
            }

            @Override
            public void onFailure(Call<CreatedReturnValue> call, Throwable t) {
                callback.onLikePost(1, post,"网络错误");
            }
        });
    }

    @Override
    public void unlike(final Post post, int userID, String token, final PostCallback callback) {
        Call<Void> unlike_it = unLikeApi.unlike_this(get_like_id(userID, post), userID, token);
        unlike_it.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    SinglePostModel.remove_like(MainActivity.user_id, post);
                    callback.onUnlikePost(0, post, "取消点赞成功");
                }else if (response.code() == 401){
                    callback.onUnlikePost(response.code(), post, "登录超时, 请同步一下课表");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onUnlikePost(1, post, "网络连接超时");
            }
        });
    }

    @Override
    public void delete(final Post post, int userID, String token, final PostCallback callback) {
        if (MainActivity.user_id == -1) {
//            Toast.makeText(getContext(), "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
            callback.onDelete(1, post, "登录超时, 请同步一次课表");
            return;
        }

        deletePostApi = SyllabusRetrofit.retrofit.create(DeletePostApi.class);
        Call<Void> call = deletePostApi.delete_post(post.id, userID, token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onDelete(0, post, "删除成功");

                } else if (response.code() == 401) {
//                    Toast.makeText(getContext(), "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 403) {
//                    Toast.makeText(getContext(), "不能删除别人的资源", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(getContext(), "网络错误, 请重试", Toast.LENGTH_SHORT).show();
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
