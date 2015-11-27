package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hjsmallfly.syllabus.syllabus.OAObject;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.List;

/**
 * Created by smallfly on 15-11-27.
 */
public class OAAdapter extends ArrayAdapter<OAObject> {

    private int layout_id;

    class ViewHolder{
        private TextView depart_view;
        private TextView time_text_view;
        private TextView title_view;
    }

    public OAAdapter(Context context, int resource, List<OAObject> objects) {
        super(context, resource, objects);
        this.layout_id = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 首先先获取数据
        OAObject oaObject = getItem(position);
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null) {
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.depart_view = (TextView) view.findViewById(R.id.oa_item_department_view);
            viewHolder.time_text_view = (TextView) view.findViewById(R.id.oa_item_time_view);
            viewHolder.title_view = (TextView) view.findViewById(R.id.oa_item_title_view);
            view.setTag(viewHolder);
        } else {  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 设置需要显示的数据
        String department = oaObject.department;
        String time_str = oaObject.date;
        String title = oaObject.title;

        viewHolder.depart_view.setText(department);
        viewHolder.time_text_view.setText(time_str);
        viewHolder.title_view.setText(title);
        return view;
    }
}
