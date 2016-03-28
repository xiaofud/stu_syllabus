package com.hjsmallfly.syllabus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjsmallfly.syllabus.adapters.GridImageViewAdapter;
import com.hjsmallfly.syllabus.adapters.PostAdapter;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.pojo.CreatedReturnValue;
import com.hjsmallfly.syllabus.pojo.PhotoList;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.pojo.PostThumbUp;
import com.hjsmallfly.syllabus.pojo.ThumbUpTask;
import com.hjsmallfly.syllabus.restful.PushThumbUpApi;
import com.hjsmallfly.syllabus.syllabus.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostContentActivity extends AppCompatActivity {

    public static Post post;

    // =============== views ================
    private ImageView avatarView;   // 头像

    private ImageView like_image_view;
    private TextView like_count_text_view;

    private ImageView comment_image_view;
    private TextView comment_count_text_view;

    private GridView post_photos_grid_view;

    private TextView publisher_text;
    private TextView pub_time_text;
    private TextView content_text;

    private ListView comments_list_view;

    // =============== views ================

    // API
    private PushThumbUpApi thumbUpApi;

    private Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);

        thumbUpApi = SyllabusRetrofit.retrofit.create(PushThumbUpApi.class);

//        GlobalDiscussActivity.need_to_update_posts = true;

        init_views();
    }


    // =============== 跟UI有关的函数 ===============

    private void find_views(){
        // include 的内容
        View view = findViewById(R.id.post_area);
        avatarView = (ImageView) view.findViewById(R.id.discuss_avatar_image_view);

        like_image_view = (ImageView) view.findViewById(R.id.like_image_view);
        like_count_text_view = (TextView) view.findViewById(R.id.like_count_text_view);

        comment_image_view = (ImageView) view.findViewById(R.id.comment_image_view);
        comment_count_text_view = (TextView) view.findViewById(R.id.comment_count_text_view);

        post_photos_grid_view = (GridView) view.findViewById(R.id.discuss_grid_image_view);

        publisher_text = (TextView) view.findViewById(R.id.discuss_speaker_text);
        pub_time_text = (TextView) view.findViewById(R.id.discuss_time_text);
        content_text = (TextView) view.findViewById(R.id.discuss_content);

        comments_list_view = (ListView) view.findViewById(R.id.comments_list_view);
    }

    private void setup_views(){
        if (post != null){

            // 设置头像
            if (post.postUser.image != null && !post.postUser.image.isEmpty()){
                // 显示图片
                Picasso.with(PostContentActivity.this).load(post.postUser.image).error(R.mipmap.syllabus_icon2).into(avatarView);
            }else{
                avatarView.setImageResource(R.mipmap.syllabus_icon2);
            }

            final int like_id = PostAdapter.get_like_id(1, post);

            // 判断用户是否点过赞, 设置相应的图片
            if (like_id != -1){ // 点过赞
                like_image_view.setImageResource(R.drawable.liked);
            }else{
                like_image_view.setImageResource(R.drawable.to_like);
            }

            // 设置监听器
            // 因为会重用, 所以暂时先每次都添加解决冲突
            like_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ImageView imageView = (ImageView) v;
                    ThumbUpTask task = new ThumbUpTask(post.id, 1, "000000");
                    Call<CreatedReturnValue> thumbUpCall = thumbUpApi.like_this(task);
                    thumbUpCall.enqueue(new Callback<CreatedReturnValue>() {
                        @Override
                        public void onResponse(Call<CreatedReturnValue> call, Response<CreatedReturnValue> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(PostContentActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                int id = response.body().id;
                                imageView.setImageResource(R.drawable.liked);
                                post.thumbUps.add(new PostThumbUp(id, 1));
                                setup_views();
//                                notifyDataSetChanged();
                            } else if (response.code() == 401) {
                                Toast.makeText(PostContentActivity.this, "登录超时, 请同步一下课表", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 403) {
                                Toast.makeText(PostContentActivity.this, "已经赞过了", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CreatedReturnValue> call, Throwable t) {
                            Toast.makeText(PostContentActivity.this, "网络错误, 请重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
//        }

            // 先判断这个post有没有图片
            PhotoList photoList = gson.fromJson(post.photoListJson, PhotoList.class);

            if (photoList != null){
                // 说明这个post有图片
                // 将显示图片的控件设置为可见
                post_photos_grid_view.setVisibility(View.VISIBLE);
                if (post_photos_grid_view.getAdapter() == null) {
                    // 第一次处理这个view
                    List<String> thumbnails = photoList.get_thumbnails();
                    post_photos_grid_view.setAdapter(new GridImageViewAdapter(PostContentActivity.this, thumbnails));
                }else{
                    GridImageViewAdapter adapter = (GridImageViewAdapter) post_photos_grid_view.getAdapter();
                    adapter.update_urls(photoList.get_thumbnails());
                }
            }else{
                // 没有图片, 则该控件不应该显示出来
                post_photos_grid_view.setVisibility(View.GONE);
            }

            publisher_text.setText(post.postUser.nickname);
            pub_time_text.setText(post.postTime);
            content_text.setText(post.content.trim());   // 去除没必要的空字符

            String like_count = post.thumbUps.size() + "";
            String comment_count = post.comments.size() + "";
            like_count_text_view.setText(like_count);
            comment_count_text_view.setText(comment_count);

        }
    }

    private void init_views(){
        find_views();
        setup_views();
    }

    // =============== 跟UI有关的函数 ===============

}
