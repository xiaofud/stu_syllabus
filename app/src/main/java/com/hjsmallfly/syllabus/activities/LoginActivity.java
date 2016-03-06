package com.hjsmallfly.syllabus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.syllabus.R;

public class LoginActivity extends AppCompatActivity {

    // 用于强行第一次显示这个界面
    public static final String VERIFY_FILE_NAME = "has_showed_this_login_window.txt";

    // 用于作为输入框的初始值
    public static String setted_username;
    public static String setted_password;

    private Button login_button;
    private EditText username_box;
    private EditText password_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        username_box = (EditText) findViewById(R.id.login_user);
        password_box = (EditText) findViewById(R.id.login_password);

        if (setted_username != null && setted_password != null){
            username_box.setText(setted_username);
            password_box.setText(setted_password);

            username_box.setSelection(setted_username.length());
        }

        login_button = (Button) findViewById(R.id.login_button_for_girl);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = username_box.getText().toString().trim();
                String password = password_box.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "虽然是女生节,还是需要账号密码的", Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity.cur_username = username;
                MainActivity.cur_password = password;
                FileOperation.save_to_file(LoginActivity.this, VERIFY_FILE_NAME, "");
                finish();
            }
        });
    }

    /**
     * 禁用掉返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
