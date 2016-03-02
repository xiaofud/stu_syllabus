package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hjsmallfly.syllabus.syllabus.Homework;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.List;

/**
 * Created by STU_nwad on 2015/10/11.
 */
public class HomeworkAdapter extends ArrayAdapter<Homework> {

    private int layout_id;

    class ViewHolder{
        TextView publisher_text;
        TextView publish_time_text;
        TextView homework_content;
    }

    public HomeworkAdapter(Context context, int layout_id, List<Homework> objects) {
        super(context, layout_id, objects);
        this.layout_id = layout_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Homework homework = getItem(position);  // 传进来的那个数据源
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null){
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.publisher_text = (TextView) view.findViewById(R.id.homework_publisher_text);
            viewHolder.publish_time_text = (TextView) view.findViewById(R.id.homework_pub_time);
            viewHolder.homework_content = (TextView) view.findViewById(R.id.homework_history_content);
            view.setTag(viewHolder);
        }else{  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 到这里布局就已经建立好了，接下来是设置数据这些
        viewHolder.publisher_text.setText("发布者: " + homework.publisher);
        viewHolder.publish_time_text.setText("发布时间: " + homework.transfer_time());
        viewHolder.homework_content.setText("内容:\n" + homework.content.trim());   // 去除没必要的空字符

        return view;    // view 里面的对象的属性是通过viewHolder修改的
    }



}
