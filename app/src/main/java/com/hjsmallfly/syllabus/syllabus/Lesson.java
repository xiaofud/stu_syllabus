package com.hjsmallfly.syllabus.syllabus;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by STU_nwad on 2015/9/23.
 *
 */
public class Lesson {
    public String name;
    public String id;
    public String teacher;
    public String room;
    public String duration;
    public String credit;
    public HashMap<String, String> days;
    public int colorID;

    // 额外的信息
    public int start_year;
    public int end_year;
    public int semester;

    // 颜色编号
    public int color_code;

    // 记录课程是否分为单双周
    public String comment;

//    self.class_id = str(start_year) + "_" + str(end_year) + "_" + str(semester) + "_" + self.class_number

    public String generate_class_id(){
        return start_year + "_" + end_year + "_" + semester + "_" + id;
    }


    @Override
    public String toString(){
        // 去掉课程的[课程号]
        String name_without_prefix = name;
        int s_index = name.indexOf(']');
        if (s_index != -1){
            name_without_prefix = name.substring(s_index + 1);
        }
        // 考虑单双周的情况
        if (comment != null)
            name_without_prefix = "[" + comment + "]" + name_without_prefix;

        return name_without_prefix + "@" + room + "" /* + days.toString() */;
    }


    public int[] get_duration(){
        String[] week_strs = duration.split("-");
        int[] range = new int[2];
        for(int i = 0 ; i < 2 ; ++i){
            range[i] = Integer.parseInt(week_strs[i].trim());
        }
        return range;
    }
}
