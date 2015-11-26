package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.hjsmallfly.syllabus.adapters.DiscussionAdapter;
import com.hjsmallfly.syllabus.helpers.ClipBoardHelper;
import com.hjsmallfly.syllabus.helpers.DeleteTask;
import com.hjsmallfly.syllabus.helpers.InfoPullTask;
import com.hjsmallfly.syllabus.helpers.InsertTask;
import com.hjsmallfly.syllabus.helpers.JSONHelper;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.interfaces.AfterDeleteHandler;
import com.hjsmallfly.syllabus.interfaces.DiscussionHandler;
import com.hjsmallfly.syllabus.interfaces.InsertHandler;
import com.hjsmallfly.syllabus.syllabus.Discussion;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalDiscussActivity extends AppCompatActivity implements DiscussionHandler, InsertHandler, View.OnClickListener, AfterDeleteHandler {

    private ListView global_list_view;
    private EditText global_discuss_edit;
    private Button global_discuss_button;

    private List<Discussion> global_discussions;
    private DiscussionAdapter discussionAdapter;

    private int display_count = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_discuss);

        find_views();
        setup_views();

        // 拉取数据
        get_latest_global_discussions();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_global_discussion, menu);
        return true;
    }




    private void find_views(){
        global_list_view = (ListView) findViewById(R.id.global_discuss_list_view);
        global_discuss_edit = (EditText) findViewById(R.id.global_discuss_edit);
        global_discuss_button = (Button) findViewById(R.id.global_discuss_button);
    }

    private void setup_views(){
        global_discuss_button.setOnClickListener(this);

        registerForContextMenu(global_list_view);
    }

    private void get_latest_global_discussions(){
        InfoPullTask global_discussion_getter = new InfoPullTask(this, InfoPullTask.PULL_DISCUSSION);
        global_discussion_getter.setDiscussionHandler(this);
        global_discussion_getter.get_information(display_count, "0", 0, 0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update_global_discussion_action:
                get_latest_global_discussions();
                return true;

            case R.id.personal_info_action:
                Intent person_intent = new Intent(this, PersonalInfoActivity.class);
                startActivity(person_intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void submit_global_discussion(){
        String content = global_discuss_edit.getText().toString();
        if (content.trim().isEmpty()){
            Toast.makeText(GlobalDiscussActivity.this, "不能发送空吐槽!", Toast.LENGTH_SHORT).show();
            return;
        }

        // curl http://127.0.0.1:5000/api/v1.0/discuss -X POST -d "publisher=14xfdeng&pub_time=1000&content=How are you?&number=100000&start_year=2015&end_year=2016&semester=1&token=675054"

        HashMap<String, String> post_data = new HashMap<>();
        MainActivity.set_local_token(this);
        String token = MainActivity.token;
        String publisher = MainActivity.cur_username;
        String pub_time = System.currentTimeMillis() * 1000 + "";
        String number = "0";
        String start_year = "0";
        String end_year = "0";
        String semester = "0";

        post_data.put("publisher", publisher);
        post_data.put("pub_time", pub_time);
        post_data.put("content", content);
        post_data.put("number", number);
        post_data.put("start_year", start_year);
        post_data.put("end_year", end_year);
        post_data.put("token", token);
        post_data.put("semester", semester);

        InsertTask insert_discussion_task = new InsertTask(this, this);
        insert_discussion_task.execute(post_data);


    }

    @Override
    public void deal_with_discussion(ArrayList<Discussion> all_discussions) {
        if (all_discussions == null) {
            // 发生了某些错误
//            Toast.makeText(GlobalDiscussActivity.this, "没有任何吹水数据呢", Toast.LENGTH_SHORT).show();
            return;
        }

        if (all_discussions.size() == 0){
            Toast.makeText(GlobalDiscussActivity.this, "还没有吹水数据呢", Toast.LENGTH_SHORT).show();
            return;
        }

        if (global_discussions == null){
            global_discussions = new ArrayList<>(all_discussions);
            discussionAdapter = new DiscussionAdapter(this, R.layout.discuss_item_layout, global_discussions);
            global_list_view.setAdapter(discussionAdapter);
        }else{
            global_discussions.clear();
            global_discussions.addAll(all_discussions);
            discussionAdapter.notifyDataSetChanged();
        }

        // 保证最后一个能看到
        if (global_discussions.size() > 0)
            global_list_view.setSelection(global_discussions.size() - 1);
    }

    @Override
    public void deal_with_insert_result(String ret_val) {
        String error = JSONHelper.check_and_get_error(ret_val);
        if (error != null){
            if (error.equals("wrong token")){
                Toast.makeText(GlobalDiscussActivity.this, StringDataHelper.ERROR_TOKEN, Toast.LENGTH_SHORT).show();
                return;
            }else{
//                Toast.makeText(GlobalDiscussActivity.this, "出现错误: " + error, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(GlobalDiscussActivity.this, "吐槽成功", Toast.LENGTH_SHORT).show();
        global_discuss_edit.setText("");
        get_latest_global_discussions();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("请选择一个操作");
        menu.add(0, v.getId(), 0, "复制");
        menu.add(0, v.getId(), 0, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //  info.position will give the index of selected item
        if (item.getTitle().equals("复制")){
            int index = info.position; // 被点击的项的所在位置
            Discussion discussion = (Discussion) global_list_view.getItemAtPosition(index);
            ClipBoardHelper.setContent(this, discussion.content);
            Toast.makeText(this, "成功复制到剪贴板", Toast.LENGTH_SHORT).show();
            return true;
        }else if (item.getTitle().equals("删除")){
            // 删除信息
            int index = info.position;
            Discussion discussion = (Discussion) global_list_view.getItemAtPosition(index);
            HashMap<String, String> delete_data = new HashMap<>();
            delete_data.put("resource_id", discussion.id + "");
            delete_data.put("token", MainActivity.token);
            delete_data.put("user", MainActivity.cur_username);

            DeleteTask task = new DeleteTask(this, this, DeleteTask.DELETE_DISCUSSION, index);
            task.execute(delete_data);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.global_discuss_button:
                submit_global_discussion();
                break;
            default:
                break;
        }
    }

    @Override
    public void deal_with_delete(String response, int position) {
        String error = JSONHelper.check_and_get_error(response);
        if (error != null){
//            Toast.makeText(MyTabActivity.this, "删除错误: " + error, Toast.LENGTH_SHORT).show();
            if (error.equals(DeleteTask.ERROR_WRONG_TOKEN))
                Toast.makeText(this, StringDataHelper.ERROR_TOKEN, Toast.LENGTH_SHORT).show();
            else if (error.equals(DeleteTask.ERROR_NO_AUTHORIZED)){
                Toast.makeText(this, "只能删除自己的信息哟", Toast.LENGTH_SHORT).show();
            }
            return;

        }
        // 代表删除成功
        Discussion discussion = (Discussion) global_list_view.getItemAtPosition(position);
        discussionAdapter.remove(discussion);
        discussionAdapter.notifyDataSetChanged();
        Toast.makeText(this, "成功删除消息" /*+ discussion.content*/, Toast.LENGTH_SHORT).show();
    }
}
