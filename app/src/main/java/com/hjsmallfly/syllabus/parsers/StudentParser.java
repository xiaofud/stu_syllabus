package com.hjsmallfly.syllabus.parsers;

import android.util.Log;

import com.hjsmallfly.syllabus.syllabus.StudentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daidaijie on 2016/3/22.
 */
public class StudentParser {

    public static List<StudentInfo> parser(String json) throws JSONException {

        List<StudentInfo> studentList = new ArrayList<>();

        JSONObject root = new JSONObject(json);
        JSONObject classInfo = root.getJSONObject("class_info");
        String studentJsonString = classInfo.getString("student");
        JSONArray studentArray = new JSONArray(studentJsonString);


        for (int j = 0; j < studentArray.length(); j++) {
            JSONObject studentJson = studentArray.getJSONObject(j);
            String major = studentJson.getString("major");
            String name = studentJson.getString("name");
            String number = studentJson.getString("number");
            String gender = studentJson.getString("gender");

            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setMajor(major);
            studentInfo.setName(name);
            studentInfo.setNumber(number);
            studentInfo.setGender(gender);
            studentList.add(studentInfo);
        }

        return studentList;
    }
}
