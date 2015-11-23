package com.hjsmallfly.syllabus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.hjsmallfly.syllabus.adapters.ExamAdapter;
import com.hjsmallfly.syllabus.helpers.ExamGetter;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.interfaces.ExamHandler;
import com.hjsmallfly.syllabus.parsers.ExamParser;
import com.hjsmallfly.syllabus.syllabus.Exam;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener, ExamHandler {


    private TextView exam_info_text_view;
    private ListView exam_list_view;
    private Button query_exam_button;

    private List<Exam> exam_list;
    private ExamAdapter examAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);


        find_views();
        setup_views();

        if (FileOperation.hasFile(this, StringDataHelper.generate_exam_file(MainActivity.cur_username, MainActivity.cur_year_string, MainActivity.cur_semester))){
            load_cached_exam_file();
        }else
            get_exam_list();

    }

    private void find_views(){
        exam_info_text_view = (TextView) findViewById(R.id.exam_info_view);
        exam_list_view = (ListView) findViewById(R.id.exam_list_view);
        query_exam_button = (Button) findViewById(R.id.update_exam_button);
    }

    private void setup_views(){
        query_exam_button.setOnClickListener(this);
    }

    private void get_exam_list(){
        String username = MainActivity.cur_username;
        String password = MainActivity.cur_password;
        String years = MainActivity.cur_year_string;
        String semester = MainActivity.cur_semester + "";

        HashMap<String, String> post_data = new HashMap<>();
        post_data.put("username", username);
        post_data.put("password", password);
        post_data.put("years", years);
        post_data.put("semester", semester);

        ExamGetter examGetter = new ExamGetter(this, this);
        examGetter.execute(post_data);
    }

    @Override
    public void deal_with_exam_list(List<Exam> exam_list) {
        if (exam_list == null){
//            Toast.makeText(ExamActivity.this, "exam_list 为 null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.exam_list == null){
            this.exam_list  = new ArrayList<>(exam_list);
            this.examAdapter = new ExamAdapter(this, R.layout.exam_list_item, this.exam_list);
            exam_list_view.setAdapter(examAdapter);
        }else{
            this.exam_list.clear();
            this.exam_list.addAll(exam_list);
            examAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.update_exam_button){
            get_exam_list();
        }
    }

//    private void save_exam_file(String data){
//        String filename = StringDataHelper.generate_exam_file(MainActivity.cur_username, MainActivity.cur_year_string, MainActivity.cur_semester);
//        FileOperation.save_to_file(this, filename, data);
//    }

    private void load_cached_exam_file(){
        String filename = StringDataHelper.generate_exam_file(MainActivity.cur_username, MainActivity.cur_year_string, MainActivity.cur_semester);
        String raw_data = FileOperation.read_from_file(this, filename);

        if (raw_data != null){
            ExamParser parser = new ExamParser(this);
            this.deal_with_exam_list(parser.parse_exam_list(raw_data));
        }

    }
}
