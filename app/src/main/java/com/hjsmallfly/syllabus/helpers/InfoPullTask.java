package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.interfaces.DiscussionHandler;
import com.hjsmallfly.syllabus.interfaces.HomeworkHandler;
import com.hjsmallfly.syllabus.parsers.HomeworkParser;
import com.hjsmallfly.syllabus.syllabus.Discussion;
import com.hjsmallfly.syllabus.parsers.DiscussionParser;
import com.hjsmallfly.syllabus.syllabus.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/10/12.
 */
public class InfoPullTask extends AsyncTask<HashMap<String, String>, Void, String> {

    public static final int PULL_HOMEWORK = 0;
    public static final int PULL_DISCUSSION = 1;

    private int task_type;
    private String host_address;
    private Context context;

    private HomeworkHandler homeworkHandler;
    private DiscussionHandler discussionHandler;

    public void setHomeworkHandler(HomeworkHandler homeworkHandler){this.homeworkHandler = homeworkHandler;}
    public void setDiscussionHandler(DiscussionHandler discussionHandler){this.discussionHandler = discussionHandler;}

    private void get_address(){
        switch (task_type){
            case PULL_HOMEWORK:
                host_address = WebApi.get_server_address() +  context.getString(R.string.get_home_work_api);
                break;
            case PULL_DISCUSSION:
                host_address = WebApi.get_server_address() + context.getString(R.string.get_discussion_api);
                break;
            default:
                host_address = "";  // 意味着是错的
                break;
        }
    }

    public InfoPullTask(Context context, int type_){
        this.context = context;
        this.task_type = type_;
        get_address();
    }

    public void get_information(int count, String class_number, int start_year, int end_year, int semester){

        HashMap<String, String> data = new HashMap<>();
        data.put("number", class_number);
        data.put("start_year", start_year + "");
        data.put("end_year", end_year + "");
        data.put("semester", semester + "");
        data.put("count", count + "");

        execute(data);

    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        try {
            String query_string = HttpCommunication.get_url_encode_string(params[0]);
            Log.d(MainActivity.TAG, query_string);
            return HttpCommunication.perform_get_call(host_address + "?" + query_string, 3000);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String response){
        if (homeworkHandler != null){
            HomeworkParser homeworkParser = new HomeworkParser(context);
            homeworkHandler.deal_with_homework(homeworkParser.parser_json(response));
            return;
        }

        if (discussionHandler != null){
            DiscussionParser discussionParser = new DiscussionParser(context);
            ArrayList<Discussion> all_discussions = discussionParser.parse_json(response);
            if (all_discussions != null){
                Collections.reverse(all_discussions);
            }
            discussionHandler.deal_with_discussion(all_discussions);
            return;
        }
    }
}
