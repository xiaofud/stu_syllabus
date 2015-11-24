package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;

import com.hjsmallfly.syllabus.interfaces.UserAlterHandler;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.HashMap;

/**
 * Created by smallfly on 15-11-24.
 * 用于修改用户信息
 */
public class UserInfoAlter extends AsyncTask<HashMap<String, String>, Void, String> {

    private Context context;
    // 处理尝试修改后返回的信息
    private UserAlterHandler handler;

    public UserInfoAlter(Context context, UserAlterHandler handler){
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        String req_url = WebApi.get_server_address() + context.getString(R.string.change_user_information_api);
        return HttpCommunication.performPostCall(req_url, params[0]);
    }

    @Override
    protected void onPostExecute(String response){
        handler.handle_user_alter(response);
    }
}
