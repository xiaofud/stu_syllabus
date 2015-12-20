package com.hjsmallfly.syllabus.helpers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by daidaijie on 2015/12/20.
 */
public class LessonItemShapeDrawable extends ShapeDrawable {
    int color;

    public LessonItemShapeDrawable(Shape s, int
            color) {
        super(s);
        this.color =
                color;
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {

        paint.setStrokeJoin(Paint.Join.ROUND);

        paint.setDither(true);
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(this.color);

        paint.setStrokeWidth(4.0f);
        shape.draw(canvas, paint);

    }
}
