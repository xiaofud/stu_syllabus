package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hjsmallfly.syllabus.syllabus.Exam;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.List;

/**
 * Created by smallfly on 15-11-23.
 */
public class ExamAdapter extends ArrayAdapter<Exam> {

//    private Context context;
    private int layout_id;

    class ViewHolder{
        private TextView location_view;
        private TextView name_text_view;
        private TextView time_text_view;
    }

    public ExamAdapter(Context context, int resource, List<Exam> objects) {
        super(context, resource, objects);

//        this.context = context;
        this.layout_id = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 首先先获取数据
        Exam exam = getItem(position);
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null) {
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.location_view = (TextView) view.findViewById(R.id.exam_location_text_view);
            viewHolder.name_text_view = (TextView) view.findViewById(R.id.exam_class_text_view);
            viewHolder.time_text_view = (TextView) view.findViewById(R.id.exam_time_text_view);
            view.setTag(viewHolder);
        } else {  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 设置需要显示的数据
        String location = exam.exam_location;
        String name = exam.exam_class;
        String time_str = exam.exam_time;

        viewHolder.location_view.setText(location);
        viewHolder.name_text_view.setText(name);
        viewHolder.time_text_view.setText(time_str);

        return view;
    }
}
