package com.hjsmallfly.syllabus.syllabus;

import java.io.Serializable;

/**
 * Created by daidaijie on 2015/12/9.
 */
public class StudentInfo implements Serializable {
    private String major;
    private String name;
    private String number;
    private String gender;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
