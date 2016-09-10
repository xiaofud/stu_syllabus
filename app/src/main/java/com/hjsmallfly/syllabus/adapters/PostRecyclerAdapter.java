package com.hjsmallfly.syllabus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hjsmallfly.syllabus.mvp.contract.PostContract;
import com.hjsmallfly.syllabus.mvp.model.PostModel;
import com.hjsmallfly.syllabus.mvp.presenter.PostPresenter;
import com.hjsmallfly.syllabus.mvp.viewholder.DiscussItemLayoutHolder;
import com.hjsmallfly.syllabus.pojo.Post;

import java.util.List;

/**
 * Created by smallfly on 16-9-10.
 *
 */
public class PostRecyclerAdapter extends RecyclerView.Adapter<DiscussItemLayoutHolder> {

    private List<Post> posts;

    public PostRecyclerAdapter(List<Post> posts){
        this.posts = posts;
    }

    @Override
    public DiscussItemLayoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DiscussItemLayoutHolder viewHolder = new DiscussItemLayoutHolder(LayoutInflater.from(parent.getContext()), parent);
        PostModel postModel = new PostModel();
        PostContract.PostPresenter presenter = new PostPresenter(postModel, viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscussItemLayoutHolder holder, int position) {
        Post post = posts.get(position);
        holder.getPresenter().setPost(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
