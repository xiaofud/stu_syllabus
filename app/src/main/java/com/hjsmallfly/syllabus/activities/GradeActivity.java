package com.hjsmallfly.syllabus.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.GradeGetter;
import com.hjsmallfly.syllabus.interfaces.GradeHandler;
import com.hjsmallfly.syllabus.syllabus.Grade;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.HashMap;
import java.util.List;

public class GradeActivity extends AppCompatActivity implements View.OnClickListener, GradeHandler{

    private EditText gpa_debug_view;
    private EditText grade_debug_display;
    private Button grade_query_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        find_views();
        setup_views();
    }

    private void find_views(){
        gpa_debug_view = (EditText) findViewById(R.id.gpa_debug);
        grade_debug_display = (EditText) findViewById(R.id.grade_debug);
        grade_query_button = (Button) findViewById(R.id.query_grade_button);
    }

    private void setup_views(){
        // listeners
        grade_query_button.setOnClickListener(this);
    }

    @Override
    public void handle_grade_list(List<Grade> grade_list) {

        if (grade_list == null)
            return;
//        Toast.makeText(GradeActivity.this, "handle_grade_list", Toast.LENGTH_SHORT).show();
        StringBuilder sb = new StringBuilder();
        double grade_point_sum = 0.0;
        double credit_sum = 0.0;
        for(int i = 0 ; i < grade_list.size() ; ++i) {
            sb.append(grade_list.get(i).toString() + "\n");

            // 计算gpa
            double grade_point = Double.parseDouble(grade_list.get(i).class_grade) - 50;
            double credit = Double.parseDouble(grade_list.get(i).class_credit);
            if (grade_point < 60 - 50)
                // 不及格的话
                grade_point = 0;
            grade_point_sum += grade_point / 10 * credit;
            credit_sum += credit;
        }

        double gpa = grade_point_sum / credit_sum;

        gpa_debug_view.setText(gpa + "");

//        grade_debug_display.setText("");

        grade_debug_display.setText(sb.toString());

    }

    private void get_grades(){
        HashMap<String, String> post_data = new HashMap<>();
        post_data.put("username", MainActivity.cur_username);
        post_data.put("password", MainActivity.cur_password);
        GradeGetter getter = new GradeGetter(this, this);
        getter.execute(post_data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query_grade_button:
                get_grades();
            default:
                break;
        }
    }
}
