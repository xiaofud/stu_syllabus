package com.hjsmallfly.syllabus.syllabus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smallfly on 16-5-7.
 * 对应于每一个课程格子
 */
public class ClassGrid {

    private List<Lesson> class_in_the_grid; // 存放这个格子上有的课程

    public ClassGrid(){
        class_in_the_grid = new ArrayList<>();
    }


    /**
     * 添加课程
     * @param lesson
     */
    public void addClass(Lesson lesson){
        this.class_in_the_grid.add(lesson);
    }

    /**
     * 根据下标取得课程
     * @param index
     * @return
     */
    public Lesson getClassAt(int index){
        return class_in_the_grid.get(index);
    }

}
