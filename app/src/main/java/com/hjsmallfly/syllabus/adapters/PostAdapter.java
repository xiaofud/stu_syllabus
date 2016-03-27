package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.pojo.PhotoList;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.syllabus.Discussion;
import com.hjsmallfly.syllabus.syllabus.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by STU_nwad on 2015/10/11.
 *
 */
public class PostAdapter extends ArrayAdapter<Post> {

    private Gson gson = new GsonBuilder().create();

    class ViewHolder{
        private ImageView avatarView;   // 头像

        private ImageView like_image_view;
        private TextView like_count_text_view;

        private ImageView comment_image_view;
        private TextView comment_count_text_view;

        private GridView post_photos_grid_view;

        private TextView publisher_text;
        private TextView pub_time_text;
        private TextView content_text;


    }

    private int layout_id;

//    private static LinearLayout.LayoutParams params;

    // 构造函数
    public PostAdapter(Context context, int resource, List<Post> objects) {
        super(context, resource, objects);
        this.layout_id = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Post post = getItem(position);  // 传进来的那个数据源
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null){
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.avatarView = (ImageView) view.findViewById(R.id.discuss_avatar_image_view);

            viewHolder.like_image_view = (ImageView) view.findViewById(R.id.like_image_view);
            viewHolder.like_count_text_view = (TextView) view.findViewById(R.id.like_count_text_view);

            viewHolder.comment_image_view = (ImageView) view.findViewById(R.id.comment_image_view);
            viewHolder.comment_count_text_view = (TextView) view.findViewById(R.id.comment_count_text_view);

            viewHolder.post_photos_grid_view = (GridView) view.findViewById(R.id.discuss_grid_image_view);

            viewHolder.publisher_text = (TextView) view.findViewById(R.id.discuss_speaker_text);
            viewHolder.pub_time_text = (TextView) view.findViewById(R.id.discuss_time_text);
            viewHolder.content_text = (TextView) view.findViewById(R.id.discuss_content);
            view.setTag(viewHolder);
        }else{  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 设置头像
        if (post.postUser.image != null && !post.postUser.image.isEmpty()){
            // 显示图片
            Picasso.with(getContext()).load(post.postUser.image).into(viewHolder.avatarView);
        }else{
            viewHolder.avatarView.setImageResource(R.mipmap.syllabus_icon2);
        }



        if (viewHolder.post_photos_grid_view.getAdapter() == null) {
            PhotoList photoList = gson.fromJson(post.photoListJson, PhotoList.class);
//            Toast.makeText(getContext(), post.photoListJson, Toast.LENGTH_SHORT).show();
            List<String> thumbnails = photoList.get_thumbnails();
            Toast.makeText(getContext(), "" + photoList.photo_list.size(), Toast.LENGTH_SHORT).show();
            viewHolder.post_photos_grid_view.setAdapter(new GridImageViewAdapter(getContext(), thumbnails));
        }

        viewHolder.publisher_text.setText(post.postUser.nickname);
        viewHolder.pub_time_text.setText(post.postTime);
        viewHolder.content_text.setText(post.content.trim());   // 去除没必要的空字符

        String like_count = post.thumbUps.size() + "";
        String comment_count = post.comments.size() + "";
        viewHolder.like_count_text_view.setText(like_count);
        viewHolder.comment_count_text_view.setText(comment_count);


        return view;    // view 里面的对象的属性是通过viewHolder修改的
    }


}
