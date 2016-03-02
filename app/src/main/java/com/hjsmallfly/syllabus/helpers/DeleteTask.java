package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.interfaces.AfterDeleteHandler;
import com.hjsmallfly.syllabus.syllabus.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by smallfly on 2015/10/30.
 * 用于删除信息
 */
public class DeleteTask extends AsyncTask<HashMap<String, String>, Void, String> {
    public static final int DELETE_HOMEWORK = 0;
    public static final int DELETE_DISCUSSION = 1;

    public static final String ERROR_WRONG_TOKEN = "not authorized: wrong token";
    public static final String ERROR_NO_AUTHORIZED = "not authorized: no such user or user not match";

    private Context context;
    private AfterDeleteHandler afterDeleteHandler;
    private int type_;
    private int position;

    public DeleteTask(Context context, AfterDeleteHandler afterDeleteHandler, int type_, int position){
        this.context = context;
        this.afterDeleteHandler = afterDeleteHandler;
        this.type_ = type_;
        this.position = position;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        // 末尾加了 /
        String request_url = WebApi.get_server_address() + context.getString(R.string.delete_api);
        if (type_ == DELETE_HOMEWORK)
            request_url += DELETE_HOMEWORK;
        else if (type_ == DELETE_DISCUSSION)
            request_url += DELETE_DISCUSSION;
        try {
            String query_string = HttpCommunication.get_url_encode_string(params[0]);
            request_url += "?" + query_string;
//            Toast.makeText(context, "URL: " + request_url, Toast.LENGTH_SHORT).show();
            Log.d(MainActivity.TAG, "URL: " + request_url);
            return HttpCommunication.perform_delete_call(request_url, 2000);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String response){
        if (HttpCommunication.is_internet_flow_used_up()){
            response = "";
        }
        afterDeleteHandler.deal_with_delete(response, position);
    }

}
