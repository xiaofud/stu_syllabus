package com.hjsmallfly.syllabus.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.interfaces.LessonHandler;

import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/10/23.
 */
public class LessonPullTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private LessonHandler lessonHandler;
    private String request_url;

    public LessonPullTask(String url, LessonHandler lessonHandler){
        Log.d(MainActivity.TAG, "pull task address is " + url);
        this.request_url = url;
        this.lessonHandler = lessonHandler;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        Log.d(MainActivity.TAG, "开始获取课表信息");
        return HttpCommunication.performPostCall(request_url, params[0]);
    }

    @Override
    protected void onPostExecute(String raw_data){
        lessonHandler.deal_with_lessons(raw_data);
    }

}
