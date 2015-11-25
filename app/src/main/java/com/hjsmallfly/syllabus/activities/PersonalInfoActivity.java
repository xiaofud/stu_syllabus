package com.hjsmallfly.syllabus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.JSONHelper;
import com.hjsmallfly.syllabus.helpers.PullUserTask;
import com.hjsmallfly.syllabus.helpers.UserInfoAlter;
import com.hjsmallfly.syllabus.interfaces.UserAlterHandler;
import com.hjsmallfly.syllabus.interfaces.UserHandler;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.UserInformation;

import java.util.HashMap;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener, UserHandler, UserAlterHandler {

    private EditText username_edit;
    private EditText nickname_edit;
    private Button update_nickname_button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        find_views();
        setup_views();

        get_user_info();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update_information_action){
            get_user_info();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void find_views(){
        username_edit = (EditText) findViewById(R.id.username_info_edit);
        nickname_edit = (EditText) findViewById(R.id.nickname_info_edit);
        update_nickname_button = (Button) findViewById(R.id.personal_info_update_button);

    }

    private void setup_views(){
        // add listener
        update_nickname_button.setOnClickListener(this);
    }

    private void alter_user_info(){
        String nickname = nickname_edit.getText().toString();
        if (nickname.trim().isEmpty()){
            Toast.makeText(PersonalInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else if(nickname.length() > 20){
            Toast.makeText(PersonalInfoActivity.this, "昵称不能超过20个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> post_data = new HashMap<>();
        post_data.put("username", MainActivity.cur_username);
        post_data.put("token", MainActivity.token);
        post_data.put("nickname", nickname.trim());

        UserInfoAlter alter = new UserInfoAlter(this, this);
        alter.execute(post_data);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.personal_info_update_button){
            // 提交修改
            alter_user_info();
        }
    }


    private void get_user_info(){
        PullUserTask task = new PullUserTask(this, this);
        HashMap<String, String> post_data = new HashMap<>();
        post_data.put("username", MainActivity.cur_username);
        post_data.put("token", MainActivity.token);

        task.execute(post_data);
    }

    @Override
    public void handle_user(UserInformation userInformation) {
        if (userInformation == null){
            return;
        }

        username_edit.setText(userInformation.username);
        if (userInformation.nick_name != null){
            nickname_edit.setText(userInformation.nick_name);
        }
    }



    @Override
    public void handle_user_alter(String response) {
        if (response.isEmpty()){
            Toast.makeText(PersonalInfoActivity.this, "网络连接错误!", Toast.LENGTH_SHORT).show();
            return ;
        }

        String error = JSONHelper.check_and_get_error(response);
        if (error != null){
            if (error.equals("no such user"))
                Toast.makeText(PersonalInfoActivity.this, "没有该用户!", Toast.LENGTH_SHORT).show();
            else if (error.equals("wrong token"))
                Toast.makeText(PersonalInfoActivity.this, "该账号在其他地方登录过，请返回主界面清除缓存文件后重新登录！", Toast.LENGTH_SHORT).show();
            else if (error.equals("the nickname has been used")){
                Toast.makeText(PersonalInfoActivity.this, "用户名已经被使用", Toast.LENGTH_SHORT).show();
            }else if(error.equals("not authorized to use this name")){
                Toast.makeText(PersonalInfoActivity.this, "不允许使用该用户名", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Toast.makeText(PersonalInfoActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();


    }
}
