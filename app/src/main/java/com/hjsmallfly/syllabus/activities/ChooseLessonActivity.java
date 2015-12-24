package com.hjsmallfly.syllabus.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.DisplayUtil;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.HttpPostTask;
import com.hjsmallfly.syllabus.helpers.LessonItemShapeDrawable;
import com.hjsmallfly.syllabus.helpers.LessonPullTask;
import com.hjsmallfly.syllabus.helpers.WebApi;
import com.hjsmallfly.syllabus.helpers.classInfoUtil;
import com.hjsmallfly.syllabus.interfaces.LessonHandler;
import com.hjsmallfly.syllabus.interfaces.PostDataGetter;
import com.hjsmallfly.syllabus.otherViews.SyncHorizontalScrollView;
import com.hjsmallfly.syllabus.otherViews.SyncScrollView;
import com.hjsmallfly.syllabus.syllabus.ClassInfo;
import com.hjsmallfly.syllabus.syllabus.Lesson;
import com.hjsmallfly.syllabus.syllabus.R;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseLessonActivity extends AppCompatActivity implements LessonHandler, PostDataGetter {

    String defaultFileName = "defaultchooseLesss.txt";

    //课表显示滚动的那四个滚动条
    SyncHorizontalScrollView gridScrollView;
    SyncHorizontalScrollView dayScrollView;
    SyncScrollView classScrollView;
    SyncScrollView timeScrollView;

    //课表显示GridLayout
    GridLayout myClassTable;
    LinearLayout syllabus_bg;

    public static final String WALL_PAPER_FILE_NAME = "syllabus_wallpaper.jpeg";

    private Bitmap wall_paper;

    String jsonString = "";
    List<ClassInfo> classInfoList;
    Map<Integer, ClassInfo> classOfDay;

    String classInfoListName;

    final int timeOfAllDay = 13;//一天课程总数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_choose_lesson);

        try {
            initView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadInfo() {
        jsonString = "";
        if (!FileOperation.hasFile(this, defaultFileName)) {
            FileOperation.save_to_file(this, defaultFileName, "");
        } else {
            jsonString = FileOperation.read_from_file(this, defaultFileName);
        }
    }

    public void initView() throws JSONException {
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

        // 读取之前的壁纸
        load_syllabus_wallpaper();

        loadInfo();
        classInfoList = new ArrayList<>();
        classOfDay = new HashMap<>();
        loadClassInfo(jsonString, true);
        showSyllabus();
    }

    private void load_syllabus_wallpaper() {
        String file_path = FileOperation.get_app_folder(true) + WALL_PAPER_FILE_NAME;
        File wall_paper_file = new File(file_path);
        if (wall_paper_file.exists()) {
            load_bitmap(file_path);
        }
    }

    private void load_bitmap(String file_path) {
        wall_paper = BitmapFactory.decodeFile(file_path);
        Drawable drawable = new BitmapDrawable(getResources(), wall_paper);
        syllabus_bg.setBackground(drawable);
    }

    public void showSyllabus() {
        myClassTable.removeAllViews();

        final int defaultGridWidth = DisplayUtil.dip2px(this, 48);
        final int defaultGridHeight = DisplayUtil.dip2px(this, 58);
        final int defaultLLWidth = DisplayUtil.dip2px(this, 50);
        final int defaultLLHeight = DisplayUtil.dip2px(this, 60);

        ClassInfo lastClassInfo = null;


        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < timeOfAllDay; j++) {

                final int indexSum = i * timeOfAllDay + j;
                final ClassInfo classInfo = classOfDay.get(indexSum);
                if (lastClassInfo != null && classInfo == lastClassInfo) {
                    continue;
                }

                LinearLayout ll = new LinearLayout(this);
                TextView textView = new TextView(this);

                textView.setTextSize(11);
                textView.setTextColor(Color.WHITE);
                textView.setWidth(defaultGridWidth);
                textView.setHeight(defaultGridHeight);
                ll.setMinimumWidth(defaultLLWidth);
                ll.setMinimumHeight(defaultLLHeight);
                ll.setGravity(Gravity.CENTER);


                GridLayout.Spec rowSpec = GridLayout.spec(j);
                GridLayout.Spec columnSpec = GridLayout.spec(i);


                //看是否存在这个课的课表
                if (classInfo != null) {
                    float roundR = 15.0f;
                    float[] outerR = new float[]{roundR, roundR, roundR, roundR, roundR, roundR, roundR, roundR};
                    Shape shape = new
                            RoundRectShape(outerR, null, null);

                    textView.setBackground(new LessonItemShapeDrawable(shape, classInfo.getBgColor()));

                    final String outputClassName;
                    String outputClassRoom;
                    int maxClassNameString = 7;
                    int maxClassRoomString = 8;

                    boolean isallEnglish = true;
                    for (char c : classInfo.getClassName().toCharArray()) {
                        if (c > 128) {
                            isallEnglish = false;
                            break;
                        }
                    }
                    if (isallEnglish) maxClassNameString *= 2;

                    int timeOfClass = 1;
                    for (int k = j + 1; k < timeOfAllDay; k++) {
                        int index = i * timeOfAllDay + k;
                        ClassInfo otherClassInfo = classOfDay.get(index);
                        if (otherClassInfo != null && otherClassInfo == classInfo) {
                            ++timeOfClass;
                        } else break;
                    }

                    //System.out.println(classInfo.getClassName() + " " + maxClassNameString * timeOfClass);

                    if (classInfo.getClassName().length() > maxClassNameString * timeOfClass + 11) {
                        outputClassName = classInfo.getClassName().substring(0, maxClassNameString * timeOfClass + 11 - 3) + "...";
                    } else outputClassName = classInfo.getClassName();

                    if (classInfo.getClassRoom().length() > maxClassRoomString * timeOfClass) {
                        outputClassRoom = classInfo.getClassRoom().substring(0, maxClassRoomString * timeOfClass - 3) + "...";
                    } else outputClassRoom = classInfo.getClassRoom();

                    textView.setText(outputClassName + "\n@" + outputClassRoom);
                    textView.setGravity(Gravity.CENTER_VERTICAL);

                    textView.setHeight(defaultGridHeight * timeOfClass);
                    rowSpec = GridLayout.spec(j, timeOfClass);

                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ChooseLessonActivity.this,ShowClassInfoActivity.class);
                            intent.putExtra("classInfo",classInfo);
                            startActivity(intent);
                        }
                    });

                    ll.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final String[] collectionItems = {"删除课程"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseLessonActivity.this)
                                    .setItems(collectionItems, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                deleteClass(classInfo.getClassName());
                                                loadClassInfo(jsonString, true);
                                                showSyllabus();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            builder.create().show();
                            return true;
                        }
                    });

                } else {
                    textView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //System.out.println("On long click!");
                            final String[] collectionItems = {"添加课程"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseLessonActivity.this)
                                    .setItems(collectionItems, new DialogInterface.OnClickListener() {

                                        View view = View.inflate(ChooseLessonActivity.this, R.layout.input_class_no, null);
                                        Button submitClassNo = (Button) view.findViewById(R.id.submitClassNo);
                                        EditText submitClassNoEditText = (EditText) view.findViewById(R.id.submitClassNoEditText);

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseLessonActivity.this)
                                                    .setTitle("请输入那门课程的开课班号")
                                                    .setView(view);

                                            final AlertDialog alertDialog = builder.create();
                                            alertDialog.show();

                                            submitClassNo.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    getClassFromServer(submitClassNoEditText.getText().toString());
                                                    alertDialog.hide();
                                                }
                                            });
                                        }
                                    });

                            builder.create().show();
                            return false;
                        }
                    });
                }


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
                ll.setGravity(Gravity.CENTER);
                ll.requestLayout();

                lastClassInfo = classInfo;
            }


        }

    }


    public void loadClassInfo(String string, boolean isClean) throws JSONException {


        List<ClassInfo> newClassInfoList = new ArrayList<>();
        classOfDay.clear();

        if (string == null || string.isEmpty()) {
            //Toast.makeText(ChooseLessonActivity.this, "没有该课程", Toast.LENGTH_SHORT).show();
            return;
        }

        //清除之前的数据
        if (isClean) {
            classInfoList.clear();
        }

        final classInfoUtil location = new classInfoUtil(this);
        location.setJsonString(string);
        newClassInfoList = location.getClassList();

        if(!isClean&&newClassInfoList.size()==0){
            Toast.makeText(ChooseLessonActivity.this,"没有该课程",Toast.LENGTH_SHORT).show();
        }

        classInfoList.addAll(newClassInfoList);

        final int[] bgColor = {
                Color.argb(178, 28, 196, 179),
                Color.argb(178, 80, 193, 250),
                Color.argb(178, 44, 177, 245),
                Color.argb(178, 2, 197, 151),
                Color.argb(178, 254, 141, 65),
                Color.argb(178, 247, 125, 138),
                Color.argb(178, 84, 134, 234),
                Color.argb(178, 200, 50, 101),
                Color.argb(178, 114, 204, 59),
                Color.argb(178, 102, 124, 177),
                Color.argb(178, 43, 144, 205),
                Color.argb(178, 122, 166, 201),
                Color.argb(178, 78, 217, 27),
                Color.argb(178, 226, 56, 145),
                Color.argb(178, 109, 55, 123),
                Color.argb(178, 227, 119, 195),
        };

        String timeTable = "1234567890ABC";
        String dayTable = "日一二三四五六";


        for (int i = 0; i < classInfoList.size(); i++) {
            ClassInfo classInfo = classInfoList.get(i);
            classInfo.setBgColor(bgColor[i % bgColor.length]);
            String beginTime = classInfo.getBeginTime();
            String[] everyTime = beginTime.split("，");

            boolean isConflict = false;
            List<Integer> timeOfClass = new ArrayList<>();
            if (everyTime.length > 0) {
                for (int j = 1; j < everyTime.length; j++) {

                    String thisTime = everyTime[j];
                    System.out.println(thisTime);
                    String[] thisTimeSplit = thisTime.split("[(]");

                    //获取星期
                    char dateOfTime = thisTimeSplit[0].charAt(thisTimeSplit[0].length() - 1);
                    int dateOfTimeIndex = dayTable.indexOf(dateOfTime);


                    for (int k = 0; k < thisTimeSplit[1].length(); k++) {
                        char tmp = thisTimeSplit[1].charAt(k);
                        if (timeTable.indexOf(tmp) != -1) {
                            int TimeOfDateIndex = timeTable.indexOf(tmp);//获取上课时间
                            int indexSum = dateOfTimeIndex * timeOfAllDay + TimeOfDateIndex;

                            if (classOfDay.get(indexSum) != null) {
                                //Log.v("indexSum", indexSum + "");
                                isConflict = true;
                            }
                            timeOfClass.add(indexSum);
                        }
                    }

                }
            }
            if (!isConflict) {
                for (int index = 0; index < timeOfClass.size(); index++) {
                    classOfDay.put(timeOfClass.get(index), classInfo);
                }
            } else {
                Toast.makeText(ChooseLessonActivity.this,
                        "该课程和" + classInfo.getClassName() + "("
                                + classInfo.getBeginTime() + ")冲突",
                        Toast.LENGTH_SHORT).show();
                classInfoList.remove(classInfo);
            }
        }
    }

    //删除某一个课程
    private void deleteClass(String className) throws JSONException, UnsupportedEncodingException {
        Log.v("classInfoList.size", classInfoList.size() + "");
        for (int i = 0; i < classInfoList.size(); i++) {
            ClassInfo classInfo = classInfoList.get(i);
            if (classInfo.getClassName().equals(className)) {
                classInfoList.remove(i);
            }
        }

        final classInfoUtil location = new classInfoUtil(this);
        jsonString = location.classListToJson(classInfoList);

    }

    private void getClassFromServer(String queryString) {

        final String url = "http://121.42.175.83:8050/t";
//        LessonPullTask sync_task = new LessonPullTask(url, ChooseLessonActivity.this);
//
//        HashMap<String, String> postMap = new HashMap<String, String>();
//        postMap.put("queryString", queryString);
//        postMap.put("semester", "2015-2016学年秋季学期");
//        postMap.put("criterion", "classNo");
//        sync_task.execute(postMap);

        HashMap<String, String> post_data = new HashMap<>();
        post_data.put("queryString", queryString);
        post_data.put("semester", "2015-2016学年春季学期");
        post_data.put("criterion", "classNo");

        HttpPostTask task = new HttpPostTask(url, this);
        task.execute(post_data);

        //Log.v("queryString",queryString);
    }

    @Override
    public void deal_with_lessons(String raw_data) {

    }

    @Override
    public void handle_post_response(String response) {
        if (response.isEmpty()) {
            Toast.makeText(ChooseLessonActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
        }
        jsonString = response;

        try {
            loadClassInfo(jsonString, false);
            //Log.v("jsonStringLen", jsonString.length() + "");
            showSyllabus();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.save_chosen_class:
                // 保存
                final classInfoUtil location = new classInfoUtil(this);
                try {
                    jsonString = location.classListToJson(classInfoList);
                    FileOperation.save_to_file(ChooseLessonActivity.this, defaultFileName, jsonString);
                    Toast.makeText(ChooseLessonActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
//                break;
            case R.id.clean_chosen_class:
                //清除
                try {
                    jsonString = "";
                    loadClassInfo(jsonString, true);
                    classInfoList.clear();
                    showSyllabus();
                    Toast.makeText(ChooseLessonActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
