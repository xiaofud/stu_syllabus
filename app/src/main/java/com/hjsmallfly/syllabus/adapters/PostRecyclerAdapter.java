package com.hjsmallfly.syllabus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hjsmallfly.syllabus.interfaces.ItemRemovedListener;
import com.hjsmallfly.syllabus.mvp.model.SinglePostModel;
import com.hjsmallfly.syllabus.mvp.presenter.SinglePostPresenter;
import com.hjsmallfly.syllabus.mvp.viewholder.PostViewHolder;
import com.hjsmallfly.syllabus.pojo.Post;

import java.util.List;


/**
 * Created by smallfly on 16-9-10.
 *
 */
public class PostRecyclerAdapter extends RecyclerView.Adapter<PostViewHolder> implements ItemRemovedListener {

    /**
     * 存放所有的posts
     */
    private List<Post> posts;

    public PostRecyclerAdapter(List<Post> posts){
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PostViewHolder viewHolder = new PostViewHolder(LayoutInflater.from(parent.getContext()), parent);
        SinglePostModel postModel = new SinglePostModel();
        viewHolder.addItemRemovedListener(this);
        new SinglePostPresenter(postModel, viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.getPresenter().setPost(post);
    }

    @Override
    public void onItemRemoved(int position) {
        posts.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
