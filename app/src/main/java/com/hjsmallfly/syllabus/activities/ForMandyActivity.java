package com.hjsmallfly.syllabus.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hjsmallfly.syllabus.otherViews.Snowflake;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ForMandyActivity extends AppCompatActivity {

//    public List<Snowflake> snowFlakeList;

    private float sx = 0.1f;
    private float sy = 4.0f;

    FrameLayout for_mandy_root;

    ImageView daijieMail;

    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        // 取消再次显示
        MainActivity.has_show_special_girl = true;

        setContentView(R.layout.activity_for_mandy);
        initView();
    }


    public void initView() {
        for_mandy_root = (FrameLayout) findViewById(R.id.for_mandy_root);
        daijieMail = (ImageView) findViewById(R.id.daijieMail);

        final List<Snowflake> snowFlakeList = new ArrayList<>();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();

        display.getMetrics(metrics);

        Button skip_button = (Button) findViewById(R.id.skip_button);
        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //画雪花
        /*for (int i = 0; i < 20; i++) {
            Snowflake snowflake = new Snowflake(ForMandyActivity.this);
            snowflake.currentX = (float) (Math.random() * metrics.widthPixels);
            snowflake.currentY = 0;
            snowflake.setFocusable(true);
            snowflake.invalidate();
            snowFlakeList.add(snowflake);
            for_mandy_root.addView(snowflake);


            Log.v("x",snowflake.currentX+"");
        }*/

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x1233) {

                    //创建雪花
                    if(Math.random()<0.05)
                    {
                        Snowflake snowflake = new Snowflake(ForMandyActivity.this);
                        snowflake.currentX = (float) (Math.random() * metrics.widthPixels);
                        snowflake.currentY = -100;
                        snowflake.setFocusable(true);
                        snowflake.invalidate();
                        snowFlakeList.add(snowflake);
                        for_mandy_root.addView(snowflake);
                    }


                    //移动雪花
                    for (int i = 0; i < snowFlakeList.size(); i++) {
                        Snowflake snowflake = snowFlakeList.get(i);
                        snowflake.currentX += sx;
                        snowflake.currentY += sy;

                        if (snowflake.currentX > metrics.widthPixels || snowflake.currentY > metrics.heightPixels) {
                            for_mandy_root.removeView(snowflake);
                            snowFlakeList.remove(i);
                        }
                        snowflake.invalidate();
                    }
                }
            }
        };


        TimerTask timerTask = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 0x1233;
                handler.sendMessage(message);


            }
        };


        Timer timer = new Timer(true);
        timer.schedule(timerTask, 100, 30);


    }

    public void showEmail(View view){
        daijieMail.setVisibility(View.VISIBLE);
    }

    public void hideEmail(View view){
        daijieMail.setVisibility(View.GONE);
    }
}