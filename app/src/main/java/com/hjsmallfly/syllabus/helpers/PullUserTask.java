package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hjsmallfly.syllabus.interfaces.UserHandler;
import com.hjsmallfly.syllabus.parsers.UserParser;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.UserInformation;

import java.util.HashMap;

/**
 * Created by smallfly on 15-11-24.
 * 从服务器拉取用户数据
 */

public class PullUserTask extends AsyncTask<HashMap<String, String>, Void, String> {

    public static final String USER_TASK_TAG = "user_task";

    private Context context;
    private UserHandler handler;

    public PullUserTask(Context context, UserHandler handler){
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        String query_string = "?" + "token=" + params[0].get("token");
        String req_url = WebApi.get_server_address()
                + context.getString(R.string.get_user_information_api)
                + params[0].get("username")
                + query_string;
//        Toast.makeText(context, req_url, Toast.LENGTH_SHORT).show();
        Log.d(USER_TASK_TAG, req_url);
                                                // 注意超时单位是　毫秒
        return HttpCommunication.perform_get_call(req_url, 5000);
    }

    @Override
    protected void onPostExecute(String raw_user_data){
        // 解析数据
        Log.d(USER_TASK_TAG, "原始信息是: " +  raw_user_data);
        UserParser parser = new UserParser(context);
        UserInformation user =  parser.parse_user(raw_user_data);
        handler.handle_user(user);
    }

}
