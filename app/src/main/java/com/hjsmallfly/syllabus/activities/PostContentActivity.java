package com.hjsmallfly.syllabus.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjsmallfly.syllabus.adapters.CommentAdapter;
import com.hjsmallfly.syllabus.adapters.GridImageViewAdapter;
import com.hjsmallfly.syllabus.adapters.PostAdapter;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.pojo.Comment;
import com.hjsmallfly.syllabus.pojo.CommentList;
import com.hjsmallfly.syllabus.pojo.CreatedReturnValue;
import com.hjsmallfly.syllabus.pojo.PhotoList;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.pojo.PostCommentTask;
import com.hjsmallfly.syllabus.pojo.PostThumbUp;
import com.hjsmallfly.syllabus.pojo.ThumbUpTask;
import com.hjsmallfly.syllabus.restful.GetCommentsApi;
import com.hjsmallfly.syllabus.restful.PostCommentApi;
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

    private EditText comment_edit;
    private AlertDialog.Builder dialog_builder;
    private AlertDialog dialog;

    // =============== views ================

    // API
    private PushThumbUpApi thumbUpApi;
    private GetCommentsApi getCommentApi;
    private PostCommentApi postCommentApi;

    private Gson gson = new Gson();

    private CommentAdapter commentAdapter;
    private List<Comment> comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);

        // 初始化 api
        thumbUpApi = SyllabusRetrofit.retrofit.create(PushThumbUpApi.class);
        getCommentApi = SyllabusRetrofit.retrofit.create(GetCommentsApi.class);
        postCommentApi = SyllabusRetrofit.retrofit.create(PostCommentApi.class);

//        GlobalDiscussActivity.need_to_update_posts = true;

        init_views();

        get_comments();
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


        comments_list_view = (ListView) findViewById(R.id.commentListView);
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

                    if (MainActivity.user_id == -1){
                        Toast.makeText(PostContentActivity.this, "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final ImageView imageView = (ImageView) v;
                    ThumbUpTask task = new ThumbUpTask(post.id, MainActivity.user_id, MainActivity.token);
                    Call<CreatedReturnValue> thumbUpCall = thumbUpApi.like_this(task);
                    thumbUpCall.enqueue(new Callback<CreatedReturnValue>() {
                        @Override
                        public void onResponse(Call<CreatedReturnValue> call, Response<CreatedReturnValue> response) {
                            if (response.isSuccessful()) {
//                                Toast.makeText(PostContentActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
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

            // 点击评论按钮 显示/隐藏 评论
            comment_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int flag = comments_list_view.getVisibility();
                    if (flag == View.VISIBLE)
                        comments_list_view.setVisibility(View.GONE);
                    else
                        comments_list_view.setVisibility(View.VISIBLE);
                }
            });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.make_comment:
                make_comment();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // =============== 跟UI有关的函数 ===============

    // =============== 其他函数 ===============

    private void make_comment(){

        if (MainActivity.user_id == -1){
            Toast.makeText(PostContentActivity.this, "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dialog_builder == null) {
            dialog_builder = new AlertDialog.Builder(this);

            if (comment_edit == null) {
                comment_edit = new EditText(this);
                comment_edit.setMaxLines(6);
            }

            dialog_builder.setView(comment_edit);
            dialog_builder.setTitle("请输入评论内容");
            dialog_builder.setCancelable(false);
            dialog_builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    PostCommentTask task = new PostCommentTask(post.id, MainActivity.user_id, comment_edit.getText().toString().trim(), MainActivity.token);
                    Call<Void> commentCall = postCommentApi.post_comment(task);
                    commentCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(PostContentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                comment_edit.setText("");
//                                post.comments.add(new PostComment());
                                get_comments();
//                                dialog.dismiss();
                            } else {
                                Toast.makeText(PostContentActivity.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(PostContentActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
            dialog_builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog = dialog_builder.create();
        }
        dialog.show();
    }

    private void display_comments(){
        if (comments != null && comments.size() > 0){
            if (commentAdapter == null){
                commentAdapter = new CommentAdapter(this, R.layout.comment_item_layout, comments);
                if (comments_list_view == null){
                    Toast.makeText(PostContentActivity.this, "strange!", Toast.LENGTH_SHORT).show();
                }else
                    comments_list_view.setAdapter(commentAdapter);
            }else
                commentAdapter.notifyDataSetChanged();
        }
    }

    private void get_comments(){
        Call<CommentList> call = getCommentApi.get_comments(post.id);
        call.enqueue(new Callback<CommentList>() {
            @Override
            public void onResponse(Call<CommentList> call, Response<CommentList> response) {
                if (response.isSuccessful()){
                    CommentList commentList = response.body();
//                    Toast.makeText(PostContentActivity.this, commentList.comments.size() + "", Toast.LENGTH_SHORT).show();
                    if (commentList.comments.size() > 0)
                        if (comments == null){
                            // 第一次
                            comments = commentList.comments;
                            display_comments();
                        }else{
                            // 更新数据
//                            commentAdapter.notifyDataSetChanged();
//                            Toast.makeText(PostContentActivity.this, "更新数据 " + commentList.comments.size(), Toast.LENGTH_SHORT).show();
                            comments.clear();
                            comments.addAll(commentList.comments);
                            display_comments();
                        }
                }else if (response.code() != 404){
                    Toast.makeText(PostContentActivity.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentList> call, Throwable t) {
                Toast.makeText(PostContentActivity.this, "网络错误, 请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // =============== 其他函数 ===============


}
