package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hjsmallfly.syllabus.interfaces.GradeHandler;
import com.hjsmallfly.syllabus.parsers.GradeParser;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.HashMap;

/**
 * Created by smallfly on 15-11-21.
 * 用于获取成绩信息
 */
public class GradeGetter extends AsyncTask<HashMap<String, String>, Void, String> {

    private GradeHandler handler;
    private Context context;

    public GradeGetter(Context context, GradeHandler handler){
        this.context = context;
        this.handler = handler;
    }


    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        String address = WebApi.get_server_address() + context.getString(R.string.get_grades_api);
        return HttpCommunication.performPostCall(address, params[0]);
    }

    @Override
    protected void onPostExecute(String raw_json_data){
        GradeParser parser = new GradeParser(context);
        if (!raw_json_data.isEmpty())
            Toast.makeText(context, "更新成绩成功: )", Toast.LENGTH_SHORT).show();
        handler.handle_grade_list(parser.parse(raw_json_data));
    }

}
