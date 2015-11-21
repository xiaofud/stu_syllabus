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

    @Override
    public String toString(){
        return class_name + ": " + class_grade;
    }
}
