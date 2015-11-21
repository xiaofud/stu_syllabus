package com.hjsmallfly.syllabus.parsers;

import android.content.Context;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.JSONHelper;
import com.hjsmallfly.syllabus.interfaces.GradeHandler;
import com.hjsmallfly.syllabus.syllabus.Grade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smallfly on 15-11-21.
 * 解析成绩信息
 */
public class GradeParser {

    private Context context;

    public GradeParser(Context context){
        this.context = context;
    }

    public List<Grade> parse(String raw_data){
        if (raw_data.isEmpty())
            return null;
//        Toast.makeText(context, "parsing grade raw data", Toast.LENGTH_SHORT).show();
        JSONTokener json_parser = new JSONTokener(raw_data);
        try {
            // 先检测有无错误
            String error = JSONHelper.check_and_get_error(raw_data);
            if (error != null) {
                if (error.equals(GradeHandler.ERROR_NO_GRADE_INFO)){
                    Toast.makeText(context, "还没有任何成绩信息呢!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                return null;
            }

            JSONObject json_obj = (JSONObject) json_parser.nextValue();
            JSONArray all_grades = json_obj.getJSONArray("GRADES");
            List<Grade> grade_list = new ArrayList<>();
            for(int i = 0 ; i < all_grades.length() ; ++i){
                // 每个元素又是数组
                JSONArray grades_of_a_semester = (JSONArray) all_grades.get(i);
                for(int j = 0 ; j < grades_of_a_semester.length() ; ++j){
                    JSONObject grade_obj = (JSONObject) grades_of_a_semester.get(j);
                    /**
                     * class_credit": "4.0",
                     "class_grade": "86",
                     "class_name": "[ELC2]\u82f1\u8bed(ELC2)",
                     "class_number": "71609",
                     "class_teacher": "\u9648\u7433",
                     "semester": "\u79cb\u5b63\u5b66\u671f",
                     "years": "2014-2015"
                     */
                    String class_credit = grade_obj.getString("class_credit");
                    String class_name = grade_obj.getString("class_name");
                    String class_grade = grade_obj.getString("class_grade");
                    String class_number = grade_obj.getString("class_number");
                    String class_teacher = grade_obj.getString("class_teacher");
                    String semester = grade_obj.getString("semester");
                    String years = grade_obj.getString("years");

                    Grade grade = new Grade();
                    grade.class_credit = class_credit;
                    grade.class_name = class_name;
                    grade.class_grade = class_grade;
                    grade.class_number = class_number;
                    grade.class_teacher = class_teacher;
                    grade.semester = semester;
                    grade.years = years;

                    grade_list.add(grade);

                }
            }
            return grade_list;

        } catch (JSONException e) {
            e.printStackTrace();
//            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

}
