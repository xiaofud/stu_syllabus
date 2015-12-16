package com.hjsmallfly.syllabus.helpers;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/10/16.
 * 主要是一些字符串相关的转换或者生成函数
 */
public class StringDataHelper {

    public static String ERROR_NO_CLASSES = "请选择有课程的学期";
    public static String ERROR_CREDIT_TIME_OUT = "连接学分制超时";
    public static String ERROR_WRONG_PASSWORD = "密码错误";

    public static String ERROR_TOKEN = "该用户在其他设备上登录过, 请在课表界面上点击更新课表, 再重试";

    public static String NO_CLASSES = "No classes";
    public static String WRONG_PASSWORD = "the password is wrong";
    public static String TIMEOUT = "timeout";

    public static String get_error_response(String error){
        if (error.equals(NO_CLASSES))
            return ERROR_NO_CLASSES;
        if (error.equals(WRONG_PASSWORD))
            return ERROR_WRONG_PASSWORD;
        if (error.equals(TIMEOUT))
            return ERROR_CREDIT_TIME_OUT;
        return null;
    }

    public static HashMap<String, String> SEMESTER_LANGUAGE;
    static {
        SEMESTER_LANGUAGE = new HashMap<>();
        SEMESTER_LANGUAGE.put("AUTUMN", "秋");
        SEMESTER_LANGUAGE.put("SUMMER", "夏");
        SEMESTER_LANGUAGE.put("SPRING", "春");
    }

    // 产生近count年的年份字符串 2015-2016
    public static String[] generate_years(int count){
        // 获取当今年份
        int cur_year = Calendar.getInstance().get(Calendar.YEAR);
        // 生成count年的年份数据
        String[] strs = new String[count];
        for(int i = 0 ; i < strs.length ; ++ i){
            strs[i] = (cur_year - i) + "-" + (cur_year - i + 1);
        }
        return strs;
    }

    public static String generate_exam_file(String username, String years, int semester){
        return username + "_" + "_" + years + "_" + semester;
    }

    public static String generate_token_file_name(String username){
        return username + "_token";
    }

    public static String generate_syllabus_file_name(String username, String year, String semester, String sep){
        return username + sep + year + sep + semester;
    }

    public static String generate_syllabus_file_name(String username, String year, int semester, String sep){
        return username + sep + year + sep + semester_to_string(semester);
    }

    public static String generate_class_file_name(String username, String class_id, String sep){
        return username + sep + class_id;
    }

    public static String semester_to_string(int semester){
        switch (semester){
            case 1:
                return "AUTUMN";
            case 2:
                return "SPRING";
            case 3:
                return "SUMMER";
            default:
                return null;
        }
    }

    public static int semester_to_int(String semester){
        if (semester.equals("AUTUMN"))
            return 1;
        else if(semester.equals("SPRING"))
            return 2;
        else if (semester.equals("SUMMER"))
            return 3;
        return -1;
    }

    public static int semester_to_selection_index(int semester){
        // 0 1 2
        // 2 3 1
        switch (semester){
            case 1:
                return 2;
            case 3:
                return 1;
            case 2:
                return 0;
            default:
                return 0;
        }
    }


//    public static String semester_from_view_id(int id){
//        String semester;
//        switch (id){
//            case R.id.spring_text_view:
//                semester = "SPRING";
//                break;
//            case R.id.summer_text_view:
//                semester = "SUMMER";
//                break;
//            case R.id.autumn_text_view:
//                semester = "AUTUMN";
//                break;
//            default:
//                semester = "";
//                break;
//        }
//        return semester;
//    }
}
