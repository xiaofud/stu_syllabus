package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hjsmallfly.syllabus.syllabus.Grade;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.List;

/**
 * Created by smallfly on 15-11-22.
 * 成绩的数据源
 */
public class GradeAdapter extends ArrayAdapter<Grade> {

    private int layout_id;

    class ViewHolder{
        private TextView type_information;
        private TextView name_text_view;
        private TextView grade_text_view;
        private TextView credit_text_view;
    }

    public GradeAdapter(Context context, int resource, List<Grade> objects) {
        super(context, resource, objects);

        this.layout_id = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 首先先获取数据
        Grade grade = getItem(position);
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null) {
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.credit_text_view = (TextView) view.findViewById(R.id.grade_credit_text_view);
            viewHolder.grade_text_view = (TextView) view.findViewById(R.id.grade_grade_text_view);
            viewHolder.name_text_view = (TextView) view.findViewById(R.id.grade_class_name);
            viewHolder.type_information = (TextView) view.findViewById(R.id.grade_year_semester_info);

            view.setTag(viewHolder);
        } else {  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 设置需要显示的数据
        String type_info = grade.years + " " + grade.semester;
        String name = Grade.get_simple_name(grade.class_name);
        String score = grade.class_grade;
        String credit = grade.class_credit;

        viewHolder.type_information.setText(type_info);
        viewHolder.name_text_view.setText(name);
        viewHolder.grade_text_view.setText(score);
        viewHolder.credit_text_view.setText(credit);

        // 隐藏不必要的学期信息
        if (position > 0){
            Grade pre_grade = getItem(position - 1);
            if (pre_grade.years.equals(grade.years) && pre_grade.semester.equals(grade.semester))
                viewHolder.type_information.setVisibility(View.GONE);
            else{
                viewHolder.type_information.setVisibility(View.VISIBLE);
            }
        }else{
            viewHolder.type_information.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
