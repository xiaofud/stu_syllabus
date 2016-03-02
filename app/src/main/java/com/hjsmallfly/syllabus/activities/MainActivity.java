package com.hjsmallfly.syllabus.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.LessonPullTask;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.helpers.UpdateHelper;
import com.hjsmallfly.syllabus.helpers.WebApi;
import com.hjsmallfly.syllabus.interfaces.LessonHandler;
import com.hjsmallfly.syllabus.interfaces.TokenGetter;
import com.hjsmallfly.syllabus.interfaces.UpdateHandler;
import com.hjsmallfly.syllabus.parsers.ClassParser;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.syllabus.Lesson;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.SyllabusVersion;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, UpdateHandler, LessonHandler, TokenGetter, Spinner.OnItemSelectedListener {
    public static Object[] weekdays_syllabus_data;     // 用于向显示课表的activity传递数据
    public static ArrayList<Lesson> weekends_syllabus_data;
    public static String info_about_syllabus;
    public static final String USERNAME_FILE = "username.txt";
    public static final String PASSWORD_FILE = "password.txt";

    // 用户的token数据
    public static String token = "";

    // 用于和其他activity共享的数据
    public static String cur_year_string;
    public static int cur_semester;
    public static String cur_username;
    public static String cur_password;

    // 控件及常量
    public static final String TAG = "POSTTEST";
    public static String[] YEARS;// = {"2012-2013", "2013-2014", "2014-2015", "2015-2016", "2016-2017", "2017-2018"};
    public static final String[] SEMESTER = new String[]{"SPRING", "SUMMER", "AUTUMN"};

    public static final String[] SEMESTER_CHINESE = new String[]{"春季学期", "夏季学期", "秋季学期"};


    private int position = -1;  // 用于决定保存的文件名
//    private int semester;    // 用于决定保存的文件名


    //    private EditText address_edit;  // 服务器地址
    private EditText username_edit;
    private EditText password_edit;
//    private ListView syllabus_list_view;    // 用于显示所有课表的list_view

    private Spinner year_spinner;
    private Spinner semester_spinner;
    private Button query_button;

    private EditText debug_ip_edit;

    // 如果已经显示过默认课表就没必要再显示了
    private boolean has_showed_default = false;
    private boolean has_checked_update = false;

    private UpdateHelper updateHelper;

    // 创建主界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 加载主布局
        YEARS = StringDataHelper.generate_years(4);  // 生成5年的选项
        getAllViews();
        setupViews();

        // 设置web service 的默认地址
        WebApi.set_server_address(getString(R.string.server_ip));

        // 检查更新
        if (!has_checked_update)
            check_update();
        if (!has_showed_default)
            load_default_syllabus();

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

    private void getAllViews() {
//        address_edit = (EditText) findViewById(R.id.address_edit);
        username_edit = (EditText) findViewById(R.id.username_edit);
        password_edit = (EditText) findViewById(R.id.passwd_edit);
//        syllabus_list_view = (ListView) findViewById(R.id.syllabus_list_view);

        year_spinner = (Spinner) findViewById(R.id.year_spinner);
        semester_spinner = (Spinner) findViewById(R.id.semester_spinner);
        query_button = (Button) findViewById(R.id.query_syllabus_button);

        debug_ip_edit = (EditText) findViewById(R.id.debug_ip_edit);
    }

    private void setupViews() {
//        YearSemesterChooseParser list_adapter = new YearSemesterChooseParser(this);
//        syllabus_list_view.setAdapter(list_adapter);

        year_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, YEARS));
        semester_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, SEMESTER_CHINESE));

        // 读取用户
        String[] user = FileOperation.load_user(this, USERNAME_FILE, PASSWORD_FILE);
        if (user != null) {
            username_edit.setText(user[0]);
            password_edit.setText(user[1]);
            cur_password = user[1];

            if (user[0].equals("14xfdeng")) {
                // 开启debug模式
                debug_ip_edit.setVisibility(View.VISIBLE);

            } else {
                debug_ip_edit.setVisibility(View.GONE);
            }
        } else
            debug_ip_edit.setVisibility(View.GONE);


        // 选项卡

        // listener
        query_button.setOnClickListener(this);

        semester_spinner.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.check_update_action) {
            // 友盟
            MobclickAgent.onEvent(this, "Check_Update");
            Intent update_activity = new Intent(this, UpdateActivity.class);
            startActivity(update_activity);
            return true;
        }

        if (id == R.id.delete_default_syllabus) {
            if (delete_default_syllabus())
                Toast.makeText(MainActivity.this, "清除了默认课表的设置", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "清除默认课表出错", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.about_us_action) {
            // 友盟
            MobclickAgent.onEvent(this, "Setting_Aboutus");
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.help_action) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean delete_default_syllabus() {
        if (FileOperation.hasFile(this, SyllabusActivity.DEFAULT_SYLLABUS_FILE))
            return FileOperation.delete_file(this, SyllabusActivity.DEFAULT_SYLLABUS_FILE);
        return true;
    }

    private void load_default_syllabus() {
        String default_file_name = FileOperation.read_from_file(this, SyllabusActivity.DEFAULT_SYLLABUS_FILE);
        if (default_file_name != null) {
            if (FileOperation.hasFile(this, default_file_name)) {
//                Toast.makeText(MainActivity.this, "存在文件: " + default_file_name, Toast.LENGTH_SHORT).show();
                String json_data = FileOperation.read_from_file(this, default_file_name);
                if (json_data != null) {
                    // 设置一些相关信息
                    String[] info = default_file_name.split("_");
                    cur_username = info[0];
                    if (!cur_username.equals(username_edit.getText().toString()))
                        // 说明用户已经登录了其他账号
                        return;
                    cur_year_string = info[1];
                    cur_semester = StringDataHelper.semester_to_int(info[2]);
                    // 把选项也弄成当前学期的
                    semester_spinner.setSelection(StringDataHelper.semester_to_selection_index(cur_semester));
                    for (int i = 0; i < YEARS.length; ++i)
                        if (cur_year_string.equals(YEARS[i]))
                            position = i;
                    info_about_syllabus = cur_username + " " + cur_year_string + " " + info[2];
                    has_showed_default = true;
                    // 本地课表文件里面存的token可能是过期的.
                    parse_and_display(json_data, false);
                }
            }
        }
    }

    private void set_cur_semester_with_spinner(int selection_id) {
        switch (selection_id) {
            case 0:
//                semester = 2;
                cur_semester = 2;
                break;
            case 1:
//                semester = 3;
                cur_semester = 3;
                break;
            case 2:
//                semester = 1;
                cur_semester = 1;
                break;
            default:
                Log.d(TAG, "maybe there is a typo  in submit(int, int)");
                break;
        }
    }

    /**
     * @param year_index             年份下标
     * @param semester_spinner_index 下拉菜单的选中项, 注意这个并不对应学分制所需要的学期参数
     */
    private void submit_query_request(int year_index, int semester_spinner_index) {
        this.position = year_index;
        String username = username_edit.getText().toString();
        cur_username = username;

        String years = YEARS[year_index];  // 点击到列表的哪一项
        cur_year_string = years;    // 用于共享目的

        String password = password_edit.getText().toString();
        cur_password = password;

        // 更新一下 服务器的地址
        WebApi.set_server_address(debug_ip_edit.getText().toString());

        // 读取之前存的 token
        get_local_token();

        set_cur_semester_with_spinner(semester_spinner_index);

        info_about_syllabus = username + " " + years + " " + StringDataHelper.semester_to_string(cur_semester);
        // 先判断有无之前保存的文件
//        String filename = username + "_" + years + "_" + semester;
        String filename = StringDataHelper.generate_syllabus_file_name(username, years, cur_semester, "_");
        String json_data = FileOperation.read_from_file(MainActivity.this, filename);
        if (json_data != null) {
            // 本地的文件里面的token可能是过期的
            parse_and_display(json_data, false);
            return;
        }

        Toast.makeText(MainActivity.this, "正在获取课表信息", Toast.LENGTH_SHORT).show();


        // 禁用按钮
        query_button.setEnabled(false);

//            {"SPRING", "SUMMER", "AUTUMN"}

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("password", password);
        postData.put("submit", "query");
        postData.put("years", years);
        postData.put("semester", cur_semester + "");
//        Log.d(TAG, "onClick");

        LessonPullTask task = new LessonPullTask(WebApi.get_server_address() + getString(R.string.syllabus_get_api), this);
        task.execute(postData);


//        syllabusGetter.execute(postData);
    }

    private void check_update() {
        if (updateHelper == null)
            updateHelper = new UpdateHelper(this, this);
        updateHelper.check_for_update();
        has_checked_update = true;
    }


    @Override
    public void deal_with_update(int flag, final SyllabusVersion version) {
        if (flag == UpdateHandler.EXIST_UPDATE) {
            // 存在更新的话
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("发现新版本, 是否更新?");
            builder.setMessage("描述:\n" + version.description);
            builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Intent update_activity = new Intent(MainActivity.this, UpdateActivity.class);
//                    startActivity(update_activity);
                    updateHelper.download(version.dowload_address, version);
                    Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("稍后", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }


    @Override
    public void deal_with_lessons(String raw_data) {

        // 恢复按钮
        query_button.setEnabled(true);

        if (raw_data.isEmpty()) {
            Toast.makeText(MainActivity.this, "没能成功获取课表数据", Toast.LENGTH_SHORT).show();
            return;
        }
        // 统计用户登陆
        MobclickAgent.onProfileSignIn(MainActivity.cur_username);

        // 从网络拉过来的数据中 token 肯定是新的, 所以需要更新本地的token

        parse_and_display(raw_data, true);
    }

    private void parse_and_display(String json_data, boolean update_local_token) {
//        if (classParser == null)
        // 每次用新的classParser [暂时这样修复这个BUG]
        ClassParser classParser = new ClassParser(this, this);
        if (classParser.parseJSON(json_data, update_local_token)) {
            classParser.inflateTable();     // 用数据填充课表
            MainActivity.weekdays_syllabus_data = classParser.weekdays_syllabus_data;
//            MainActivity.weekends_syllabus_data = classParser.weekend_classes;
//                    Log.d(TAG, "established adapter");

            // 保存文件 命名格式: name_years_semester
            String username = ((EditText) MainActivity.this.findViewById(R.id.username_edit)).getText().toString();
//                    String filename = username + "_" + YEARS[position] + "_"
//                            + semester;
            // 保存文件 格式是: 14xfdeng_2014-2015_autumn
            String filename = StringDataHelper.generate_syllabus_file_name(username, YEARS[position], cur_semester, "_");
            if (FileOperation.save_to_file(MainActivity.this, filename, json_data)) {
//                        Toast.makeText(MainActivity.this, "成功保存文件 " + filename, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "saved file " + filename);
                // 保存用户文件
                FileOperation.save_user(MainActivity.this, USERNAME_FILE, PASSWORD_FILE, username, password_edit.getText().toString());

                // 读取token
                get_local_token();

                Intent syllabus_activity = new Intent(MainActivity.this, SyllabusActivity.class);
                startActivity(syllabus_activity);
//                    Toast.makeText(MainActivity.this, "读取课表成功哟~~~~", Toast.LENGTH_SHORT).show();

            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_syllabus_button:
                query_syllabus();
                break;
            default:
                break;
        }
    }

    private void query_syllabus() {
        int year_index = year_spinner.getSelectedItemPosition();
        int semester_index = semester_spinner.getSelectedItemPosition();
        submit_query_request(year_index, semester_index);
    }


    @Override
    public void get_token(String token) {
        MainActivity.token = token;
        boolean saved =
                FileOperation.save_to_file(this, StringDataHelper.generate_token_file_name(cur_username), token);
        if (!saved) {
            Toast.makeText(MainActivity.this, "保存Token文件失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取本地存储的token
     */
    public void get_local_token() {
        String filename = StringDataHelper.generate_token_file_name(cur_username);
        if (FileOperation.hasFile(this, filename)) {
            MainActivity.token = FileOperation.read_from_file(this, filename);
//            Toast.makeText(MainActivity.this, "成功读取Token " + token, Toast.LENGTH_SHORT).show();
        } else
            MainActivity.token = "";

    }

    public static void set_local_token(Context use_for_file_context) {
        String filename = StringDataHelper.generate_token_file_name(cur_username);
        if (FileOperation.hasFile(use_for_file_context, filename)) {
            MainActivity.token = FileOperation.read_from_file(use_for_file_context, filename);
//            Toast.makeText(MainActivity.this, "成功读取Token " + token, Toast.LENGTH_SHORT).show();
        } else
            MainActivity.token = "";

    }

//    private void delete_cached_file(){
//        String username = username_edit.getText().toString();
//        int year_index = year_spinner.getSelectedItemPosition();
////        String semester_name = semester_spinner.getSelectedItem().toString();
//        String semester_name = StringDataHelper.semester_to_string(cur_semester);
//        // 错误的值
//        if (semester_name == null)
//            return;
//        String filename = StringDataHelper.generate_syllabus_file_name(username, YEARS[year_index], semester_name, "_");
//        //        Toast.makeText(MainActivity.this, "filename: " + filename, Toast.LENGTH_SHORT).show();
//        delete_cache_file(this, filename);
//    }

//    private void delete_cache_file(Context context, String file_name) {
//        if (FileOperation.hasFile(context, file_name)) {
//            if (FileOperation.delete_file(context, file_name))
//                Toast.makeText(context, "成功删除缓存文件", Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(context, "删除缓存文件失败", Toast.LENGTH_SHORT).show();
//        } else
//            Toast.makeText(context, "不存在该缓存文件", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        set_cur_semester_with_spinner(position);
//        Toast.makeText(MainActivity.this, "position: " + position + " cur_semester = " + cur_semester, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
