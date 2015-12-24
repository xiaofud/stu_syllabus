package com.hjsmallfly.syllabus.syllabus;

import java.io.Serializable;
import java.util.List;

/**
 * Created by daidaijie on 2015/12/9.
 */
public class ClassInfo implements Serializable {
    private String classRoom;
    private String teacherName;
    private String className;
    private String semester;
    private String beginTime;
    private String classNo;
    private int bgColor;


    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassInfo)) return false;

        ClassInfo classInfo = (ClassInfo) o;

        if (!classRoom.equals(classInfo.classRoom)) return false;
        if (!teacherName.equals(classInfo.teacherName)) return false;
        if (!className.equals(classInfo.className)) return false;
        if (!semester.equals(classInfo.semester)) return false;
        return beginTime.equals(classInfo.beginTime);

    }

    @Override
    public int hashCode() {
        int result = classRoom.hashCode();
        result = 31 * result + teacherName.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + semester.hashCode();
        result = 31 * result + beginTime.hashCode();
        return result;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
