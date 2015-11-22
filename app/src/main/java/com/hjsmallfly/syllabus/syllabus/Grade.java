package com.hjsmallfly.syllabus.syllabus;

/**
 * Created by smallfly on 15-11-21.
 * 用于记录成绩信息
 */
public class Grade {
    public String class_credit;
    public String class_grade;
    public String class_name;
    public String class_number;
    public String class_teacher;
    public String semester;
    public String years;

    /**
     * 因为有些课程名字 前后都有 [xxx] [xxx], 所以现在简化出来
     * @return
     */
    public static String get_simple_name(String full_name){
        // 先找到第一个 ]
        int end_index = full_name.indexOf("]");
        if (end_index == -1)
            return full_name;
        full_name = full_name.substring(end_index + 1);
        end_index = full_name.indexOf("[");
        if (end_index == -1)
            return full_name;
        full_name = full_name.substring(0, end_index);
        return full_name;
    }

    @Override
    public String toString(){
        return class_name + ": " + class_grade;
    }
}
