package com.hjsmallfly.syllabus.parsers;

import android.content.Context;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.JSONHelper;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.syllabus.UserInformation;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by smallfly on 15-11-24.
 * 解析用户信息
 */
public class UserParser {

    private Context context;

    public UserParser(Context context){
        this.context = context;
    }

    public UserInformation parse_user(String raw_data){
        if (raw_data.isEmpty()){
            Toast.makeText(context, "网络连接错误!", Toast.LENGTH_SHORT).show();
            return null;
        }

        String error = JSONHelper.check_and_get_error(raw_data);
        if (error != null){
            if (error.equals("wrong token")){
                Toast.makeText(context, StringDataHelper.ERROR_TOKEN, Toast.LENGTH_SHORT).show();
                return null;
            }else if (error.equals("no such user")){
                Toast.makeText(context, "没有该用户", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        // 解析数据

        JSONTokener json_parser = new JSONTokener(raw_data);
        try {
            JSONObject user_obj = (JSONObject) json_parser.nextValue();
            String username;
            String nickname;
            username = user_obj.getString("user");
            if (user_obj.isNull("nickname"))
                nickname = null;
            else
                nickname = user_obj.getString("nickname");

            UserInformation user = new UserInformation();
            // 账号
            user.username = username;
            // 昵称
            user.nick_name = nickname;

            return user;

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "解析错误", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

}
