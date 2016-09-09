package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by smallfly on 15-12-23.
 * 用于连接到校园网
 */
public class InternetLogin {

    /**
     * 用于登录和注销的API地址
     */
    public static String REQUEST_URL = "http://1.1.1.2/ac_portal/login.php";

    private Context context;
    private String username;
    private String password;

    private InternetLogin(Context context, String username, String password){
        this.context = context;
        this.username = username;
        this.password = password;
    }

    public static void login_to_internet(Context context, String username, String password){
        InternetLogin login = new InternetLogin(context, username, password);

        LoginTask loginTask = login.new LoginTask();
        loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LoginTask extends AsyncTask<Void, Void, String>{



        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> post_data = new HashMap<>();
            post_data.put("opr", "pwdLogin");
            post_data.put("userName", InternetLogin.this.username);
            post_data.put("pwd", InternetLogin.this.password);
            post_data.put("rememberPwd", "0");

            return HttpCommunication.performPostCall(InternetLogin.REQUEST_URL, post_data);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("newFlux", s);
            try {
                JSONObject loginObject = new JSONObject(s);
//                boolean success = loginObject.getBoolean("success");
                String msg = loginObject.getString("msg");
                Toast.makeText(InternetLogin.this.context, msg, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(InternetLogin.this.context, "网络连接错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}
