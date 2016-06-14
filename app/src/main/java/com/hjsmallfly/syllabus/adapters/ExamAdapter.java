package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hjsmallfly.syllabus.syllabus.Exam;
import com.hjsmallfly.syllabus.syllabus.R;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by smallfly on 15-11-23.
 */
public class ExamAdapter extends ArrayAdapter<Exam> {

    //    private Context context;
    private int layout_id;

    class ViewHolder {
        private TextView location_view;
        private TextView name_text_view;
        private TextView time_text_view;
        private TextView sit_position_text_view;
        private TextView comment_text_view;
        private TextView tv_near_exam_time;
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
            viewHolder.sit_position_text_view = (TextView) view.findViewById(R.id.sit_position);
            viewHolder.comment_text_view = (TextView) view.findViewById(R.id.exam_comment_text_view);
            viewHolder.tv_near_exam_time = (TextView) view.findViewById(R.id.tv_near_exam_time);

            view.setTag(viewHolder);
        } else {  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 设置需要显示的数据
        String location = "教室: "+exam.exam_location;
        String name = "考试科目: " + exam.exam_class;
        String time_str = exam.exam_time;
        String sit_position = "座位号: " + exam.exam_stu_position;
        String exam_comment = "备注信息: ";
        if (exam.exam_comment.trim().isEmpty()) {
            exam_comment += "无";
        } else {
            exam_comment += exam.exam_comment;
        }
        viewHolder.location_view.setText(location);
        viewHolder.name_text_view.setText(name.replaceAll("\\[(\\w)*\\]",""));
        viewHolder.time_text_view.setText(time_str.replace("(","\n("));
        viewHolder.sit_position_text_view.setText(sit_position);
        viewHolder.comment_text_view.setText(exam_comment);

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        nowCal.setTime(new Date());
//        SimpleDateFormat format = new SimpleDateFormat("MM_dd天HH小时mm分ss秒");

        Calendar examCal = Calendar.getInstance();
        examCal.setTimeZone(nowCal.getTimeZone());
        int year = Integer.parseInt(time_str.substring(11, 15));
        int month = Integer.parseInt(time_str.substring(16, 18));
        int day = Integer.parseInt(time_str.substring(19, 21));

        int hour = Integer.parseInt(time_str.substring(23, 25));
        int minute = Integer.parseInt(time_str.substring(26, 28));

        examCal.set(year, month - 1, day, hour, minute, 0);

        Calendar nearCal = Calendar.getInstance();
        nearCal.setTimeZone(nowCal.getTimeZone());
        Date date = new Date(examCal.getTimeInMillis() - nowCal.getTimeInMillis());
        nearCal.setTime(date);
        nearCal.add(Calendar.HOUR_OF_DAY, -8);
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        /*Log.d("time", "nowCal: " + format.format(nowCal.getTime()));
        Log.d("time", "examCal: " + format.format(examCal.getTime()) + " " + nearCal.getTimeZone());
        Log.d("time", "nearCal: " + format.format(nearCal.getTime()) + " " + nearCal.getTimeZone());*/

        if (examCal.compareTo(nowCal) > 0) {
            StringBuilder nearTimeString = new StringBuilder();
            if (nearCal.get(Calendar.DAY_OF_MONTH) - 1 > 0) {
                nearTimeString.append(nearCal.get(Calendar.DAY_OF_MONTH) - 1);
                nearTimeString.append("天 ");
            }
            nearTimeString.append(String.format("%02d:%02d:%02d",
                    nearCal.get(Calendar.HOUR_OF_DAY),
                    nearCal.get(Calendar.MINUTE),
                    nearCal.get(Calendar.SECOND)));

            viewHolder.tv_near_exam_time.setText("离考试还剩\n" + nearTimeString);
        } else {
            viewHolder.tv_near_exam_time.setVisibility(View.GONE);
        }
        return view;
    }
}
