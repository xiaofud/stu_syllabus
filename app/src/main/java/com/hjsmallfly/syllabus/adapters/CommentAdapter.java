package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hjsmallfly.syllabus.pojo.Comment;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.List;

/**
 * Created by smallfly on 16-3-28.
 * 显示评论
 */
public class CommentAdapter extends ArrayAdapter<Comment> {

    private int layout_id;

    class ViewHolder{
        private TextView nickname_view;
        private TextView time_text_view;
        private TextView content_view;
    }

    public CommentAdapter(Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);
        this.layout_id = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 首先先获取数据
        Comment comment = getItem(position);
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null) {
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.nickname_view = (TextView) view.findViewById(R.id.comment_nickname);
            viewHolder.time_text_view = (TextView) view.findViewById(R.id.comment_time);
            viewHolder.content_view = (TextView) view.findViewById(R.id.comment_content);
            view.setTag(viewHolder);
        } else {  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 设置需要显示的数据
        String nickname = comment.nickname;
        String time_str = comment.postTime;
        String comment_content = comment.comment;

        viewHolder.nickname_view.setText(nickname);
        viewHolder.time_text_view.setText(time_str);
        viewHolder.content_view.setText(comment_content);
        return view;
    }
}
