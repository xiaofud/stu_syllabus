package com.hjsmallfly.syllabus.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.ColorHelper;
import com.hjsmallfly.syllabus.helpers.DisplayUtil;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.InternetLogin;
import com.hjsmallfly.syllabus.helpers.LessonItemShapeDrawable;
import com.hjsmallfly.syllabus.helpers.LessonPullTask;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.helpers.WebApi;
import com.hjsmallfly.syllabus.interfaces.LessonHandler;
import com.hjsmallfly.syllabus.interfaces.TokenGetter;
import com.hjsmallfly.syllabus.otherViews.SyncHorizontalScrollView;
import com.hjsmallfly.syllabus.otherViews.SyncScrollView;
import com.hjsmallfly.syllabus.parsers.ClassParser;
import com.hjsmallfly.syllabus.syllabus.Lesson;
import com.hjsmallfly.syllabus.syllabus.R;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


public class SyllabusActivity extends AppCompatActivity implements LessonHandler, TokenGetter {

    public static Lesson clicked_lesson;

    public static final String DEFAULT_SYLLABUS_FILE = "default_syllabus";

    public static final String WALL_PAPER_FILE_NAME = "syllabus_wallpaper.jpeg";

    public static final String WALL_PAPER_FILE_TEMP = "temp_pic_file.jpg";

    public static final String COLOR_FILE_NAME = "syllabus_text_color";

    private static final int PICK_PHOTO_FROM_GALLERY = 1; // 从相册中选择
    private static final int CROP_PHOTO_REQUEST = 2; // 结果

    //private Button show_oa_button;
//    private TextView info_text;

    private Bitmap wall_paper;


    //课表显示滚动的那四个滚动条
    SyncHorizontalScrollView gridScrollView;
    SyncHorizontalScrollView dayScrollView;
    SyncScrollView classScrollView;
    SyncScrollView timeScrollView;

    //课表显示GridLayout
    GridLayout myClassTable;
    LinearLayout syllabus_bg;


    private void setupViews() {

        //设置同步滚动
        gridScrollView = (SyncHorizontalScrollView) findViewById(R.id.gridScrollView);
        dayScrollView = (SyncHorizontalScrollView) findViewById(R.id.dayScrollView);
        gridScrollView.setScrollView(dayScrollView);
        dayScrollView.setScrollView(gridScrollView);
        dayScrollView.setHorizontalScrollBarEnabled(false);

        classScrollView = (SyncScrollView) findViewById(R.id.classScrollView);
        timeScrollView = (SyncScrollView) findViewById(R.id.timeScrollView);
        classScrollView.setScrollView(timeScrollView);
        timeScrollView.setScrollView(classScrollView);
        timeScrollView.setVerticalScrollBarEnabled(false);

        myClassTable = (GridLayout) findViewById(R.id.myClassTable);
        syllabus_bg = (LinearLayout) findViewById(R.id.syllabus_bg);
        showSyllabus();

        // 读取之前的壁纸
        load_syllabus_wallpaper();


    }


    public void showSyllabus() {
        Object[] weekdays_syllabus_data = MainActivity.weekdays_syllabus_data;
        myClassTable.removeAllViews();

        final int defaultGridWidth = DisplayUtil.dip2px(this, 48);
        final int defaultGridHeight = DisplayUtil.dip2px(this, 58);
        final int defaultLLWidth = DisplayUtil.dip2px(this, 50);
        final int defaultLLHeight = DisplayUtil.dip2px(this, 60);

        String prevClassID = null;
        boolean do_not_show;    // 不需要显示

        for (int i = 0; i < 7; ++i) {
            for (int j = 1; j <= 13; j++) {

                int index = j * 8 + i;
                Lesson lesson = null;

                if (!(weekdays_syllabus_data[index] instanceof Lesson)) {
                    do_not_show = true;
                } else {
                    lesson = (Lesson) weekdays_syllabus_data[index];

//                    Log.d("debug", lesson.name);
                    do_not_show = false;


//                    boolean need_to_test_single_or_double = true;

                    // -------------判断是否课程已经上完了-------------
//                    Calendar calendar = Calendar.getInstance();
//                    // 年月日
//                    String[] date = MainActivity.initial_date.split("/");
//                    int [] fields = new int[3];
//                    for (int loop = 0 ; loop < fields.length ; ++loop)
//                        fields[loop] = Integer.parseInt(date[loop]);
////                    Toast.makeText(SyllabusActivity.this, Arrays.toString(fields), Toast.LENGTH_SHORT).show();
//
//                    // 月份是从0开始切记
//                    calendar.set(fields[0], fields[1], fields[2]);
////                    calendar.set(fields[0], 1, 15);
//                    // day of week 是按照 周日为第一天，所以周三就是第四天，但是是星期三
//                    int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
////                    Toast.makeText(SyllabusActivity.this, "今天是" + day_of_week, Toast.LENGTH_SHORT).show();
//                    // 计算出星期一的日期
//                    calendar.add(Calendar.DAY_OF_MONTH, - (day_of_week - 1 ));
//                    int month = calendar.get(Calendar.MONTH) + 1;
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
////                    Toast.makeText(SyllabusActivity.this, "这周星期一是" + month + "/" + day , Toast.LENGTH_SHORT).show();
//                    Calendar today = Calendar.getInstance();
//                    long diff = ( today.getTime().getTime() - calendar.getTime().getTime() ) / (60 * 60 * 24 * 1000);
////                    Toast.makeText(SyllabusActivity.this, "日期相差了" + diff, Toast.LENGTH_SHORT).show();
//                    int this_week = (int) diff / 7 + MainActivity.initial_week;
////                    Toast.makeText(SyllabusActivity.this, "这周是" + this_week, Toast.LENGTH_SHORT).show();
//                    int[] range = lesson.get_duration();
////                    Toast.makeText(SyllabusActivity.this, Arrays.toString(range), Toast.LENGTH_SHORT).show();
//                    if (this_week < range[0] || this_week > range[1]) {
//                        do_not_show = true;
//                        Log.d("pick", lesson.name);
//                        need_to_test_single_or_double = false;
//                    }
//
//                    // -------------判断是否课程已经上完了-------------

//                    if (need_to_test_single_or_double) {
//                        // -------------判断是否有单双周的情况-------------
//                        // 额外判断周数
//                        // 单双周显示
//                        int w = -1;
////                    int day = -1;
//                        String has_single_or_double = null;
//                        for (String day_obj : lesson.days.keySet()) {
//                            String time_str = lesson.days.get(day_obj);
//                            if (time_str.contains("单")) {
//                                has_single_or_double = "单";
//                                w = Integer.parseInt(day_obj.substring(1));
//                            } else if (time_str.contains("双")) {
//                                has_single_or_double = "双";
//                                w = Integer.parseInt(day_obj.substring(1));
//                            }
//                        }
//
//                        if (has_single_or_double != null) {
//                            if (w == i) {
////                            lesson_str = "[" + has_single_or_double + "]" + lesson_str;
//                                int week = MainActivity.initial_week;
//                                if (week % 2 == 0 && has_single_or_double.equals("单")){
//                                    do_not_show = true;
//                                }else if (week % 2 == 1 && has_single_or_double.equals("双")){
//                                    do_not_show = true;
//                                }
//                            }
//                        }
//
//                        // -------------判断是否有单双周的情况-------------
//                    }


//                    if (!do_not_show) {
                        if (prevClassID != null && prevClassID.equals(lesson.id)) {
                            continue;
                        }
//                    }
                }


                LinearLayout ll = new LinearLayout(SyllabusActivity.this);
                TextView textView = new TextView(SyllabusActivity.this);
                textView.setTextSize(11);
                textView.setTextColor(Color.WHITE);
                textView.setWidth(defaultGridWidth);
                textView.setHeight(defaultGridHeight);
                ll.setMinimumWidth(defaultLLWidth);
                ll.setMinimumHeight(defaultLLHeight);
                ll.setGravity(Gravity.CENTER);

                textView.setHeight(defaultGridHeight);
                GridLayout.Spec rowSpec = GridLayout.spec(j - 1);
                GridLayout.Spec columnSpec = GridLayout.spec(i);

                if (!do_not_show) {

                    String lesson_str = lesson.toString();

//                    // 单双周显示
//                    int w = -1;
////                    int day = -1;
//                    String has_single_or_double = null;
//                    for (String day_obj : lesson.days.keySet()) {
//                        String time_str = lesson.days.get(day_obj);
//                        if (time_str.contains("单")) {
//                            has_single_or_double = "单";
//                            w = Integer.parseInt(day_obj.substring(1));
//                        } else if (time_str.contains("双")) {
//                            has_single_or_double = "双";
//                            w = Integer.parseInt(day_obj.substring(1));
//                        }
//                    }
//
//                    if (has_single_or_double != null) {
//                        if (w == i) {
//                            lesson_str = "[" + has_single_or_double + "]" + lesson_str;
//                        }
//                    }

                    textView.setText(lesson_str /* + "@" + lesson.room */);
                    textView.setGravity(Gravity.CENTER);

                    //计算下面有多少节相同的课程
                    int timeOfClass = 1;

                    for (int k = j + 1; k <= 13; k++) {
                        int nextIndex = index + (k - j) * 8;
                        if (!(weekdays_syllabus_data[nextIndex] instanceof Lesson)) {
                            break;
                        }
                        Lesson otherlesson = (Lesson) weekdays_syllabus_data[nextIndex];
                        if (otherlesson.toString().equals(lesson.toString())) {
                            ++timeOfClass;
                        } else break;
                    }
//                    textView.setAlpha(0.7f);
                    textView.setHeight(defaultGridHeight * timeOfClass);
                    ll.setMinimumHeight(defaultLLHeight * timeOfClass);
                    rowSpec = GridLayout.spec(j - 1, timeOfClass);
                    Log.v("Note", timeOfClass + "");

//                    textView.setBackgroundColor(textView.getResources().getColor(
//                            lesson.colorID
//                    ));
                    //textView.setBackground(new ColorDrawable(lesson.colorID));

                    float roundR = 15.0f;
                    float[] outerR = new float[] { roundR, roundR, roundR, roundR,roundR, roundR, roundR, roundR  };
                    Shape shape = new
                            RoundRectShape(outerR, null, null);

                    textView.setBackground(new LessonItemShapeDrawable(shape, lesson.colorID));

                    final Lesson finalLesson = lesson;
                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showClassInfo(finalLesson);
                        }
                    });
                }

                //ll.setPadding(5,5,5,5);
                ll.addView(textView);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                textView.requestLayout();


                myClassTable.addView(ll, new GridLayout.LayoutParams(rowSpec, columnSpec));
                ViewGroup.LayoutParams llp = ll.getLayoutParams();
                llp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                llp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                ll.setLayoutParams(llp);
                ll.requestLayout();

                if (!do_not_show) prevClassID = lesson.id;
                else prevClassID = null;


                myClassTable.requestLayout();
            }
        }

    }

    private void setActionBarTitle(String title){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        setupViews();
        setActionBarTitle("第" + MainActivity.initial_week + "周");
//        Toast.makeText(SyllabusActivity.this, "当前周数是" + MainActivity.initial_week, Toast.LENGTH_SHORT).show();


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

    public void showClassInfo(Lesson lesson) {
        // 友盟
        MobclickAgent.onEvent(this, "Main_ShowDetail");

        clicked_lesson = lesson;
//        Toast.makeText(SyllabusActivity.this, lesson.days.toString(), Toast.LENGTH_SHORT).show();
        Intent tab_intent = new Intent(this, MyTabActivity.class);
        startActivity(tab_intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_syllabus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_default_syllabus: {
                if (set_default_syllabus()) {
                    Toast.makeText(SyllabusActivity.this, "成功设置默认课表", Toast.LENGTH_SHORT).show();
                    return true;
                } else
                    return false;
            }
            case R.id.pick_wallpaper:
                pick_photo();
                break;

            case R.id.query_grade_action:

                // 友盟
                MobclickAgent.onEvent(this, "More_Grade");

                // 查看成绩
                Intent grade_intent = new Intent(this, GradeActivity.class);
                startActivity(grade_intent);
                break;

            case R.id.global_discuss_action:
                Intent global_discuss_intent = new Intent(this, GlobalDiscussActivity.class);
                startActivity(global_discuss_intent);
                break;

            case R.id.query_exam_action:
                // 友盟
                MobclickAgent.onEvent(this, "More_Exam");
                Intent exam_intent = new Intent(this, ExamActivity.class);
                startActivity(exam_intent);
                break;

            case R.id.sync_syllabus_action:
                // 更新课表
                // 友盟
                MobclickAgent.onEvent(this, "More_Sync");
                sync_syllabus();
                break;
            case R.id.show_oa_action:
                Intent intent = new Intent(this, OAActivity.class);
                startActivity(intent);
                break;

            case R.id.choose_class_action:
                Intent choose_activity = new Intent(this, ChooseLessonActivity.class);
                startActivity(choose_activity);
                break;

            case R.id.login_to_internet:
                InternetLogin.login_to_internet(this, MainActivity.cur_username, MainActivity.cur_password);
                break;

            case R.id.set_week:
                set_week_info();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private boolean set_week_info(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("输入当前周数");

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(16);
        numberPicker.setMinValue(1);
        numberPicker.setValue(MainActivity.initial_week);
        builder.setView(numberPicker);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int week = numberPicker.getValue();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);   // 0-11
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String date_string = year + "/" + month + "/" + day;
                String content = date_string + "," + week;

                String filename = FileOperation.generate_week_file(MainActivity.cur_username
                        , MainActivity.cur_year_string,
                        MainActivity.cur_semester + "");
                if (FileOperation.save_to_file(SyllabusActivity.this, filename, content)){
                    Toast.makeText(SyllabusActivity.this, "设定当前周数为 " + week, Toast.LENGTH_SHORT).show();
                    MainActivity.initial_week = week;
                    MainActivity.initial_date = date_string;
                    ClassParser parser = new ClassParser(SyllabusActivity.this, SyllabusActivity.this);
                    parser.parseJSON(MainActivity.json_data, false);
                    parser.inflateTable();     // 用数据填充课表
                    MainActivity.weekdays_syllabus_data = parser.weekdays_syllabus_data;
                    setActionBarTitle("第" + MainActivity.initial_week + "周");
                }else{
                    Toast.makeText(SyllabusActivity.this, "设置周数出错", Toast.LENGTH_SHORT).show();
                }

                showSyllabus();
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.create().show();
        return true;
    }

    /**
     * 设置默认学期
     */
    private boolean set_default_syllabus() {
        String syllabus_file_name = StringDataHelper.generate_syllabus_file_name(MainActivity.cur_username, MainActivity.cur_year_string,
                MainActivity.cur_semester, "_");
        // Debug
//        Toast.makeText(SyllabusActivity.this, syllabus_view.getWidth() + ", " + syllabus_view.getHeight(), Toast.LENGTH_SHORT).show();
        return FileOperation.save_to_file(this, DEFAULT_SYLLABUS_FILE, syllabus_file_name);

    }

    private void pick_photo() {
        Intent pick_intent = new Intent(Intent.ACTION_PICK, null);
        if (!FileOperation.is_sd_mounted() || !FileOperation.create_app_folder()) {
            Toast.makeText(SyllabusActivity.this, "读取错误", Toast.LENGTH_SHORT).show();
            return;
        }
        pick_intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pick_intent, PICK_PHOTO_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_PHOTO_FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    int width = syllabus_bg.getWidth();
                    int height = syllabus_bg.getHeight();
                    startPhotoZoom(data.getData(), width, height);
                }
                break;
            case CROP_PHOTO_REQUEST:
                if (resultCode == RESULT_OK) {
                    set_syllabus_wallpaper();

                    // 添加友盟的统计数据
                    MobclickAgent.onEvent(this, "Setting_Background_Custom");
                }
                break;
        }
    }

    private void load_bitmap(String file_path) {
        wall_paper = BitmapFactory.decodeFile(file_path);
        Drawable drawable = new BitmapDrawable(getResources(), wall_paper);
        syllabus_bg.setBackground(drawable);
    }

    private void set_syllabus_wallpaper() {
        String file_path = Environment.getExternalStorageDirectory() + "/" + FileOperation.APP_FOLDER + "/" + WALL_PAPER_FILE_TEMP;
        String wall_paper_file = Environment.getExternalStorageDirectory() + "/" + FileOperation.APP_FOLDER + "/" + WALL_PAPER_FILE_NAME;
        FileOperation.copy_file(new File(file_path), new File(wall_paper_file));
        load_bitmap(wall_paper_file);
    }


    private void load_syllabus_wallpaper() {
        String file_path = FileOperation.get_app_folder(true) + WALL_PAPER_FILE_NAME;
        File wall_paper_file = new File(file_path);
        if (wall_paper_file.exists()) {
            load_bitmap(file_path);
        }
    }


    private void startPhotoZoom(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);

        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        Uri picture_uri = FileOperation.getTempUri();
        if (picture_uri == null)
            return;

        intent.putExtra(MediaStore.EXTRA_OUTPUT, picture_uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, CROP_PHOTO_REQUEST);
    }


//    private void set_text_color(int color) {
//
//        ColorHelper.save_color_to_file(this, color, COLOR_FILE_NAME);
//    }

//    private void set_random_color() {
//        int color = ColorHelper.get_random_color();
//        set_text_color(color);
//    }

    private void sync_syllabus() {
        LessonPullTask sync_task = new LessonPullTask(WebApi.get_server_address() + getString(R.string.syllabus_get_api), this);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", MainActivity.cur_username);
        postData.put("password", MainActivity.cur_password);
        postData.put("submit", "query");
        postData.put("years", MainActivity.cur_year_string);
        postData.put("semester", MainActivity.cur_semester + "");
        sync_task.execute(postData);
    }

    @Override
    public void deal_with_lessons(String raw_data) {
        if (raw_data.isEmpty()) {
            Toast.makeText(SyllabusActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
            return;
        }

        ClassParser classParser = new ClassParser(this, this);
        if (classParser.parseJSON(raw_data, true)) {
            classParser.inflateTable();     // 用数据填充课表
            MainActivity.weekdays_syllabus_data = classParser.weekdays_syllabus_data;
//                    Log.d(TAG, "established adapter");

            // 保存文件 命名格式: name_years_semester
            String username = MainActivity.cur_username;
//                    String filename = username + "_" + YEARS[position] + "_"
//                            + semester;
            // 保存文件 格式是: 14xfdeng_2014-2015_autumn
            String filename = StringDataHelper.generate_syllabus_file_name(username, MainActivity.cur_year_string, MainActivity.cur_semester, "_");
            if (FileOperation.save_to_file(this, filename, raw_data)) {
//                        Toast.makeText(MainActivity.this, "成功保存文件 " + filename, Toast.LENGTH_SHORT).show();
                Log.d(MainActivity.TAG, "saved file " + filename);
            }
            // 保存用户文件
            FileOperation.save_user(this, MainActivity.USERNAME_FILE, MainActivity.PASSWORD_FILE, username, MainActivity.cur_password);

            // 记得重新显示
            showSyllabus();

            Toast.makeText(SyllabusActivity.this, "课表同步成功", Toast.LENGTH_SHORT).show();

            // 统计用户登陆次数
            MobclickAgent.onProfileSignIn(MainActivity.cur_username);
        }
//                    Toast.makeText(MainActivity.this, "读取课表成功哟~~~~", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void get_token(String token) {
        // 保存token
        FileOperation.save_to_file(this, StringDataHelper.generate_token_file_name(MainActivity.cur_username), token);
        MainActivity.token = token;
    }
}
