package com.hjsmallfly.syllabus.interfaces;

import com.hjsmallfly.syllabus.syllabus.Grade;

import java.util.List;

/**
 * Created by smallfly on 15-11-21.
 * 处理成绩信息
 */
public interface GradeHandler {

    // 没有任何成绩信息
    static String ERROR_NO_GRADE_INFO = "there is no information about grade";

    void handle_grade_list(List<Grade> grade_list);
}
