package com.hjsmallfly.syllabus.syllabus;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smallfly on 16-3-4.
 *
 */
public class Banner {

    private String url;  // 图片的url地址
    private String description;  // 描述
    private String go_to_link;   // 点击之后的跳转
    private int id; // 图片位置
    private long timestamp; // 这一组照片的时间戳

    private Banner(){

    }

    /*
    {
  "latest": {
    "notifications": [
      {
        "description": "",
        "id": 1,
        "link": "http://ceqjo.img47.wal8.com/img47/538344_20160304112228/145706357503.jpg",
        "url": "http://ceqjo.img47.wal8.com/img47/538344_20160304112228/145706357503.jpg"
      },
      {
        "description": "",
        "id": 2,
        "link": "http://ceqjo.img47.wal8.com/img47/538344_20160304112228/145706357404.jpg",
        "url": "http://ceqjo.img47.wal8.com/img47/538344_20160304112228/145706357404.jpg"
      },
      {
        "description": "",
        "id": 3,
        "link": "http://ceqjo.img47.wal8.com/img47/538344_20160304112228/145706357236.jpg",
        "url": "http://ceqjo.img47.wal8.com/img47/538344_20160304112228/145706357236.jpg"
      }
    ],
    "timestamp": 1457074135
  }
}
     */


    public static List<Banner> parse(String json){
        JSONTokener jsonTokener = new JSONTokener(json);
        ArrayList<Banner> banners = new ArrayList<>();
        try {
            JSONObject banner_obj = (JSONObject) jsonTokener.nextValue();
            JSONObject latest = banner_obj.getJSONObject("latest");
            long timestamp = latest.getLong("timestamp");
            JSONArray photo_array = latest.getJSONArray("notifications");
            for(int i = 0 ; i < photo_array.length() ; ++i){
                JSONObject banner_json_obj = photo_array.getJSONObject(i);
                Banner banner = new Banner();
                banner.setTimestamp(timestamp);
                banner.setDescription(banner_json_obj.getString("description"));
                banner.setGo_to_link(banner_json_obj.getString("link"));
                banner.setId(banner_json_obj.getInt("id"));
                banner.setUrl(banner_json_obj.getString("url"));

                banners.add(banner);
            }
            return banners;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Banner", e.getMessage());
            return null;
        }
    }

    public String getName(){
        int index = url.lastIndexOf("/");
        if (index != -1)
            return url.substring(index);
        else
            return null;
    }

    public static List<String> toFilenames(List<Banner> banners){
        List<String> filenames = new ArrayList<>();
        for(int i = 0 ; i < banners.size() ; ++i){
            Banner banner = banners.get(i);
            String name = banner.getName();
            Log.d("banner", banner.getName());
            filenames.add(name);
        }
        return filenames;
    }

    public static List<String> toUrls(List<Banner> banners){
        List<String> urls = new ArrayList<>();
        if (banners == null)
            return urls;
        for(int i = 0 ; i < banners.size() ; ++i){
            Banner banner = banners.get(i);
            urls.add(banner.getUrl());
            Log.d("banner", banner.getUrl());
        }
        return urls;
    }

    public static long getTimestap(String banner_json){
        JSONTokener jsonTokener = new JSONTokener(banner_json);
        try {
            JSONObject banner_obj = (JSONObject) jsonTokener.nextValue();
            JSONObject latest = banner_obj.getJSONObject("latest");
            return latest.getLong("timestamp");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Banner", e.getMessage());
            return -1;
        }
    }

    // -----------getters and setters-------------
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGo_to_link() {
        return go_to_link;
    }

    public void setGo_to_link(String go_to_link) {
        this.go_to_link = go_to_link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // -----------getters and setters-------------
}
