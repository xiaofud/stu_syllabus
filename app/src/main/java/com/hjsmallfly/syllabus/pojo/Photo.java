package com.hjsmallfly.syllabus.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smallfly on 16-3-26
 * 一张photo包含原图地址和缩略图地址
 */
public class Photo {
    @SerializedName("size_big")
    @Expose
    private String size_big;

    @SerializedName("size_small")
    @Expose
    private String size_small;

    public Photo(String size_big, String size_small){
        this.size_big = size_big;
        this.size_small = size_small;
    }

    public String getSize_big() {
        return size_big;
    }

    public void setSize_big(String size_big) {
        this.size_big = size_big;
    }

    public String getSize_small() {
        return size_small;
    }

    public void setSize_small(String size_small) {
        this.size_small = size_small;
    }
}
