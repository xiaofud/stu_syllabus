package com.hjsmallfly.syllabus.mvp.viewholder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.hjsmallfly.syllabus.helpers.ClipBoardHelper;
import com.hjsmallfly.syllabus.interfaces.ItemRemovedListener;
import com.hjsmallfly.syllabus.mvp.contract.SinglePostContract;
import com.hjsmallfly.syllabus.pojo.PhotoList;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.syllabus.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder implements SinglePostContract.PostView {

    private ImageView discussAvatarImageView;
    private TextView discussSpeakerText;
    private TextView discussContent;
    private GridView discussGridImageView;
    private TextView discussTimeText;
    private ImageView likeImageView;
    private TextView likeCountTextView;
    private ImageView commentImageView;
    private TextView commentCountTextView;

    private List<ItemRemovedListener> itemRemovedListeners;

    private Gson gson = new Gson();

    private SinglePostContract.PostPresenter postPresenter;

    public PostViewHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.discuss_item_layout, parent, false));
    }

    public PostViewHolder(View view) {
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

    public void addItemRemovedListener(ItemRemovedListener listener){
        if (itemRemovedListeners == null)
            itemRemovedListeners = new ArrayList<>();
        itemRemovedListeners.add(listener);
    }

    public void notifyItemRemoved(){
        if (itemRemovedListeners == null)
            return;
        for(ItemRemovedListener listener: itemRemovedListeners)
            listener.onItemRemoved(getAdapterPosition());
    }

    public static int get_like_id(int uid, Post post){
        for(int i = 0 ; i < post.thumbUps.size() ; ++i){
            if (post.thumbUps.get(i).uid == uid)
                return post.thumbUps.get(i).id;
        }
        return -1;
    }

    @Override
    public void setPresenter(SinglePostContract.PostPresenter presenter){
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
    public void onDeleteReturn(int code, Post post, String message) {
        if (code == 0){
            Toast.makeText(discussContent.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
            notifyItemRemoved();
        }else
            Toast.makeText(discussContent.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayPostBrief(Post post) {
        // ---------- 文字信息 ----------
        discussSpeakerText.setText(post.postUser.nickname);
        commentCountTextView.setText(String.valueOf(post.comments.size()));
        likeCountTextView.setText(String.valueOf(post.thumbUps.size()));
        String postTimeInfo;    // 有可能有[来自]信息
        if (post.source != null && !post.source.isEmpty())
            postTimeInfo = post.postTime + " 来自" + post.source;
        else
            postTimeInfo = post.postTime;
        discussTimeText.setText(postTimeInfo);
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
        View.OnClickListener showDetailListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPresenter.displayDetailButtonClicked();
            }
        };
        commentImageView.setOnClickListener(showDetailListener);
        commentCountTextView.setOnClickListener(showDetailListener);
        discussContent.setOnClickListener(showDetailListener);

        // 弹出菜单
        discussContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                postPresenter.onLongClicked();
                return true;
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
    public void confirmOption(final Post post) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(discussContent.getContext());
        if (MainActivity.cur_username.equals("14xfdeng") || MainActivity.cur_username.equals("13yjli3") || MainActivity.cur_username.equals("14jhwang")){
            builder.setTitle("请选择一个操作" + "(" + post.postUser.account + ")");
        }else
            builder.setTitle("请选择一个操作");

        builder.setPositiveButton("复制", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipBoardHelper.setContent(discussContent.getContext(), post.content);
                Toast.makeText(discussContent.getContext(), "已将内容复制到剪贴板上", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MainActivity.user_id == -1){
                    Toast.makeText(discussContent.getContext(), "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
                    return;
                }
                postPresenter.onOptionConfirmed(SinglePostContract.PostView.OPTION_DELETE);
            }
        });
        builder.create().show();
    }

    @Override
    public SinglePostContract.PostPresenter getPresenter() {
        return postPresenter;
    }

}
