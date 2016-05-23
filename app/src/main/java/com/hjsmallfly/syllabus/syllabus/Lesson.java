package com.hjsmallfly.syllabus.syllabus;

import android.content.Context;
import android.util.Log;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/9/23.
 *
 */
public class Lesson {

    // 课程是否单双周限制
    public static final int ALL_WEEK = 0;
    public static final int ODD_WEEK = 1;
    public static final int EVEN_WEEK = 2;

    public String name;
    public String id;
    public String teacher;
    public String room;
    public String duration;
    public String credit;
    public HashMap<String, String> days;
    public int colorID;

    // 额外的信息
    public int start_year;
    public int end_year;
    public int semester;

    // 颜色编号
    public int color_code;

    // 记录课程是否分为单双周
    public String comment;

//    self.class_id = str(start_year) + "_" + str(end_year) + "_" + str(semester) + "_" + self.class_number

    public String generate_class_id(){
        return start_year + "_" + end_year + "_" + semester + "_" + id;
    }

    // weekday 7 是周日, week_parameter 是否单双周
    public static Lesson makeLesson(String name, String teacher, String room, int week_parameter, int weekday, int start_week, int end_week, int start_time, int end_time, int credit, String class_id){
        Lesson lesson = new Lesson();
        lesson.id = class_id;
        lesson.name = name;
        lesson.teacher = teacher;
        lesson.room = room;
        lesson.credit = "" + credit;
        lesson.duration = start_week + "-" + end_week;
        lesson.days = new HashMap<>();
        // 这样也把 7 转换成 0 了
        weekday = weekday % 7;

        String class_time;
        switch (week_parameter){
            case ODD_WEEK:
                class_time = "单";
                break;
            case EVEN_WEEK:
                class_time = "双";
                break;
            default:
                class_time = "";
        }
        // 构建节数信息
        for(int i = start_time ; i <= end_time ; ++i){
            if (i == 10)    // 记得转化这种情况
                class_time += "0";
            else if (i == 11)
                class_time += "A";
            else if (i == 12)
                class_time += "B";
            else if (i == 13)
                class_time += "C";
            else
                class_time += i;
        }

        lesson.days.put("w" + weekday, class_time);

        return lesson;
    }

    public boolean addToSyllabus(Context context){
        String original_json = MainActivity.syllabus_json_data;
        if (original_json != null && !original_json.isEmpty()){

            try {
                JSONObject syllabusObj = new JSONObject(original_json);
                JSONArray classes = syllabusObj.getJSONArray("classes");
                JSONObject new_class = toJson();
                if (new_class == null)
                    return false;
                classes.put(new_class);
                syllabusObj.put("classes", classes);
                Log.d("addToSyllabus", syllabusObj.toString());
                String filename = StringDataHelper.generate_syllabus_file_name(MainActivity.cur_username, MainActivity.cur_year_string, MainActivity.cur_semester, "_");
                return FileOperation.save_to_file(context, filename, syllabusObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
//        {"id": "80930",
// "teacher": "张杰/林艺文(实验)",
// "credit": "4.0",
// "name": "[CST2103A]计算机组织与体系结构I",
// "days": {"w4": "AB", "w0": "None", "w2": "89", "w6": "None", "w3": "None", "w5": "None", "w1": "None"},
// "duration": "1 -16",
// "room": "E401"}

        try {
            jsonObject.put("id", id);
            jsonObject.put("teacher", teacher);
            jsonObject.put("credit", credit);
            jsonObject.put("name", name);

            JSONObject days = new JSONObject();
            for(String key: this.days.keySet())
                days.put(key, this.days.get(key));

            jsonObject.put("days", days);
            jsonObject.put("duration", duration);
            jsonObject.put("room", room);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;

    }

    @Override
    public String toString(){
        // 去掉课程的[课程号]
        String name_without_prefix = name;
        int s_index = name.indexOf(']');
        if (s_index != -1){
            name_without_prefix = name.substring(s_index + 1);
        }
        // 考虑单双周的情况
        if (comment != null)
            name_without_prefix = "[" + comment + "]" + name_without_prefix;

        return name_without_prefix + "@" + room + "" /* + days.toString() */;
    }


    public int[] get_duration(){
        String[] week_strs = duration.split("-");
        int[] range = new int[2];
        for(int i = 0 ; i < 2 ; ++i){
            range[i] = Integer.parseInt(week_strs[i].trim());
        }
        return range;
    }
}
