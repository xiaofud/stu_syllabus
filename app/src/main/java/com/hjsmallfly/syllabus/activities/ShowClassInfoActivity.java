package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hjsmallfly.syllabus.helpers.HttpCommunication;
import com.hjsmallfly.syllabus.helpers.WebApi;
import com.hjsmallfly.syllabus.interfaces.LessonHandler;
import com.hjsmallfly.syllabus.parsers.StudentParser;
import com.hjsmallfly.syllabus.syllabus.ClassInfo;
import com.hjsmallfly.syllabus.syllabus.Lesson;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.StudentInfo;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowClassInfoActivity extends AppCompatActivity {

    Lesson classInfo;
    TextView classNameTextView;
    TextView teacherNameTextView;
    TextView beginTimeTextView;
    TextView classRoomTextView;
    TextView classNoTextView;

    List<StudentInfo> studentInfoList;

    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_class_info);

        classInfo = SyllabusActivity.clicked_lesson;
        initView();

    }

    public void initView() {
        button = (Button) findViewById(R.id.show_student);

        classNameTextView = (TextView) findViewById(R.id.classNameTextView);
        classNameTextView.setText(classInfo.name);

        teacherNameTextView = (TextView) findViewById(R.id.teacherNameTextView);
        teacherNameTextView.setText(classInfo.teacher);

        classNoTextView = (TextView) findViewById(R.id.classNoTextView);
        classNoTextView.setText("开课班号: " + classInfo.id);

        beginTimeTextView = (TextView) findViewById(R.id.beginTimeTextView);
        beginTimeTextView.setText(classInfo.duration);

        classRoomTextView = (TextView) findViewById(R.id.classRoomTextView);
        classRoomTextView.setText(classInfo.room);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentsPullTask pullTask = new StudentsPullTask(WebApi.get_server_address()
                        + getString(R.string.students_api) + "?class_id=" + classInfo.id);
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

    }


    class StudentsPullTask extends AsyncTask<HashMap<String, String>, Void, String> {

        //        private LessonHandler lessonHandler;
        private String request_url;

        public StudentsPullTask(String url) {
            Log.d(MainActivity.TAG, "pull task address is " + url);
            this.request_url = url;
//            this.lessonHandler = lessonHandler;
        }

        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            Log.d(MainActivity.TAG, "开始获取课表信息");
            return HttpCommunication.perform_get_call(request_url, 4000);
        }

        @Override
        protected void onPostExecute(String raw_data) {
//            if (HttpCommunication.is_internet_flow_used_up()){
//                raw_data = "";
//            }
            try {
                Log.d("parser_error", "hahaha"+raw_data);
                studentInfoList = StudentParser.parser(raw_data);
            } catch (JSONException e) {
                studentInfoList = new ArrayList<>();
                Log.d("parser_error", e.getMessage());
            }

            Intent intent = new Intent(ShowClassInfoActivity.this, ShowStudentInfoListActivity.class);
            intent.putExtra("studentInfoList", (Serializable) studentInfoList);
            startActivity(intent);
        }

    }


}
