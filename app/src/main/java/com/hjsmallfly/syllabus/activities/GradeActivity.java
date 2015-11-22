package com.hjsmallfly.syllabus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hjsmallfly.syllabus.adapters.GradeAdapter;
import com.hjsmallfly.syllabus.helpers.GradeGetter;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.interfaces.GradeHandler;
import com.hjsmallfly.syllabus.syllabus.Grade;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GradeActivity extends AppCompatActivity implements View.OnClickListener, GradeHandler{

    private TextView gpa_display_view;
//    private EditText grade_debug_display;
    private Button grade_query_button;

//    private Spinner year_spinner;
//    private Spinner semester_spinner;

    private ListView grade_list_view;

//    public static final String[] SEMESTERS = {"SPRING", "SUMMER", "AUTUMN"};

    private List<Grade> grade_list; // 存放成绩信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        find_views();
        setup_views();

        // 打开便查一下成绩
        get_grades();
    }

    private void find_views(){
        gpa_display_view = (TextView) findViewById(R.id.gpa_display_text_view);
//        grade_debug_display = (EditText) findViewById(R.id.grade_debug);
        grade_query_button = (Button) findViewById(R.id.query_grade_button);

//        year_spinner = (Spinner) findViewById(R.id.grade_year_spinner);
//        semester_spinner = (Spinner) findViewById(R.id.grade_semester_spinner);

        grade_list_view = (ListView) findViewById(R.id.grade_list_view);
    }

    private void setup_views(){
        // listeners
        grade_query_button.setOnClickListener(this);

        // year_spinner
//        String[] years = StringDataHelper.generate_years(4);
//        year_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, years));

        // semester_spinner
//        semester_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, SEMESTERS));

    }

    @Override
    public void handle_grade_list(List<Grade> grade_list) {

        if (grade_list == null)
            return;
//        Toast.makeText(GradeActivity.this, "handle_grade_list", Toast.LENGTH_SHORT).show();
//        StringBuilder sb = new StringBuilder();
        double grade_point_sum = 0.0;
        double credit_sum = 0.0;
        for(int i = 0 ; i < grade_list.size() ; ++i) {
//            sb.append(grade_list.get(i).toString() + "\n");

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

        String gpa_string = "GPA: " + gpa;
        gpa_display_view.setText(gpa_string);

//        grade_debug_display.setText("");

//        grade_debug_display.setText(sb.toString());

        if (this.grade_list == null){
            this.grade_list = new ArrayList<>(grade_list);
        }else{
            // 更新里面的数据就行了
            this.grade_list.clear();
            this.grade_list.addAll(grade_list);
        }
        // 在list view上显示出来
        grade_list_view.setAdapter(new GradeAdapter(this, R.layout.grade_list_item, this.grade_list));
    }

    /**
     * 获取课程成绩
     */
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
