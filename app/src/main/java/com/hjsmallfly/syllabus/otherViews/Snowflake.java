package com.hjsmallfly.syllabus.otherViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.hjsmallfly.syllabus.syllabus.R;

/**
 * Created by daidaijie on 2015/12/24.
 */
public class Snowflake extends View {

    public float currentX;
    public float currentY;

    int[] snowflakeRes = new int[]{
            R.drawable.snowflake1,
            R.drawable.snowflake2,
            R.drawable.snowflake3,
            R.drawable.snowflake4,
            R.drawable.snowflake5,
            R.drawable.snowflake6,
            R.drawable.snowflake7,
            R.drawable.snowflake8,
    };

    Bitmap snowflakeBitmap;



    public Snowflake(Context context) {
        super(context);
        snowflakeBitmap = BitmapFactory.decodeResource(context.getResources(), snowflakeRes[(int) (Math.random() * snowflakeRes.length)]);
        //setFocusable(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        canvas.drawBitmap(snowflakeBitmap, currentX, currentY, paint);
    }




}
