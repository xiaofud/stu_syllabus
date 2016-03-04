package com.hjsmallfly.syllabus.helpers;

import android.os.AsyncTask;

import com.hjsmallfly.syllabus.interfaces.BannerHandler;

/**
 * Created by smallfly on 16-3-4.
 */
public class BannerGetter extends AsyncTask<String, Void, String> {

    private BannerHandler bannerHandler;

    public BannerGetter(BannerHandler handler){
        this.bannerHandler = handler;
    }

    @Override
    protected String doInBackground(String... params) {
//        Log.d("BannerGetter", params[0]);
        return HttpCommunication.perform_get_call(params[0], 4000);
    }

    @Override
    protected void onPostExecute(String s) {
        bannerHandler.handle_get_response(s);
    }
}
