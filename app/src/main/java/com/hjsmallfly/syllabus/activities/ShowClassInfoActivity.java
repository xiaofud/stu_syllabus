package com.hjsmallfly.syllabus.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ShowClassInfoActivity extends AppCompatActivity {

    Lesson classInfo;
    TextView classNameTextView;
    TextView teacherNameTextView;
    TextView beginTimeTextView;
    TextView classRoomTextView;
    TextView classNoTextView;

    CardView class_name_card;

    List<StudentInfo> studentInfoList;

    TextView buttonTextView;
    CardView show_student_card;

    boolean isCheckStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
        }

        setContentView(R.layout.activity_show_class_info);

        getSupportActionBar().hide();
        classInfo = SyllabusActivity.clicked_lesson;
        initView();

    }

    public void initView() {
        buttonTextView = (TextView) findViewById(R.id.show_student);
        show_student_card = (CardView) findViewById(R.id.show_student_card);

        classNameTextView = (TextView) findViewById(R.id.classNameTextView);
        classNameTextView.setText(classInfo.name);

        teacherNameTextView = (TextView) findViewById(R.id.teacherNameTextView);
        teacherNameTextView.setText("教师: " + classInfo.teacher);

        classNoTextView = (TextView) findViewById(R.id.classNoTextView);
        classNoTextView.setText("开课班号: " + classInfo.id);


        String classTime = "上课时间: "+classInfo.duration + "周";

        String[] weeks = {
                "周一", "周二", "周三", "周四", "周五", "周六", "周日",
        };

        ArrayList<String> keyList = new ArrayList<>();
        for (String key : classInfo.days.keySet()) {
            keyList.add(key);
        }

        Collections.sort(keyList, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.charAt(1) - t1.charAt(1);
            }
        });

        for (String key : keyList) {
            if (!key.isEmpty()) {
                Log.d("showClass", key.charAt(1) + "");
                int index;
                if (key.charAt(1) == '0')
                    index = 6;  // 周日
                else
                    index = key.charAt(1) - '1';
                classTime += " , " + weeks[index];

            }
            if (key.length() > 2) {
                classTime += key.substring(2);
            }
            classTime += classInfo.days.get(key);
        }

        beginTimeTextView = (TextView) findViewById(R.id.beginTimeTextView);
        beginTimeTextView.setText(classTime);


        classRoomTextView = (TextView) findViewById(R.id.classRoomTextView);
        classRoomTextView.setText("教室: " + classInfo.room);

        class_name_card = (CardView) findViewById(R.id.class_name_card);
        class_name_card.setCardBackgroundColor(classInfo.colorID);

        show_student_card.setCardBackgroundColor(classInfo.colorID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            class_name_card.setCardElevation(getResources().getDimension(R.dimen.card_ele));
        }
        isCheckStudent = false;

        buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCheckStudent) {
                    StudentsPullTask pullTask = new StudentsPullTask(WebApi.get_server_address()
                            + getString(R.string.students_api) + "?class_id=" + classInfo.id);
                    pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    isCheckStudent = true;
                }

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
                Log.d("parser_error", "hahaha" + raw_data);
                studentInfoList = StudentParser.parser(raw_data);
                Intent intent = new Intent(ShowClassInfoActivity.this, ShowStudentInfoListActivity.class);
                intent.putExtra("studentInfoList", (Serializable) studentInfoList);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                            ShowClassInfoActivity.this,
                            buttonTextView,
                            "show_students_card").toBundle());
                } else {
                    startActivity(intent);
                }

            } catch (JSONException e) {
                studentInfoList = new ArrayList<>();
                Toast.makeText(ShowClassInfoActivity.this, "查询失败,请检查网络连接", Toast.LENGTH_SHORT).show();
                Log.d("parser_error", e.getMessage());
            }

            isCheckStudent = false;

        }

    }


}
