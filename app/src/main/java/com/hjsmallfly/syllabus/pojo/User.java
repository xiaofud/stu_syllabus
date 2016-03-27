package com.hjsmallfly.syllabus.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("account")
    @Expose
    public String account;
    @SerializedName("birthday")
    @Expose
    public String birthday;
    @SerializedName("gender")
    @Expose
    public Integer gender;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("nickname")
    @Expose
    public String nickname;
    @SerializedName("profile")
    @Expose
    public String profile;
}
