package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hjsmallfly.syllabus.interfaces.ExamHandler;
import com.hjsmallfly.syllabus.parsers.ExamParser;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.HashMap;

/**
 * Created by smallfly on 15-11-23.
 */
public class ExamGetter extends AsyncTask<HashMap<String, String>, Void, String> {

    private Context context;
    private ExamHandler examHandler;

    public ExamGetter(Context context, ExamHandler examHandler){
        this.context = context;
        this.examHandler = examHandler;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        return HttpCommunication.performPostCall(WebApi.get_server_address() + context.getString(R.string.get_exam_list_api), params[0]);
    }

    @Override
    protected void onPostExecute(String raw_data){

        if (HttpCommunication.is_internet_flow_used_up()){
            raw_data = "";
        }
        if (raw_data.isEmpty()){
            Toast.makeText(context, "网络连接出错", Toast.LENGTH_SHORT).show();
            return;
        }



        String error = JSONHelper.check_and_get_error(raw_data);
        if (error != null){
            if (error.equals("no exams")){
                Toast.makeText(context, "还没有这个学期的考试安排", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(context, "出现错误: " + error, Toast.LENGTH_SHORT).show();
            return;
        }

        ExamParser parser = new ExamParser(context);
        Toast.makeText(context, "更新成功", Toast.LENGTH_SHORT).show();
        examHandler.deal_with_exam_list(parser.parse_exam_list(raw_data));

    }

}
