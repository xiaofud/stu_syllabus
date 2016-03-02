package com.hjsmallfly.syllabus.parsers;

import android.content.Context;
import android.util.Log;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.syllabus.Homework;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by STU_nwad on 2015/10/10.
 * 用于从JSON数据解析出Homework对象
 */
public class HomeworkParser {

    public static final String ERROR_STRING = "ERROR";

    private Context context;

    public HomeworkParser(Context context){
        this.context = context;
    }

//    {
//        "count": 2,
//            "homework": [
//        {
//            "content": "P326 1 2",
//                "hand_in_time": "Tuesday",
//                "pub_time": 66666,
//                "publisher": "14xfdeng"
//        },
//        {
//            "content": "P326 1 2",
//                "hand_in_time": "Tuesday",
//                "pub_time": 66666,
//                "publisher": "14xfdeng"
//        }
//        ]
//    }

    /**
     * 从JSON数据中解析出 Homework 对象
     * @param json_data
     * @return 出现错误则返回 null, 记得检查这一点
     */
    public ArrayList<Homework> parser_json(String json_data){
        JSONTokener json_parser = new JSONTokener(json_data);
        try {
            // 第一次调用这个的时候其实就是将最外围的 {} 看成了一个JSON对象
            JSONObject json_obj = (JSONObject) json_parser.nextValue();
            if (json_obj.has(ERROR_STRING)){
                String err = json_obj.getString(ERROR_STRING);
//                Toast.makeText(context, "解析Homework类出错: " + err, Toast.LENGTH_SHORT).show();
                Log.d(MainActivity.TAG, err);
                return null;
            }

            // 开始解析
            int count = json_obj.getInt("count");   // 总数量
            JSONArray all_homework_json = json_obj.getJSONArray("homework");
            ArrayList<Homework> all_homework = new ArrayList<>();

            for(int i = 0 ; i < all_homework_json.length() ; ++i){
                // 得到每一项具体的HOMEWORK
                JSONObject homework_obj = (JSONObject) all_homework_json.get(i);
                // 然后就是获取相应的属性
                Homework homework = new Homework();
                homework.publisher = homework_obj.getString("publisher");
                homework.content = homework_obj.getString("content");
                homework.pub_time = homework_obj.getInt("pub_time");
                homework.hand_in_time = homework_obj.getString("hand_in_time");
                homework.id = homework_obj.getInt("id");
                if (!homework_obj.isNull("publisher_nickname")){
                    homework.nickname = homework_obj.getString("publisher_nickname");
                }
//                Log.d(MainActivity.TAG, homework.toString());
                all_homework.add(homework);
            }
            Log.d(MainActivity.TAG, "The length of homework is " + all_homework.size());
//            Toast.makeText(context, "HOMEWORK数据解析成功", Toast.LENGTH_SHORT).show();
            return all_homework;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG, e.toString());
            return null;
        }
    }

}
