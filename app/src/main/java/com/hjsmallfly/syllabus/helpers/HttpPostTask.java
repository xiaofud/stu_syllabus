package com.hjsmallfly.syllabus.helpers;

import android.os.AsyncTask;

import com.hjsmallfly.syllabus.interfaces.PostDataGetter;

import java.util.HashMap;

/**
 * Created by smallfly on 15-11-27.
 */
public class HttpPostTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private String req_url;

    private PostDataGetter postDataGetter;

    public HttpPostTask(String req_url, PostDataGetter postDataGetter){
        this.req_url = req_url;
        this.postDataGetter = postDataGetter;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        return HttpCommunication.performPostCall(req_url, params[0]);
    }

    @Override
    protected void onPostExecute(String response){
        postDataGetter.handle_post_response(response);
    }

}
