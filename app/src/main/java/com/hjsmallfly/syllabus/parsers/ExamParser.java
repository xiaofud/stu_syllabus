package com.hjsmallfly.syllabus.parsers;

import android.content.Context;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.syllabus.Exam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smallfly on 15-11-23.
 */
public class ExamParser {

    /**
     * EXAMS": [
     {
     "exam_class": "[CST1501A]\u8ba1\u7b97\u79d1\u5b66\u5bfc\u8bba[CST9106]",
     "exam_class_number": "12349",
     "exam_comment": "",
     "exam_invigilator": "\u5f20\u51ef\u4e1c",
     "exam_location": "\u8bb2\u5802\u56db",
     "exam_main_teacher": "\u8521\u73b2\u5982",
     "exam_stu_numbers": "69",
     "exam_stu_position": "61",
     "exam_time": "\u7b2c17\u5468\u661f\u671f\u4e00\u7b2c2\u573a(2015.01.12  10:30-12:30)"
     },
     */

    private Context context;

    public ExamParser(Context context){
        this.context = context;
    }

    public List<Exam> parse_exam_list(String raw_data){
        if (raw_data.isEmpty())
            return null;
        JSONTokener jsonTokener = new JSONTokener(raw_data);
        try {
            JSONObject json_object = (JSONObject) jsonTokener.nextValue();
            JSONArray exam_array = json_object.getJSONArray("EXAMS");
            List<Exam> exam_list = new ArrayList<>();
            for(int i = 0 ; i < exam_array.length() ; ++i){
                JSONObject exam_obj = (JSONObject) exam_array.get(i);
                
                Exam exam = new Exam();
                exam.exam_class = exam_obj.getString("exam_class");
                exam.exam_class_number = exam_obj.getString("exam_class_number");
                exam.exam_comment = exam_obj.getString("exam_comment");
                exam.exam_invigilator = exam_obj.getString("exam_invigilator");
                exam.exam_location = exam_obj.getString("exam_location");
                exam.exam_main_teacher = exam_obj.getString("exam_main_teacher");
                exam.exam_stu_numbers = exam_obj.getString("exam_stu_numbers");
                exam.exam_stu_position = exam_obj.getString("exam_stu_position");
                exam.exam_time = exam_obj.getString("exam_time");

                exam_list.add(exam);
            }

            // 保存文件
            FileOperation.save_to_file(context, StringDataHelper.generate_exam_file(MainActivity.cur_username, MainActivity.cur_year_string, MainActivity.cur_semester)
            , raw_data);
            return exam_list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
