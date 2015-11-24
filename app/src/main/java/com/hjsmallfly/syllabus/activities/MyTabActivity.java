package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.adapters.DiscussionAdapter;
import com.hjsmallfly.syllabus.helpers.ClipBoardHelper;
import com.hjsmallfly.syllabus.helpers.DeleteTask;
import com.hjsmallfly.syllabus.helpers.JSONHelper;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.helpers.WebApi;
import com.hjsmallfly.syllabus.interfaces.AfterDeleteHandler;
import com.hjsmallfly.syllabus.syllabus.Discussion;
import com.hjsmallfly.syllabus.interfaces.DiscussionHandler;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.syllabus.Homework;
import com.hjsmallfly.syllabus.interfaces.HomeworkHandler;
import com.hjsmallfly.syllabus.parsers.HomeworkParser;
import com.hjsmallfly.syllabus.helpers.HttpCommunication;
import com.hjsmallfly.syllabus.helpers.InfoPullTask;
import com.hjsmallfly.syllabus.syllabus.Lesson;
import com.hjsmallfly.syllabus.syllabus.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/10/7.
 */
public class MyTabActivity extends AppCompatActivity implements View.OnClickListener, HomeworkHandler,
        DiscussionHandler, TabHost.OnTabChangeListener, AfterDeleteHandler{

    public static Lesson lesson;

//    Context context;

    public static final String PERSONAL_TAB = "personal";
    public static final String HOMEWORK_TAB = "homework";
    public static final String DISCUSS_TAB = "discuss";

    private static int MESSAGE_COUNT = 100;

    // 个人备注区域
    private TextView class_info_text_view;
    private Button submit_button;
    private EditText personal_comment_area;
    private String personal_comment = "";

    // 作业区
    private EditText last_homework;
    private EditText homework_content_edit;
    private EditText homework_time_edit;
    private Button homework_submit_button;
    private Button homework_history_button;

    // 吹水区
    private ListView discussion_list_view;
    private Button submit_discussion_button;
    private Button discussion_history_button;
    private EditText discussion_content_edit;


    private ArrayList<Discussion> discussions;
    private DiscussionAdapter discussionAdapter;

    private TabHost tabHost;
    private TabHost.TabSpec personal_tab_content;
    private TabHost.TabSpec homework_tab_content;
    private TabHost.TabSpec discuss_tab_content;

    private InfoPullTask homework_pull_task;
    private InfoPullTask discussion_pull_task;

    public void setLesson(Lesson lesson){
        MyTabActivity.lesson = lesson;
//        Log.d(MainActivity.TAG, lesson.representation());
        class_info_text_view.setText(lesson.representation());
        personal_comment = load_comment();
        if (personal_comment != null) {
            personal_comment_area.setText(personal_comment);
            personal_comment_area.setSelection(personal_comment.length());
        }else{
            personal_comment_area.setText("");
        }
        // 每次设置这个的时候应该把之前的数据先清空
        homework_content_edit.setText("");
        homework_time_edit.setText("");
//        last_homework.setVisibility(View.INVISIBLE);

        // 当调用这个函数的时候就拉取一次最新的消息
//        get_latest_homework(1);
    }


    private void find_views(){
        class_info_text_view = (TextView) findViewById(R.id.dialog_content);
        submit_button = (Button) tabHost.findViewById(R.id.personal_submit);
        personal_comment_area = (EditText) tabHost.findViewById(R.id.personal_note);

        homework_time_edit = (EditText) tabHost.findViewById(R.id.homework_time_edit);
        homework_content_edit = (EditText) tabHost.findViewById(R.id.homework_content_edit);
        homework_submit_button = (Button) tabHost.findViewById(R.id.homework_submit_button);
        homework_history_button = (Button) tabHost.findViewById(R.id.homework_history_button);
        last_homework = (EditText) tabHost.findViewById(R.id.last_homework);

        discussion_list_view = (ListView) tabHost.findViewById(R.id.discuss_list_view);
        discussion_content_edit = (EditText) tabHost.findViewById(R.id.talk_field);
        submit_discussion_button = (Button) tabHost.findViewById(R.id.submit_discussion_button);
        discussion_history_button = (Button) tabHost.findViewById(R.id.discuss_history_button);
    }

    private void setup_views(){

        setContentView(R.layout.dialog_layout);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.setOnTabChangedListener(this);

        personal_tab_content = tabHost.newTabSpec(PERSONAL_TAB).setIndicator("个人").setContent(R.id.personal_layout);
        tabHost.addTab(personal_tab_content);

        homework_tab_content = tabHost.newTabSpec(HOMEWORK_TAB).setIndicator("作业").setContent(R.id.homework_layout);
        tabHost.addTab(homework_tab_content);

        discuss_tab_content = tabHost.newTabSpec(DISCUSS_TAB).setIndicator("吹水").setContent(R.id.talk_layout);
        tabHost.addTab(discuss_tab_content);

        // 设置标题栏为白色的字

        TabWidget tabWidget = tabHost.getTabWidget();
        for(int i = 0 ; i < tabWidget.getChildCount() ; ++ i){
            TextView title =  (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            // 貌似提供足够的 4 个字节，不然无效
            title.setTextColor(0xffffffff);
        }

        find_views();

        // add listeners
        submit_button.setOnClickListener(this);
        homework_submit_button.setOnClickListener(this);
        homework_history_button.setOnClickListener(this);
        submit_discussion_button.setOnClickListener(this);
        discussion_history_button.setOnClickListener(this);
        homework_submit_button.setClickable(true);

        // 注册上下文菜单
        registerForContextMenu(discussion_list_view);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dialog_layout);
        setup_views();
        setLesson(SyllabusActivity.clicked_lesson);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.personal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_person_action){
            Intent person_intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(person_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Discussion discussion = (Discussion) discussion_list_view.getItemAtPosition(index);
            ClipBoardHelper.setContent(this, discussion.content);
            Toast.makeText(MyTabActivity.this, "成功复制到剪贴板", Toast.LENGTH_SHORT).show();
            return true;
        }else if (item.getTitle().equals("删除")){
            // 删除信息
            int index = info.position;
            Discussion discussion = (Discussion) discussion_list_view.getItemAtPosition(index);
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

    private boolean save_comment(){
        if (personal_comment_area.getText().toString().isEmpty())
            return false;
        // 到这里即内容没有改变

        if (personal_comment_area.getText().toString().equals(personal_comment))
            return false;
        String info = MainActivity.info_about_syllabus;
        String username = info.split(" ")[0];
        String filename = StringDataHelper.generate_class_file_name(username, lesson.id, "_");
//        Log.d(MainActivity.TAG, "saving file" + filename);
        return FileOperation.save_to_file(this, filename, personal_comment_area.getText().toString());
    }

    private String load_comment(){
        String info = MainActivity.info_about_syllabus;
        String username = info.split(" ")[0];
        String filename = StringDataHelper.generate_class_file_name(username, lesson.id, "_");
//        Log.d(MainActivity.TAG, "loading " + filename);
        return FileOperation.read_from_file(this, filename);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 个人信息
            case R.id.personal_submit:
                if (save_comment()){
                    Toast.makeText(MyTabActivity.this, "保存成功~", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyTabActivity.this, "不保存空内容呢", Toast.LENGTH_SHORT).show();
                }

                break;

            // 作业信息
            case R.id.homework_submit_button:
                add_homework_to_database();
                break;

            // 作业历史
            case R.id.homework_history_button:
                show_history_activity(HistoryActivity.HISTORY_TYPES[0]);
                break;

            // 吹水历史
            case R.id.discuss_history_button:
                show_history_activity(HistoryActivity.HISTORY_TYPES[1]);
                break;

            // 添加讨论信息到相应课程
            case R.id.submit_discussion_button:
                add_discussion_to_database();
                break;

            default:
                break;
        }
    }

    private void show_history_activity(String type){
        Intent history_intent = new Intent(this, HistoryActivity.class);
        history_intent.putExtra("type", type);
        startActivity(history_intent);
    }

    /**
     * 添加验证课程的信息
     * @param data  用于POST传送的数据
     */
    private void add_lesson_identifier(HashMap<String, String> data){
        // 对应到具体的课程
        data.put("number", lesson.id);
        data.put("start_year", lesson.start_year + "");
        data.put("end_year", lesson.end_year + "");
        data.put("semester", lesson.semester + "");
        return;
    }


    private void get_latest_homework(int count){
//        HomeworkPullTask get_homework_task = new HomeworkPullTask(this, this);
        // 不可复用 task
        homework_pull_task = new InfoPullTask(this, InfoPullTask.PULL_HOMEWORK);
        homework_pull_task.setHomeworkHandler(this);
        homework_pull_task.get_information(count, lesson.id, lesson.start_year, lesson.end_year, lesson.semester);
    }

    private void get_latest_discussion(int count){
        // task 不可复用
        discussion_pull_task = new InfoPullTask(this, InfoPullTask.PULL_DISCUSSION);
        discussion_pull_task.setDiscussionHandler(this);
        discussion_pull_task.get_information(count, lesson.id, lesson.start_year, lesson.end_year, lesson.semester);
    }

    private void add_lesson_to_database(){
        // def __init__(self, number, name, credit, teacher, room, span, time_, start_year, end_year, semester):
        HashMap<String, String> data = new HashMap<>();
        data.put("number", lesson.id);
        data.put("name", lesson.name);
        data.put("credit", lesson.credit);
        data.put("teacher", lesson.teacher);
        data.put("room", lesson.room);
        data.put("span", lesson.duration);
        data.put("time", "None");
        data.put("start_year", lesson.start_year + "");
        data.put("end_year", lesson.end_year + "");
        data.put("semester", lesson.semester + "");

        // 添加课程
        InsertTask insert_class_task = new InsertTask(WebApi.get_server_address() + getString(R.string.insert_class_api));
        insert_class_task.execute(data);

    }

    private void add_user_to_database(){
        HashMap<String, String> data = new HashMap<>();
        // 用户名
        data.put("username", MainActivity.cur_username);
        InsertTask insert_user_task = new InsertTask(WebApi.get_server_address() +  getString(R.string.insert_user_api));
        insert_user_task.execute(data);
    }

    private void add_homework_to_database(){

        String content = homework_content_edit.getText().toString().trim();
        if (content.isEmpty()){
            Toast.makeText(MyTabActivity.this, "不能发送空作业哟", Toast.LENGTH_SHORT).show();
            return;
        }

        String hand_in_time = homework_time_edit.getText().toString().trim();
        if (hand_in_time.isEmpty()){
            Toast.makeText(MyTabActivity.this, "要填上上交的时间哟~~~", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> data = new HashMap<>();
        // 对应到具体的课程

        add_lesson_identifier(data);

        // 作业的信息
        data.put("publisher", MainActivity.cur_username);
        long timestamp =  (System.currentTimeMillis() / 1000);
        data.put("pub_time", timestamp + "");  // 现在的时间
        data.put("hand_in_time", hand_in_time);
        data.put("content", content); // 去除没必要的空白字符

        // 添加hash_code
        add_hash_code(data, MainActivity.cur_username + timestamp);

        InsertTask insert_homework_task = new InsertTask(WebApi.get_server_address() + getString(R.string.insert_home_work_api));
        insert_homework_task.execute(data);

    }

//    self.parse.add_argument("publisher", required=True)
//            self.parse.add_argument("pub_time", required=True, type=float)
//            self.parse.add_argument("content", required=True)

    public void add_discussion_to_database(){

        String content = discussion_content_edit.getText().toString().trim();
        if (content.isEmpty()){
            Toast.makeText(MyTabActivity.this, "不能发空吐槽哟~~~", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> data = new HashMap<>();

        // 对应到具体的课程

        add_lesson_identifier(data);

        // 讨论的信息
        data.put("publisher", MainActivity.cur_username);
        long timestamp = (System.currentTimeMillis() / 1000);
        data.put("pub_time", timestamp + "");
        data.put("content", content);

        // 增加hash_code
        add_hash_code(data, MainActivity.cur_username + timestamp);

        InsertDiscussionTask task = new InsertDiscussionTask(WebApi.get_server_address() + getString(R.string.insert_discussion_api));
        task.execute(data);
    }

    @Override
    public void deal_with_homework(ArrayList<Homework> all_homework) {
        if (all_homework == null) {
            Log.d(MainActivity.TAG, "看起来好像all_homework是null呢.....");
            return;
        }

        // 显示最新的作业
        Homework latest = all_homework.get(all_homework.size() - 1);    // 最新发布的作业
        last_homework.setText(latest.toString());
    }

    @Override
    public void deal_with_discussion(ArrayList<Discussion> all_discussions) {
        if (all_discussions == null){
            Log.d(MainActivity.TAG, "看起来没读取到吐槽信息呢");
            return;
        }

        // 显示最新的吐槽
        if (this.discussions == null) {
            this.discussions = new ArrayList<>(all_discussions);
        }else{
            this.discussions.clear();
            this.discussions.addAll(all_discussions);
        }
        if (discussionAdapter == null){
            discussionAdapter = new DiscussionAdapter(this, R.layout.discuss_item_layout, this.discussions);
            discussion_list_view.setAdapter(discussionAdapter);
        }else{
            // 更新list view的显示， 注意上面是如何更新 this.discussions 里面的内容的！
            // 不可以直接 this.discussions = all_discussions
            // 否则只是把 this.discussions 这个引用本身指向的地方改变了
            discussionAdapter.notifyDataSetChanged();
        }

        // 确保最新的消息可见
        if (this.discussions.size() > 0)
            discussion_list_view.setSelection(this.discussions.size() - 1);
    }

    @Override
    public void onTabChanged(String tabId) {

        if (tabId.equals(HOMEWORK_TAB))
            get_latest_homework(1);
        else if (tabId.equals(DISCUSS_TAB))
            get_latest_discussion(MESSAGE_COUNT);  // 显示10条吐槽信息

    }

    private static String my_hash(String input_string){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] dataBytes;
            dataBytes = input_string.getBytes("utf-8");
            md.update(dataBytes);
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private static void add_hash_code(HashMap<String, String> data, String input_string){
        String hash_code = my_hash(input_string);
        if (hash_code != null)
            data.put("code", hash_code);
    }

    @Override
    public void deal_with_delete(String response, int position) {
        String error = JSONHelper.check_and_get_error(response);
        if (error != null){
//            Toast.makeText(MyTabActivity.this, "删除错误: " + error, Toast.LENGTH_SHORT).show();
            if (error.equals(DeleteTask.ERROR_WRONG_TOKEN))
                Toast.makeText(MyTabActivity.this, "该账号在其他地方登陆过，请返回主界面清除课程缓存文件。", Toast.LENGTH_SHORT).show();
            else if (error.equals(DeleteTask.ERROR_NO_AUTHORIZED)){
                Toast.makeText(MyTabActivity.this, "只能删除自己的信息哟", Toast.LENGTH_SHORT).show();
            }
            return;

        }
        // 代表删除成功
        Discussion discussion = (Discussion) discussion_list_view.getItemAtPosition(position);
        discussionAdapter.remove(discussion);
        discussionAdapter.notifyDataSetChanged();
        Toast.makeText(MyTabActivity.this, "成功删除消息" /*+ discussion.content*/, Toast.LENGTH_SHORT).show();
    }


    /**
     * 用于完成 需要 POST 的网络任务
     */
    class InsertTask extends AsyncTask<HashMap<String, String> , Void, String> {

        private String address;

        public InsertTask(String address){
            this.address = address;
        }


        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            return HttpCommunication.performPostCall(this.address, params[0]);
        }

        @Override
        protected void onPostExecute(String response){
            if (response.isEmpty()){
                Toast.makeText(MyTabActivity.this, "error connection", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONTokener json_parser = new JSONTokener(response);
            try {
                JSONObject json_obj = (JSONObject) json_parser.nextValue();
                if (json_obj.has(HomeworkParser.ERROR_STRING)){
                    String error_string = json_obj.getString(HomeworkParser.ERROR_STRING);
                    String NO_SUCH_CLASS = "no such class";
                    String NO_SUCH_USER = "no such user";
                    // 这是第一个检查的元素
                    if (error_string.equals(NO_SUCH_CLASS)){
                        // 说明要添加这节课到数据库中
                        add_lesson_to_database();
                    }else if (error_string.equals(NO_SUCH_USER)){
                        // 说明要添加用户到数据库中
                        add_user_to_database();
                    }else{
                        // 就是 wrong code 的情况了
                        return;
                    }
                    // 再试一次 应该就ok了
                    add_homework_to_database();
                }else{
                    Toast.makeText(MyTabActivity.this, "信息分享成功哟~~~~", Toast.LENGTH_SHORT).show();
                    homework_content_edit.setText("");
                    homework_time_edit.setText("");
                    get_latest_homework(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 用于插入评论
     */
    class InsertDiscussionTask extends AsyncTask<HashMap<String, String>, Void, String>{

        private String address;

        public InsertDiscussionTask(String addr){
            this.address = addr;
        }

        public void setAddress(String addr){
            this.address = addr;
        }

        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            return HttpCommunication.performPostCall(this.address, params[0]);
        }

        @Override
        protected void onPostExecute(String response){
            if (response.isEmpty()){
                Toast.makeText(MyTabActivity.this, "error connection", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONTokener json_parser = new JSONTokener(response);
            try {
                JSONObject json_obj = (JSONObject) json_parser.nextValue();
                if (json_obj.has(HomeworkParser.ERROR_STRING)){
                    String error_string = json_obj.getString(HomeworkParser.ERROR_STRING);
                    String NO_SUCH_CLASS = "no such class";
                    String NO_SUCH_USER = "no such user";
                    // 这是第一个检查的元素
                    if (error_string.equals(NO_SUCH_CLASS)){
                        // 说明要添加这节课到数据库中
                        add_lesson_to_database();
                    }else if (error_string.equals(NO_SUCH_USER)){
                        // 说明要添加用户到数据库中
                        add_user_to_database();
                    }else{
                        // wrong code 的情况
                        return;
                    }
                    // 再试一次 应该就ok了
                    add_discussion_to_database();
                }else{
                    Toast.makeText(MyTabActivity.this, "吐槽成功", Toast.LENGTH_SHORT).show();
                    discussion_content_edit.setText("");
                    get_latest_discussion(MESSAGE_COUNT);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

