package com.hjsmallfly.syllabus.parsers;

import com.hjsmallfly.syllabus.syllabus.OAObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smallfly on 15-11-27.
 */
public class OAParser {

    /*
    "DOCUMENTS": [
    {
      "date": "2015-11-27",
      "department": "\u7406\u5b66\u9662",
      "title": "2015\u5e74\u6c55\u5934\u5927\u5b66\u6570\u503c\u4ee3\u6570\u9ad8\u7ea7\u7814\u8ba8\u4f1a\u901a\u77e5",
      "url": "http://notes.stu.edu.cn/page/maint/template/news/newstemplateprotal.jsp?templatetype=1&templateid=3&docid=4968"
    },
    {
     */

    public List<OAObject> parse_oa(String raw_data){
        if (raw_data.isEmpty()){
            return null;
        }

        JSONTokener jsonTokener = new JSONTokener(raw_data);
        List<OAObject> all_oa = new ArrayList<>();
        try {
            JSONObject outter_most_obj = (JSONObject) jsonTokener.nextValue();
            JSONArray document_array = outter_most_obj.getJSONArray("DOCUMENTS");
            for(int i = 0 ; i < document_array.length() ; ++i){
                JSONObject oa_json = (JSONObject) document_array.get(i);
                OAObject oaObject = new OAObject();
                oaObject.title = oa_json.getString("title");
                oaObject.date = oa_json.getString("date");
                oaObject.url = oa_json.getString("url");
                oaObject.department = oa_json.getString("department");
                all_oa.add(oaObject);
            }
            return all_oa;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
