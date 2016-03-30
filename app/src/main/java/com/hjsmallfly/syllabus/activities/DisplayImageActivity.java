package com.hjsmallfly.syllabus.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hjsmallfly.syllabus.helpers.BitmapHelper;
import com.hjsmallfly.syllabus.syllabus.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DisplayImageActivity extends AppCompatActivity {

    public static Drawable drawable;

    private ImageView imageView;
    private PhotoViewAttacher attacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = (ImageView) findViewById(R.id.displayImageView);
        attacher = new PhotoViewAttacher(imageView);
        display();
    }


    private void display(){
        // 读取图片文件
        Bitmap bitmap = BitmapHelper.drawableToBitmap(drawable);
        if (bitmap == null)
            return;
        // -----获取屏幕信息------
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        // -----获取屏幕信息------
        // 计算图片原先的比例
        float photo_ratio = (float) bitmap.getWidth() / bitmap.getHeight();
        // 以屏幕的宽度作为图片的新宽度
//        int new_width = width;
        // 根据比例计算出相应的高度
        int new_height = (int) (width / photo_ratio);
        // 生成新的图片
        bitmap = Bitmap.createScaledBitmap(bitmap, width, new_height, true);
        imageView.setImageBitmap(bitmap);
        attacher.update();
    }


    @Override
    public void finish() {
        super.finish();
        // 消除动画
        overridePendingTransition(0, 0);
    }
}
