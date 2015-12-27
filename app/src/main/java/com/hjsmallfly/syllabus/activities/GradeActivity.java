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
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.GradeGetter;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.interfaces.GradeHandler;
import com.hjsmallfly.syllabus.parsers.GradeParser;
import com.hjsmallfly.syllabus.syllabus.Grade;
import com.hjsmallfly.syllabus.syllabus.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GradeActivity extends AppCompatActivity implements View.OnClickListener, GradeHandler{

    private TextView gpa_display_view;
    private Button grade_query_button;
    private ListView grade_list_view;
    private List<Grade> grade_list; // 存放成绩信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        find_views();
        setup_views();

        // 先看看有没有本地缓存好的成绩信息
        if (FileOperation.hasFile(this, MainActivity.cur_username + getString(R.string.grade_file))){
            parse_local_grade_data();
        }else
        // 从服务器获取信息
            get_grades();
    }

    // 友盟的统计功能
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void find_views(){
        gpa_display_view = (TextView) findViewById(R.id.gpa_display_text_view);
        grade_query_button = (Button) findViewById(R.id.query_grade_button);
        grade_list_view = (ListView) findViewById(R.id.grade_list_view);
    }

    private void setup_views(){
        // listeners
        grade_query_button.setOnClickListener(this);

    }

    @Override
    public void handle_grade_list(List<Grade> grade_list) {

        if (grade_list == null)
            return;



        String gpa_string = "GPA: " + Grade.GPA;
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


    // 读取本地缓存文件
    private String read_grade_data(){
        return FileOperation.read_from_file(this, MainActivity.cur_username + getString(R.string.grade_file));
    }

    // 解析本地文件
    private void parse_local_grade_data(){
        GradeParser parser = new GradeParser(this);
        handle_grade_list(parser.parse(read_grade_data()));
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
