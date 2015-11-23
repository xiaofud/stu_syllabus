package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import com.hjsmallfly.syllabus.interfaces.InsertHandler;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.HashMap;

/**
 * Created by smallfly on 15-11-23.
 * 用于发布讨论数据
 */
public class InsertTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private Context context;
    private InsertHandler insert_handler;

    public InsertTask(Context context, InsertHandler handler){
        this.context = context;
        this.insert_handler = handler;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        // 新版本的api
        return HttpCommunication.performPostCall(WebApi.get_server_address() + context.getString(R.string.insert_discussion_api_v1),
                params[0]);

    }

    @Override
    protected void onPostExecute(String raw_data){
        insert_handler.deal_with_insert_result(raw_data);
    }
}
