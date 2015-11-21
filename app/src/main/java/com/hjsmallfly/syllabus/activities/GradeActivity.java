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
        for(int i = 0 ; i < grade_list.size() ; ++i)
            sb.append(grade_list.get(i).toString() + "\n");
        grade_debug_display.setText("");
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
