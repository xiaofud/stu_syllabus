package com.hjsmallfly.syllabus.interfaces;

import com.hjsmallfly.syllabus.syllabus.UserInformation;

/**
 * Created by smallfly on 15-11-24.
 * 用于处理用户信息
 */
public interface UserHandler {
    void handle_user(UserInformation userInformation);
}
