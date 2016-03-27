//package com.hjsmallfly.syllabus.activities;
//
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import com.hjsmallfly.syllabus.adapters.PostAdapter;
//import com.hjsmallfly.syllabus.adapters.HomeworkAdapter;
//import com.hjsmallfly.syllabus.syllabus.Discussion;
//import com.hjsmallfly.syllabus.interfaces.DiscussionHandler;
//import com.hjsmallfly.syllabus.syllabus.Homework;
//import com.hjsmallfly.syllabus.interfaces.HomeworkHandler;
//import com.hjsmallfly.syllabus.helpers.InfoPullTask;
//import com.hjsmallfly.syllabus.syllabus.Lesson;
//import com.hjsmallfly.syllabus.syllabus.R;
//import com.umeng.analytics.MobclickAgent;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
//public class HistoryActivity extends AppCompatActivity implements HomeworkHandler, DiscussionHandler, View.OnClickListener{
//
//    public static final String[] HISTORY_TYPES = {"Homework", "Discussion"};
//
//    public static final int MAX_COUNT = 500;
//
//    private ArrayAdapter<String> data_adapter;
//    private HomeworkAdapter homeworkAdapter;
//    private PostAdapter postAdapter;
//
//    private ArrayList<Homework> all_homework;
////    private ArrayList<Discussion> all_discussions;
//
//    private Spinner history_type_spinner;
//    private Button query_history_button;
////    private EditText history_content;
//    private EditText history_count_edit;
//    private ListView history_list_view;
//
//
//    private void find_views(){
//        history_type_spinner = (Spinner) findViewById(R.id.history_type_spinner);
//        query_history_button = (Button) findViewById(R.id.query_history_button);
////        history_content = (EditText) findViewById(R.id.history_content);
//        history_count_edit = (EditText) findViewById(R.id.history_count_edit);
//        history_list_view = (ListView) findViewById(R.id.history_list_view);
//    }
//
//    private void setup_views(){
//        // 设置下拉框
//        data_adapter =  new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
//                HISTORY_TYPES);
//        history_type_spinner.setAdapter(data_adapter);
//
//        // 读取额外信息
//        Intent intent = getIntent();
//        String type = intent.getStringExtra("type");
//        int index = -1;
//        for(int i = 0 ; i < HISTORY_TYPES.length ; ++i){
//            if (type.equals(HISTORY_TYPES[i])){
//                index = i;
//                break;
//            }
//        }
//        if (index != -1)
//            history_type_spinner.setSelection(index);
//
//        // 添加监听器
//        query_history_button.setOnClickListener(this);
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//        find_views();
//        setup_views();
//    }
//
//    // 友盟的统计功能
//    @Override
//    protected void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(this);
//    }
//
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPause(this);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_history, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
////        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void query_history(){
//
//        if (history_count_edit.getText().toString().isEmpty()) {
//            Toast.makeText(HistoryActivity.this, "少年，要输入查询的数目吖~~~~", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        int count = Integer.parseInt(history_count_edit.getText().toString());
//
//        if (count > MAX_COUNT){
//            Toast.makeText(HistoryActivity.this, "数字不能大于" + MAX_COUNT + "哟~~~", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Homework
//        if (history_type_spinner.getSelectedItem().toString().equals(HISTORY_TYPES[0])){
//            InfoPullTask task = new InfoPullTask(this, InfoPullTask.PULL_HOMEWORK);
//            task.setHomeworkHandler(this);
//            Lesson lesson = MyTabActivity.lesson;
//            task.get_information(count, lesson.id, lesson.start_year, lesson.end_year, lesson.semester);
//        }
//
//        // Discussions
//        if (history_type_spinner.getSelectedItem().toString().equals(HISTORY_TYPES[1])){
//            InfoPullTask task = new InfoPullTask(this, InfoPullTask.PULL_DISCUSSION);
//            task.setDiscussionHandler(this);
//            Lesson lesson = MyTabActivity.lesson;
//            task.get_information(count, lesson.id, lesson.start_year, lesson.end_year, lesson.semester);
//        }
//    }
//
//    @Override
//    public void deal_with_homework(ArrayList<Homework> all_homework) {
//        if (all_homework == null){
////            history_content.setText("None");
//            Toast.makeText(HistoryActivity.this, "没有查询到作业的历史信息呢", Toast.LENGTH_SHORT).show();
//            return ;
//        }
//        if (this.all_homework == null){
//            this.all_homework = new ArrayList<>(all_homework);
//        }else{
//            this.all_homework.clear();
//            this.all_homework.addAll(all_homework);
//        }
//
//        // 减少内存的使用
//        if (homeworkAdapter == null) {
//            homeworkAdapter = new HomeworkAdapter(this, R.layout.homework_list_item, all_homework);
//            history_list_view.setAdapter(homeworkAdapter);
//        }else{
//            homeworkAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.query_history_button:
//                query_history();
//                break;
//        }
//    }
//
//    @Override
//    public void deal_with_discussion(ArrayList<Discussion> all_discussions) {
//        if (all_discussions == null){
//            Toast.makeText(HistoryActivity.this, "没有查找到吐槽记录呢~~~", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // 这个界面顺序和之前的界面顺序相反
//        Collections.reverse(all_discussions);
//
//        postAdapter = new PostAdapter(this, R.layout.discuss_item_layout, all_discussions);
//        history_list_view.setAdapter(postAdapter);
//
//    }
//}
