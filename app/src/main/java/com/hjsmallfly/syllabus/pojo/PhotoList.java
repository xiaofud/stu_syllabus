package com.hjsmallfly.syllabus.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PhotoList{

    @SerializedName("photo_list")
    @Expose
    public List<Photo> photo_list = new ArrayList<>();

    /**
     *
     * @return  缩略图
     */
    public List<String> get_thumbnails(){
        List<String> thumbnails = new ArrayList<>();
        for(int i = 0 ; i < photo_list.size() ; ++i){
            thumbnails.add(photo_list.get(i).getSize_small());
        }
        return thumbnails;
    }

    /**
     *
     * @return 得到原图 urls
     */
    public List<String> get_photos(){
        List<String> photos = new ArrayList<>();
        for(int i = 0 ; i < photo_list.size() ; ++i){
            photos.add(photo_list.get(i).getSize_big());
        }
        return photos;
    }

}
