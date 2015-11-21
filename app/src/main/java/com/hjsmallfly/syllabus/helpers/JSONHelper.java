package com.hjsmallfly.syllabus.helpers;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by smallfly on 2015/10/31.
 * 用于方便地处理JSON数据
 */
public class JSONHelper {

    public static final String ERROR = "ERROR";

    public static String check_and_get_error(String data){
        JSONTokener jsonTokener = new JSONTokener(data);
        try {
            JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
            if (jsonObject.has(ERROR)){
                return jsonObject.getString(ERROR);
            }else
                return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
