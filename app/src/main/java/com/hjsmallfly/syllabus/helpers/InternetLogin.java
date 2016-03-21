package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by smallfly on 15-12-23.
 * 用于连接到校园网
 */
public class InternetLogin {
//    request_url = "http://192.168.31.4:8080/?url="
//    postdata = urllib.parse.urlencode({
//        "AuthenticateUser": user,
//                "AuthenticatePassword": passwd,
//                "Submit": ""
//    })

    public static String REQUEST_URL = "http://192.168.31.4:8080/?url=";

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
            post_data.put("AuthenticateUser", InternetLogin.this.username);
            post_data.put("AuthenticatePassword", InternetLogin.this.password);
            post_data.put("Submit", "");

            return HttpCommunication.performPostCall(InternetLogin.REQUEST_URL, post_data);
        }

        @Override
        protected void onPostExecute(String s) {
//            Log.d("inet", "response: " + s);
            if (s.isEmpty()){
                Toast.makeText(InternetLogin.this.context, "请确保接入校园网以及密码和账号的匹配", Toast.LENGTH_SHORT).show();
                return;
            }

            String lower_case = s.toLowerCase();
            if (lower_case.contains("used bytes")){
//                Log.d("inet", "已接入外网");
                // 截取数据
                String begin = "id=\"ub\"";
                String end = "</td>";
                String used_bytes_string = null;
                int begin_index = lower_case.indexOf(begin);
                if (begin_index != -1){
                    int end_index = lower_case.indexOf(end, begin_index);
                    if (end_index != -1){
                        used_bytes_string = lower_case.substring(begin_index + begin.length() + 1, end_index);
                        used_bytes_string = used_bytes_string.replace(",", "");
                    }
                }

                int used_mb = -1;
                if (used_bytes_string != null){
                    used_mb = Integer.parseInt(used_bytes_string) / (1024 * 1024);
                }

                if (used_mb != -1)
                    Toast.makeText(InternetLogin.this.context, "已接入外网 已使用流量" + used_mb + "MB", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(InternetLogin.this.context, "已接入外网 " + used_bytes_string, Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

}
