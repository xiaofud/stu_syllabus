package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.syllabus.R;

/**
 * Created by STU_nwad on 2015/10/1.
 */
public class ListViewAdapter extends BaseAdapter {

    // 存放近四年的课表
    public static final int COUNT = 4;

    private  String[] syllabus_data;// = new String[4];
    private Context context;

    class ViewHolder{
        TextView year_text;
        TextView spring_text;
        TextView summer_text;
        TextView autumn_text;
    }

    private void init(){
        syllabus_data = StringDataHelper.generate_years(COUNT);
        for(int i = 0 ; i < syllabus_data.length ; ++i){
            syllabus_data[i] =  syllabus_data[i].replace("-", "\n");    // 格式一下
        }
    }

    private LayoutInflater layoutInflater;

    public ListViewAdapter(Context context){
        super();
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        init();
    }

    @Override
    public int getCount() {
        return syllabus_data.length;
    }

    @Override
    public Object getItem(int position) {
        return syllabus_data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder.year_text = (TextView) convertView.findViewById(R.id.year_text_view);
            holder.spring_text = (TextView) convertView.findViewById(R.id.spring_text_view);
            holder.summer_text = (TextView) convertView.findViewById(R.id.summer_text_view);
            holder.autumn_text = (TextView) convertView.findViewById(R.id.autumn_text_view);
            // 添加监听器
            holder.spring_text.setClickable(true);
            holder.summer_text.setClickable(true);
            holder.autumn_text.setClickable(true);

            MainActivity mainActivity = (MainActivity) context;
//            View.OnClickListener listener = mainActivity.getOnClickListener(position);
//            View.OnLongClickListener longClickListener = mainActivity.getOnLongClickListener(position);
//
//            holder.spring_text.setOnClickListener(listener);
//            holder.summer_text.setOnClickListener(listener);
//            holder.autumn_text.setOnClickListener(listener);
//
//            holder.spring_text.setOnLongClickListener(longClickListener);
//            holder.summer_text.setOnLongClickListener(longClickListener);
//            holder.autumn_text.setOnLongClickListener(longClickListener);

            convertView.setTag(holder);
        }else{  // 即之前缓存过的
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置数据
        holder.year_text.setText(syllabus_data[position]);
//        holder.spring_text.setText("春季学期\n一共25学分");

        return convertView;
    }
}
