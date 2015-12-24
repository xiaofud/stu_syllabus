package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hjsmallfly.syllabus.syllabus.ClassInfo;
import com.hjsmallfly.syllabus.syllabus.R;

import java.io.Serializable;
import java.util.List;

public class ShowClassInfoActivity extends AppCompatActivity {

    ClassInfo classInfo;
    TextView classNameTextView;
    TextView teacherNameTextView;
    TextView beginTimeTextView;
    TextView classRoomTextView;
    TextView classNoTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_class_info);

        classInfo = getClassInfo();
        initView();

    }

    public void initView() {
        classNameTextView = (TextView) findViewById(R.id.classNameTextView);
        classNameTextView.setText(classInfo.getClassName());

        teacherNameTextView = (TextView) findViewById(R.id.teacherNameTextView);
        teacherNameTextView.setText(classInfo.getTeacherName());

        classNoTextView = (TextView) findViewById(R.id.classNoTextView);
        classNoTextView.setText("开课班号: "+classInfo.getClassNo());

        beginTimeTextView = (TextView) findViewById(R.id.beginTimeTextView);
        beginTimeTextView.setText(classInfo.getBeginTime());

        classRoomTextView = (TextView) findViewById(R.id.classRoomTextView);
        classRoomTextView.setText(classInfo.getClassRoom());

    }


    public ClassInfo getClassInfo() {
        Intent intent = getIntent();

        ClassInfo classInfo = (ClassInfo) intent.getSerializableExtra("classInfo");

        return classInfo;
    }




    public void back(View view) {
        finish();
    }
}
