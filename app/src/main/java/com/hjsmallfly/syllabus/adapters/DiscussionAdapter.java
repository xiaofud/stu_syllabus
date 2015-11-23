package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.syllabus.Discussion;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.List;

/**
 * Created by STU_nwad on 2015/10/11.
 */
public class DiscussionAdapter extends ArrayAdapter<Discussion> {

    class ViewHolder{
        private TextView publisher_text;
        private TextView pub_time_text;
        private TextView content_text;

    }

    private int layout_id;

    private static LinearLayout.LayoutParams params;

    // 构造函数
    public DiscussionAdapter(Context context, int resource, List<Discussion> objects) {
        super(context, resource, objects);
        this.layout_id = resource;
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Discussion discussion = getItem(position);  // 传进来的那个数据源
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null){
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.publisher_text = (TextView) view.findViewById(R.id.discuss_speaker_text);
            viewHolder.pub_time_text = (TextView) view.findViewById(R.id.discuss_time_text);
            viewHolder.content_text = (TextView) view.findViewById(R.id.discuss_content);
            view.setTag(viewHolder);
        }else{  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 到这里布局就已经建立好了，接下来是设置数据这些
        viewHolder.publisher_text.setText(discussion.publisher);
        viewHolder.pub_time_text.setText(discussion.transfer_time());
        viewHolder.content_text.setText(discussion.content.trim());   // 去除没必要的空字符

        int res_id = -1;

        if (!discussion.publisher.equals(MainActivity.cur_username)) {
            res_id = R.drawable.left_msg;
            params.gravity = Gravity.LEFT;
        }
        else {
            res_id = R.drawable.right_msg;
            params.gravity = Gravity.RIGHT;
        }

        view.setBackgroundResource(res_id);
        // 这个貌似在很多机器会导致程序闪退。。。注意
//        view.setLayoutParams(params);

        return view;    // view 里面的对象的属性是通过viewHolder修改的
    }


}
