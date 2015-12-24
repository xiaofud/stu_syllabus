package com.hjsmallfly.syllabus.helpers;

import android.app.Activity;

import com.hjsmallfly.syllabus.syllabus.ClassInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by daidaijie on 2015/12/10.
 */
public class classInfoUtil {


    private Activity activity;

    private String jsonString;

    public classInfoUtil(Activity activity) {
        this.activity = activity;
    }


    public String classListToJson(List<ClassInfo> classInfoList) throws JSONException {
        JSONArray classArray = new JSONArray();

        for (int i = 0; i < classInfoList.size(); i++) {

            ClassInfo classInfo = classInfoList.get(i);
            //System.out.println(classInfo.getClassName());
            JSONObject classJson = new JSONObject();

            classJson.put("classRoom", classInfo.getClassRoom());
            classJson.put("teacherName", classInfo.getTeacherName());
            classJson.put("semester", classInfo.getSemester());
            classJson.put("beginTime", classInfo.getBeginTime());
            classJson.put("classNo", classInfo.getClassNo());
            classJson.put("className", classInfo.getClassName());


            JSONArray studentArray = new JSONArray();


            classArray.put(classJson);
        }
        //Log.v("jsonString",classArray.toString());
        return classArray.toString();
    }

    //json to ClassList
    public List<ClassInfo> getClassList() throws JSONException {
        List<ClassInfo> classInfoList = new ArrayList<>();
        //String json = getData().toString();
        String json = getJsonString();


        JSONArray classArray = new JSONArray(json);
        for (int i = 0; i < classArray.length(); i++) {
            JSONObject classJson = classArray.getJSONObject(i);
            String classRoom = classJson.getString("classRoom");
            String teacherName = classJson.getString("teacherName");
            String semester = classJson.getString("semester");
            String beginTime = classJson.getString("beginTime");
            String classNo = classJson.getString("classNo");
            String className = classJson.getString("className");


            ClassInfo classInfo = new ClassInfo();
            classInfo.setClassName(className);
            classInfo.setClassRoom(classRoom);
            classInfo.setTeacherName(teacherName);
            classInfo.setSemester(semester);
            classInfo.setBeginTime(beginTime);
            classInfo.setClassNo(classNo);


            classInfoList.add(classInfo);

        }
        return classInfoList;

    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }



}
