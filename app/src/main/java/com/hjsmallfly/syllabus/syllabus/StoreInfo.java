package com.hjsmallfly.syllabus.syllabus;

import android.view.Menu;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by daidaijie on 2016/3/26.
 */
public class StoreInfo implements Serializable {

    private String name;
    private String condition;
    private String long_number;
    private String short_number;
    private Map<String, List<MenuInfo>> menuList;

    public Map<String, List<MenuInfo>> getMenuList() {
        return menuList;
    }

    public void setMenuList(Map<String, List<MenuInfo>> menuList) {
        this.menuList = menuList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLong_number() {
        return long_number;
    }

    public void setLong_number(String long_number) {
        this.long_number = long_number;
    }

    public String getShort_number() {
        return short_number;
    }

    public void setShort_number(String short_number) {
        this.short_number = short_number;
    }


}
