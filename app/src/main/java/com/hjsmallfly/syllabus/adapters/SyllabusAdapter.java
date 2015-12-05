package com.hjsmallfly.syllabus.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.parsers.ClassParser;
import com.hjsmallfly.syllabus.syllabus.Lesson;
import com.hjsmallfly.syllabus.activities.SyllabusActivity;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by STU_nwad on 2015/9/23.
 */
public class SyllabusAdapter extends RecyclerView.Adapter<SyllabusAdapter.ViewHolder> {
    private Object[] data_set;
    private SyllabusActivity syllabusActivity;

    private int text_color = Color.WHITE;

    public static final int[] class_cell_drawable = {R.drawable.blue_cell, R.drawable.brown_cell, R.drawable.deep_red_cell, R.drawable.green_cell
        , R.drawable.input_box, R.drawable.red_cell, R.drawable.purple_cell};

    public static final Random rand = new Random(System.currentTimeMillis());

//    public static int get_random_cell(){
//        int index = rand.nextInt(class_cell_drawable.length);
//        return class_cell_drawable[index];
//    }

    public void set_color(int color){
        text_color = color;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    private ArrayList<ViewHolder> all_view_holders = new ArrayList<>();

    public void set_text_color(int color){
        for(ViewHolder vh: all_view_holders){
            vh.mTextView.setTextColor(color);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public SyllabusAdapter(Object[] my_data_set, SyllabusActivity syllabusActivity) {
        this.data_set = my_data_set;
        this.syllabusActivity = syllabusActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SyllabusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
//        android.R.layout.simple_list_item_1
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(android.R.layout.simple_list_item_1, parent, false);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_cell_text_view, parent, false);

        // set the view's size, margins, paddings and layout parameters
//        TextView text_view = (TextView) v.findViewById(R.id.class_grid_text);

        ViewHolder vh = new ViewHolder((TextView) v);
        vh.mTextView.setTextColor(text_color);
        all_view_holders.add(vh);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // 同上的情况不加框框了
        if (data_set[position] instanceof Lesson /*|| data_set[position].toString().length() == 2*/) {
            holder.mTextView.setBackgroundResource(( (Lesson) data_set[position] ).color_code);
            holder.mTextView.getBackground().setAlpha(150);  // 透明  [0,255]
        }
        else
            holder.mTextView.setBackgroundResource(0);

        String display_message = data_set[position].toString();
        // 在这里添加单双周信息
        if (data_set[position] instanceof Lesson){
            Lesson lesson = (Lesson) data_set[position];

            // 行数，也就是课程的上课节数
//            int divided_by_column = position / ClassParser.COLUMNS;
            // 上课的星期数
            int mod_by_column = position % ClassParser.COLUMNS;
            for(String key: lesson.days.keySet()){
                String time_str = lesson.days.get(key);
                String week_day_str = "w" + mod_by_column;
                if (!key.equals(week_day_str))
                    continue;

                boolean has_double = time_str.contains("双");
                boolean has_single = time_str.contains("单");
                if (has_double)
                    display_message = "[双]" + display_message;
                else if (has_single)
                    display_message = "[单]" + display_message;
            }
        }
//        else{
//            holder.mTextView.setTextSize(14);
//        }

        holder.mTextView.setOnClickListener(new ClickAndShow(position));    // 至于被点击的具体是什么内容就由 ClickAndShow 决定了
        holder.mTextView.setText(display_message);
        holder.mTextView.setClickable(true);
        holder.mTextView.setTextSize(12);
        holder.mTextView.setTextColor(text_color);


    }

    class ClickAndShow implements View.OnClickListener{

        private int position = -1;

        public ClickAndShow(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
                // 因为安卓会重用view, 所以这里需要注意判断一下，点击的到底是不是真正的课程
                TextView view = (TextView) v;
                String text = view.getText().toString();
//                view.setText("被点了");

                if (text.isEmpty())
                    return;

                // 左边的具体上课节数
                if (ClassParser.class_table.contains(text)){
                    String[] time_ = ClassParser.time_table.get(text).split(",");   // {"8:00", "8:50"}
                    Toast.makeText(syllabusActivity,  text +  " 上课时间: " + time_[0] + " 至 " + time_[1] , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (text.length() == 2 /*|| text.length() == 3*/) // 点了同上
                    return;

                // 点击的是星期几
                if (position >= 1 && position <= 5)
                    return;

                // 这里以后才是真的课程哟~
                syllabusActivity.showClassInfo((Lesson) data_set[position]);

            }
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data_set.length;
    }
}