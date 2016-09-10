package com.hjsmallfly.syllabus.mvp.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.activities.PostContentActivity;
import com.hjsmallfly.syllabus.adapters.GridImageViewAdapter;
import com.hjsmallfly.syllabus.mvp.contract.PostContract;
import com.hjsmallfly.syllabus.pojo.PhotoList;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.syllabus.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DiscussItemLayoutHolder extends RecyclerView.ViewHolder implements PostContract.PostView {

    private ImageView discussAvatarImageView;
    private TextView discussSpeakerText;
    private TextView discussContent;
    private GridView discussGridImageView;
    private TextView discussTimeText;
    private ImageView likeImageView;
    private TextView likeCountTextView;
    private ImageView commentImageView;
    private TextView commentCountTextView;

    private Gson gson = new Gson();

    private PostContract.PostPresenter postPresenter;

    public DiscussItemLayoutHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.discuss_item_layout, parent, false));
    }

    public DiscussItemLayoutHolder(View view) {
        super(view);
        discussAvatarImageView = (ImageView) view.findViewById(R.id.discuss_avatar_image_view);
        discussSpeakerText = (TextView) view.findViewById(R.id.discuss_speaker_text);
        discussContent = (TextView) view.findViewById(R.id.discuss_content);
        discussGridImageView = (GridView) view.findViewById(R.id.discuss_grid_image_view);
        discussTimeText = (TextView) view.findViewById(R.id.discuss_time_text);
        likeImageView = (ImageView) view.findViewById(R.id.like_image_view);
        likeCountTextView = (TextView) view.findViewById(R.id.like_count_text_view);
        commentImageView = (ImageView) view.findViewById(R.id.comment_image_view);
        commentCountTextView = (TextView) view.findViewById(R.id.comment_count_text_view);
    }

    public static int get_like_id(int uid, Post post){
        for(int i = 0 ; i < post.thumbUps.size() ; ++i){
            if (post.thumbUps.get(i).uid == uid)
                return post.thumbUps.get(i).id;
        }
        return -1;
    }

    @Override
    public void setPresenter(PostContract.PostPresenter presenter){
        this.postPresenter = presenter;
    }


    @Override
    public void onLikeReturn(int code, Post post, String message) {
        if (code == 0){
            // 点赞成功
            likeCountTextView.setText(String.valueOf(post.thumbUps.size()));
            likeImageView.setImageResource(R.drawable.liked);
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postPresenter.onUnLikeButtonClicked();
                }
            });
        }else{
            Toast.makeText(likeImageView.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUnLikeReturn(int code, Post post, String message) {
        if (code == 0){
            // 取消点赞成功
            likeCountTextView.setText(String.valueOf(post.thumbUps.size()));
            likeImageView.setImageResource(R.drawable.to_like);
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postPresenter.onLikeButtonClicked();
                }
            });
        }else{
            Toast.makeText(likeImageView.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayPostBrief(Post post) {
        // ---------- 文字信息 ----------
        discussSpeakerText.setText(post.postUser.nickname);
        commentCountTextView.setText(String.valueOf(post.comments.size()));
        likeCountTextView.setText(String.valueOf(post.thumbUps.size()));
        discussTimeText.setText(post.postTime);
        discussContent.setText(post.content);
        // ---------- 文字信息 ----------

        // ---------- 点赞图片 ----------
        int like_id = get_like_id(MainActivity.user_id, post);
        // 判断用户是否点过赞, 设置相应的图片
        int likeResourceID;
        if (like_id != -1) { // 点过赞
            likeResourceID = R.drawable.liked;
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postPresenter.onUnLikeButtonClicked();
                }
            });
        }
        else {
            likeResourceID = R.drawable.to_like;
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postPresenter.onLikeButtonClicked();
                }
            });
        }

        likeImageView.setImageResource(likeResourceID);

        // ---------- 点赞图片 ----------

        // 点击显示详情
        commentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPresenter.displayDetailButtonClicked();
            }
        });

        commentCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPresenter.displayDetailButtonClicked();
            }
        });

        discussContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPresenter.displayDetailButtonClicked();
            }
        });

        // 设置头像
        if (post.postUser.image != null && !post.postUser.image.isEmpty()){
            // 显示图片
            Picasso.with(discussAvatarImageView.getContext()).load(post.postUser.image).error(R.mipmap.syllabus_icon2).into(discussAvatarImageView);
        }else{
            discussAvatarImageView.setImageResource(R.mipmap.syllabus_icon2);
        }

        // ---------- 显示post的图片 ----------
        PhotoList photoList = gson.fromJson(post.photoListJson, PhotoList.class);

        if (photoList != null){
            // 说明这个post有图片
            // 将显示图片的控件设置为可见
            discussGridImageView.setVisibility(View.VISIBLE);
            if (discussGridImageView.getAdapter() == null) {
                // 第一次处理这个view
                List<String> thumbnails = photoList.get_thumbnails();
                discussGridImageView.setAdapter(new GridImageViewAdapter(discussGridImageView.getContext(), thumbnails));
            }else{
                GridImageViewAdapter adapter = (GridImageViewAdapter) discussGridImageView.getAdapter();
                adapter.update_urls(photoList.get_thumbnails());
            }
        }else{
            // 没有图片, 则该控件不应该显示出来
            discussGridImageView.setVisibility(View.GONE);
        }
        // ---------- 显示post的图片 ----------
    }

    @Override
    public void displayPostDetail(Post post) {
        PostContentActivity.post = post;
        commentImageView.getContext().startActivity(new Intent(commentImageView.getContext(), PostContentActivity.class));
    }

    @Override
    public PostContract.PostPresenter getPresenter() {
        return postPresenter;
    }

}
