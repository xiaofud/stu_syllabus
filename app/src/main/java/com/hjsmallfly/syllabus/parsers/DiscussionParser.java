package com.hjsmallfly.syllabus.parsers;

import android.content.Context;

import com.hjsmallfly.syllabus.syllabus.Discussion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by STU_nwad on 2015/10/11.
 * 从字符串中解析评论
 */
public class DiscussionParser {

    public static final String ERROR_STRING = "ERROR";

    private Context context;

    public DiscussionParser(Context context){
        this.context = context;
    }

    public ArrayList<Discussion> parse_json(String json_data){
        JSONTokener json_parser = new JSONTokener(json_data);
        try {
            JSONObject json_obj = (JSONObject) json_parser.nextValue();

            // 跳过count
            JSONArray discussion_json_array = json_obj.getJSONArray("discussions");
            ArrayList<Discussion> discussions = new ArrayList<>();
            for(int i = 0 ; i < discussion_json_array.length() ; ++i){
                JSONObject dis_obj = (JSONObject) discussion_json_array.get(i);
                String content = dis_obj.getString("content");
                String publisher = dis_obj.getString("publisher");
                long pub_time = dis_obj.getLong("time");
                int id = dis_obj.getInt("id");  // 在数据库中的主键值

                Discussion discussion = new Discussion();
                discussion.content = content;
                discussion.publisher = publisher;
                discussion.pub_time = pub_time;
                discussion.id = id;

                discussions.add(discussion);
            }
            return discussions;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }



}
