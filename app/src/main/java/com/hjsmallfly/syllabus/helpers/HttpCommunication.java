package com.hjsmallfly.syllabus.helpers;
import android.util.Log;

import com.hjsmallfly.syllabus.activities.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *  与服务器进行通信
 */
public class HttpCommunication {

    public static int timeout = 4000; // 4s

    /**
     * 访问远程网站，获取信息
     * @param hostaddr 远程地址
     * @param timeout 秒
     * @return "" 或者具体的内容
     */
    public static String perform_get_call(String hostaddr, int timeout){
        URL url;
        String response = "";
        Log.d(MainActivity.TAG, "地址是:" + hostaddr);
        try {
            url = new URL(hostaddr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);
            connection.setDoInput(true);
            connection.connect();
            int response_code = connection.getResponseCode();
            if (response_code == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response += line;
                }
                Log.d(MainActivity.TAG, "GET CALL OK");

            }
            else {
                response = "";
                Log.d(MainActivity.TAG, "GET CALL BAD");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String perform_delete_call(String address, int timeout){
        URL url;
        String response = "";
        try {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setDoInput(true);
            connection.setRequestMethod("DELETE");
            connection.connect();
            int response_code = connection.getResponseCode();
            if (response_code == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response += line;
                }
            }else{
                response = "";
            }
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            Log.d(MainActivity.TAG, "地址是:" + requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            Log.d(MainActivity.TAG, "start writing data");
            writer.write(get_url_encode_string(postDataParams));
            Log.d(MainActivity.TAG, "writer.write()");
            writer.flush();
            Log.d(MainActivity.TAG, "writer.flush()");
            writer.close();
            Log.d(MainActivity.TAG, "writer.close()");
            os.close();
            Log.d(MainActivity.TAG, "outputstream has closed!");
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                Log.d(MainActivity.TAG, "POST CALL OK");
            }
            else {
                response="";
                Log.d(MainActivity.TAG, "POST CALL BAD");

            }
        } catch (Exception e) {
            Log.d(MainActivity.TAG, e.toString());
        }

        return response;
    }

    public static String get_url_encode_string(HashMap<String, String> params) throws UnsupportedEncodingException {
        Log.d(MainActivity.TAG, "get_url_encode_string");
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
//        Log.d(MainActivity.TAG, result.toString());
        return result.toString();
    }

}
