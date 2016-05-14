package com.hjsmallfly.syllabus.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class SyllabusActivity extends AppCompatActivity implements LessonHandler, TokenGetter {

    public static Lesson clicked_lesson;

    public static final String DEFAULT_SYLLABUS_FILE = "default_syllabus";

    public static final String WALL_PAPER_FILE_NAME = "syllabus_wallpaper.jpeg";

    public static final String WALL_PAPER_FILE_TEMP = "temp_pic_file.jpg";

    private static final int PICK_PHOTO_FROM_GALLERY = 1; // 从相册中选择
    private static final int CROP_PHOTO_REQUEST = 2; // 结果

    // 用于自主添加课程的界面
    private LinearLayout custom_dialog_layout;
    private AlertDialog custom_dialog;


    private Bitmap wall_paper;

    //syllabus
    LinearLayout dayLinearLayout;
    LinearLayout timeLinearLayout;

    //课表显示滚动的那四个滚动条
    SyncHorizontalScrollView gridScrollView;
    SyncHorizontalScrollView dayScrollView;
    SyncScrollView classScrollView;
    SyncScrollView timeScrollView;

    //课表显示GridLayout
    GridLayout myClassTable;
    LinearLayout syllabus_bg;

    LinearLayout clickTextView;


    private void setupViews() {

        dayLinearLayout = (LinearLayout) findViewById(R.id.dayLinearLayout);
        timeLinearLayout = (LinearLayout) findViewById(R.id.timeLinearLayout);

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


        custom_dialog_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_class_layout, null);

        // 读取之前的壁纸
        load_syllabus_wallpaper();

        showSyllabus();

    }

    /**
     * 添加自定义课程
     */
    private void addCustomClass(){
        final EditText id = (EditText) custom_dialog_layout.findViewById(R.id.custom_id);
        final EditText name = (EditText) custom_dialog_layout.findViewById(R.id.custom_name);
        final EditText teacher = (EditText) custom_dialog_layout.findViewById(R.id.custom_teacher);
        final EditText room = (EditText) custom_dialog_layout.findViewById(R.id.custom_room);
        final EditText credit = (EditText) custom_dialog_layout.findViewById(R.id.custom_credit);
        final EditText weekday = (EditText) custom_dialog_layout.findViewById(R.id.custom_weekday);
        final EditText start_time = (EditText) custom_dialog_layout.findViewById(R.id.start_time);
        final EditText end_time = (EditText) custom_dialog_layout.findViewById(R.id.end_time);
        RadioButton default_radio = (RadioButton) custom_dialog_layout.findViewById(R.id.all_week_radio);
        final RadioButton odd_week_radio = (RadioButton) custom_dialog_layout.findViewById(R.id.single_week_radio);
        final RadioButton even_week_radio = (RadioButton) custom_dialog_layout.findViewById(R.id.double_week_radio);
        final EditText start_week = (EditText) custom_dialog_layout.findViewById(R.id.start_week);
        final EditText end_week = (EditText) custom_dialog_layout.findViewById(R.id.end_week);


        // 默认设置
        default_radio.setChecked(true);
        start_week.setText("1");
        end_week.setText("16");

        if (custom_dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SyllabusActivity.this);
            builder.setView(custom_dialog_layout);
            builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                                        Toast.makeText(SyllabusActivity.this, "确认添加", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(SyllabusActivity.this, "取消添加", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            custom_dialog = builder.create();
        }


        custom_dialog.show();
        custom_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prevent the dialog from dismissing
                String weekday_ = weekday.getText().toString();
                String start_time_ = start_time.getText().toString();
                String end_time_ = end_time.getText().toString();
                String start_week_ = start_week.getText().toString();
                String end_week_ = end_week.getText().toString();
                String credit_ = credit.getText().toString();

                if (credit_.isEmpty() || weekday_.isEmpty() || start_time_.isEmpty() || end_time_.isEmpty() || start_week_.isEmpty() || end_week_.isEmpty()) {
                    Toast.makeText(SyllabusActivity.this, "*号字段必须填写", Toast.LENGTH_SHORT).show();
                    return;
                }

                int week_flag = Lesson.ALL_WEEK;

                if (odd_week_radio.isChecked())
                    week_flag = Lesson.ODD_WEEK;
                else if (even_week_radio.isChecked())
                    week_flag = Lesson.EVEN_WEEK;


                Lesson new_lesson = Lesson.makeLesson(name.getText().toString(), teacher.getText().toString(), room.getText().toString(),
                        week_flag, Integer.parseInt(weekday_), Integer.parseInt(start_week_), Integer.parseInt(end_week_),
                        Integer.parseInt(start_time_), Integer.parseInt(end_time_), Integer.parseInt(credit_), id.getText().toString());

                Log.d("custom_class", ClassParser.calcPosition(new_lesson, 7, 11).toString());
                Log.d("custom_class", Arrays.toString(new_lesson.get_duration()));
                Log.d("custom_class", new_lesson.toJson().toString());


                if (ClassParser.checkConflict(ClassParser.all_classes, new_lesson))
                    Toast.makeText(SyllabusActivity.this, "冲突", Toast.LENGTH_SHORT).show();
                else {

                    if (new_lesson.addToSyllabus(SyllabusActivity.this)) {
                        //                                     清空所有数据
                        id.setText("");
                        name.setText("");
                        room.setText("");
                        teacher.setText("");
                        credit.setText("");
                        start_time.setText("");
                        end_time.setText("");
                        start_week.setText("");
                        end_week.setText("");
                        weekday.setText("");
                        custom_dialog.dismiss();
                        Toast.makeText(SyllabusActivity.this, "成功添加", Toast.LENGTH_SHORT).show();
                        showSyllabus();
                        custom_dialog.dismiss();
                    } else
                        Toast.makeText(SyllabusActivity.this, "存入文件失败", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    /**
     * 删除课程
     * @param lesson
     * @return 是否删除成功
     */
    private boolean removeClass(Lesson lesson){
        try {

            JSONObject syllabusObj = new JSONObject(MainActivity.syllabus_json_data);
            JSONArray allLessons = syllabusObj.getJSONArray("classes");
            // 用于生成新的 jsonString
            JSONArray toKeep = new JSONArray();
            for(int i = 0 ; i < allLessons.length() ; ++i){
                JSONObject each_lesson = (JSONObject) allLessons.get(i);
                String name = each_lesson.getString("name");
                if (!name.equals(lesson.name))
                    toKeep.put(each_lesson);
//                Log.d("removeClass", "比较: " + name + ", " + lesson.name);
            }
//            Log.d("removeClass", toKeep.toString());
            // 抹去数据
            syllabusObj.put("classes", toKeep);
            String filename = StringDataHelper.generate_syllabus_file_name(MainActivity.cur_username, MainActivity.cur_year_string, MainActivity.cur_semester, "_");
            return FileOperation.save_to_file(this, filename, syllabusObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showSyllabus() {
        ClassParser parser = new ClassParser(this, this);
        String json_data = FileOperation.read_from_file(this,
                StringDataHelper.generate_syllabus_file_name(MainActivity.cur_username, MainActivity.cur_year_string, MainActivity.cur_semester, "_"));
        if (json_data == null)
            return;
        MainActivity.syllabus_json_data = json_data;
        parser.parseJSON(json_data, false);
        parser.calcClassPosition();
        MainActivity.syllabusData = parser.syllabusGrid;
        Object[] weekdays_syllabus_data = MainActivity.syllabusData;
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

                    do_not_show = false;

                    if (prevClassID != null && prevClassID.equals(lesson.id)) {
                        continue;
                    }
                }


                final LinearLayout ll = new LinearLayout(SyllabusActivity.this);
                final TextView textView = new TextView(SyllabusActivity.this);
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

                    textView.setText(lesson_str /* + "@" + lesson.room */);
                    textView.setGravity(Gravity.CENTER);

                    //计算下面有多少节相同的课程
                    int timeOfClass = 1;

                    for (int k = j + 1; k <= 13; k++) {
                        int nextIndex = index + (k - j) * 8;
                        if (!(weekdays_syllabus_data[nextIndex] instanceof Lesson)) {
                            break;
                        }
                        Lesson otherLesson = (Lesson) weekdays_syllabus_data[nextIndex];
                        if (otherLesson.toString().equals(lesson.toString())) {
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
                    float[] outerR = new float[]{roundR, roundR, roundR, roundR, roundR, roundR, roundR, roundR};


                    Shape shape = new
                            RoundRectShape(outerR, null, null);

                    textView.setBackground(new LessonItemShapeDrawable(shape, lesson.colorID));

                    final Lesson finalLesson = lesson;


                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickTextView = ll;
                            showClassInfo(finalLesson);
                        }
                    });

                    ll.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Toast.makeText(SyllabusActivity.this, "删除课程", Toast.LENGTH_SHORT).show();
                            if (removeClass(finalLesson))
                                showSyllabus();
                            return true;
                        }
                    });

                }
                else{
//                    final int row_index = j;
//                    final int column_index = i;
                    // 空白格子
                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addCustomClass();
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

                if (!do_not_show)
                    prevClassID = lesson.id;
                else
                    prevClassID = null;


                myClassTable.requestLayout();
            }
        }

    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
        try {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                String title = item.getTitle().toString();
                Spannable spannable = new SpannableString(title);
                spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
                        spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                item.setTitle(spannable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide().setDuration(200));
        }

        setContentView(R.layout.activity_syllabus);
        getOverflowMenu();
        setupViews();
        int this_week = ClassParser.calculate_week(Calendar.getInstance());
        setActionBarTitle("第" + this_week + "周");
//        Toast.makeText(SyllabusActivity.this, "当前周数是" + MainActivity.initial_week, Toast.LENGTH_SHORT).show();


    }

    // 友盟的统计功能
    @Override
    protected void onResume() {
        super.onResume();
        setupViews();
        int this_week = ClassParser.calculate_week(Calendar.getInstance());
        setActionBarTitle("第" + this_week + "周");
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
        Intent tab_intent = new Intent(this, ShowClassInfoActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clickTextView.setTransitionName("lesson_grid");
            startActivity(tab_intent,ActivityOptions.makeSceneTransitionAnimation(SyllabusActivity.this,
                    clickTextView,"lesson_grid").toBundle());
        }else{
            startActivity(tab_intent);
        }
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


            case R.id.global_discuss_action:
                Intent global_discuss_intent = new Intent(this, SocialActivity.class);
                startActivity(global_discuss_intent);
                break;


            case R.id.sync_syllabus_action:
                // 更新课表
                // 友盟
                MobclickAgent.onEvent(this, "More_Sync");
                sync_syllabus();
                break;

            case R.id.login_to_internet:
                InternetLogin.login_to_internet(this, MainActivity.cur_username, MainActivity.cur_password);
                break;

            case R.id.set_week:
                set_week_info();
                break;

            case R.id.add_class:
                addCustomClass();
                break;

            case R.id.save_syllabus:
                saveSyllabus();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private boolean set_week_info() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("选择周数(目前周一作为第一天)");

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(16);
        numberPicker.setMinValue(1);
        int this_week = ClassParser.calculate_week(Calendar.getInstance());
        numberPicker.setValue(this_week);
        builder.setView(numberPicker);

        builder.setPositiveButton("设定周数", new DialogInterface.OnClickListener() {
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
                if (FileOperation.save_to_file(SyllabusActivity.this, filename, content)) {
                    Toast.makeText(SyllabusActivity.this, "设定当前周数为 " + week, Toast.LENGTH_SHORT).show();
                    MainActivity.initial_week = week;
                    MainActivity.initial_date = date_string;
                    ClassParser parser = new ClassParser(SyllabusActivity.this, SyllabusActivity.this);
                    parser.parseJSON(MainActivity.syllabus_json_data, false);
                    parser.calcClassPosition();     // 用数据填充课表
                    MainActivity.syllabusData = parser.syllabusGrid;
                    setActionBarTitle("第" + week + "周");
                } else {
                    Toast.makeText(SyllabusActivity.this, "设置周数出错", Toast.LENGTH_SHORT).show();
                }

                showSyllabus();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("仅查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int week = numberPicker.getValue();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);   // 0-11
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String date_string = year + "/" + month + "/" + day;
                Toast.makeText(SyllabusActivity.this, "查看第 " + week + " 周的课表", Toast.LENGTH_SHORT).show();
                MainActivity.initial_week = week;
                MainActivity.initial_date = date_string;
                ClassParser parser = new ClassParser(SyllabusActivity.this, SyllabusActivity.this);
                parser.parseJSON(MainActivity.syllabus_json_data, false);
                parser.calcClassPosition();     // 用数据填充课表
                MainActivity.syllabusData = parser.syllabusGrid;
                setActionBarTitle("第" + week + "周");
                showSyllabus();
                dialog.dismiss();
            }
        });

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
            classParser.calcClassPosition();     // 用数据填充课表
            MainActivity.syllabusData = classParser.syllabusGrid;
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


    public void saveSyllabus() {

        Bitmap dayBitmap = getViewBitmap(dayLinearLayout);
        Bitmap timeBitmap = getViewBitmap(timeLinearLayout);
        Bitmap syllabusBitmap = getViewBitmap(myClassTable);

        Matrix matrix = new Matrix();

        float scale = 1.0f;
        if (wall_paper != null) {
            scale = (dayBitmap.getHeight() + syllabusBitmap.getHeight()) * 1.0f / wall_paper.getHeight();

            Log.d("Scale", dayBitmap.getHeight() + "");
            Log.d("Scale", syllabusBitmap.getHeight() + "");
            //Log.d("Scale", wall_paper.getHeight() + "");

            matrix.postScale(scale, scale);
        }

        Bitmap result = Bitmap.createBitmap(timeBitmap.getWidth() + syllabusBitmap.getWidth(),
                syllabusBitmap.getHeight() + dayBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        if (wall_paper != null) {
            Bitmap resizeBmp = Bitmap.createBitmap(wall_paper, 0, 0, wall_paper.getWidth(), wall_paper.getHeight(), matrix, true);
            canvas.drawBitmap(resizeBmp, 0, 0, null);
        } else {
            canvas.drawARGB(255, 255, 255, 255);
        }
        canvas.drawBitmap(dayBitmap, 0, 0, null);
        canvas.drawBitmap(timeBitmap, 0, dayBitmap.getHeight(), null);
        canvas.drawBitmap(syllabusBitmap, timeBitmap.getWidth(), dayBitmap.getHeight(), null);


        MediaStore.Images.Media.insertImage(getContentResolver(), result, "syllabus", "description");

        Toast.makeText(SyllabusActivity.this, "已保存到图库", Toast.LENGTH_SHORT).show();

    }

    public Bitmap getViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

}
