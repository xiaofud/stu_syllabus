package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.graphics.Color;

import com.hjsmallfly.syllabus.syllabus.R;

import java.util.Random;

/**
 * Created by STU_nwad on 2015/10/14.
 * 用来产生颜色之类的玩意
 */
public class ColorHelper {

    public static final Random rand = new Random(System.currentTimeMillis());

    public static int get_random_color(){
        int r = rand.nextInt(256);
        int b = rand.nextInt(256);
        int g = rand.nextInt(256);
        return Color.argb(255, r, g, b);
    }

    public static int get_color_from_id(int action_id){
//        if (action_id == R.id.blue_text)
//            return Color.BLUE;
//        if (action_id == R.id.black_text)
            return Color.BLACK;
//        else if (action_id == R.id.gray_text)
//            return Color.GRAY;
//        else
//            return Color.WHITE;

    }

    public static boolean save_color_to_file(Context context, int color, String filename){
        return FileOperation.save_to_file(context, filename, color + "");
    }

    public static int read_color_from_file(Context context, String filename){
        String color_int = FileOperation.read_from_file(context, filename);
        if (color_int == null)
            return Color.WHITE;
        return Integer.parseInt(color_int);
    }

}
