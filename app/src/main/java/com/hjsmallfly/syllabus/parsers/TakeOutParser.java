package com.hjsmallfly.syllabus.parsers;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;


import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.syllabus.MenuInfo;
import com.hjsmallfly.syllabus.syllabus.StoreInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daidaijie on 2016/3/26.
 */
public class TakeOutParser {


    public static String getJsonData(Activity content, String jsonFileName) {
        StringBuffer jsonData = new StringBuffer();
        try {
            InputStreamReader isr = new InputStreamReader(content.getResources().getAssets().open(jsonFileName));
            BufferedReader br = new BufferedReader(isr);
            char[] tempChar = new char[1024];
            int charRead = 0;

            while ((charRead = br.read(tempChar)) != -1) {
                jsonData.append(tempChar);
            }
            return jsonData.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static StoreInfo every_parser(Activity content, String jsonFileName) throws JSONException {
        String jsonData = getJsonData(content, jsonFileName);
        StoreInfo storeInfo = new StoreInfo();

        JSONObject main_data = new JSONObject(jsonData);

        storeInfo.setName(main_data.getString("name"));
        storeInfo.setCondition(main_data.getString("condition"));
        storeInfo.setLong_number(main_data.getString("long_number"));
        storeInfo.setShort_number(main_data.getString("short_number"));

        JSONArray main_menu = main_data.getJSONArray("menu");
        HashMap<String, List<MenuInfo>> menuList = new HashMap<>();
        for (int i = 0; i < main_menu.length(); i++) {
            JSONObject sub_menu = main_menu.getJSONObject(i);
            String subMenuName = sub_menu.getString("sub_menu");

            JSONArray sub_menu_list = sub_menu.getJSONArray("sub_list");

            List<MenuInfo> subMenuList = new ArrayList<>();

            for (int j = 0; j < sub_menu_list.length(); j++) {
                MenuInfo menuInfo = new MenuInfo();
                JSONObject menu_info = sub_menu_list.getJSONObject(j);
                menuInfo.setDish(menu_info.getString("dist"));
                menuInfo.setPrice(menu_info.getString("price"));
                subMenuList.add(menuInfo);
            }

            menuList.put(subMenuName, subMenuList);
        }
        storeInfo.setMenuList(menuList);
        return storeInfo;

    }

    public static List<StoreInfo> parser(Activity content) {

        String jsonData = getJsonData(content, "main_menu.json");
        List<StoreInfo> storeInfoList = new ArrayList<>();

        try {
            JSONArray menuJson = new JSONArray(jsonData);
            for (int i = 0; i < menuJson.length(); i++) {
                String jsonFileName = menuJson.get(i).toString();
                storeInfoList.add(every_parser(content, jsonFileName));
                /*Toast.makeText(content, storeInfoList.get(i).getName() + "\n"
                        + storeInfoList.get(i).getCondition() + "\n"
                        + storeInfoList.get(i).getLong_number() + "\n"
                        + storeInfoList.get(i).getShort_number(), Toast.LENGTH_LONG).show();

                Map<String, List<MenuInfo>> infoMap = storeInfoList.get(i).getMenuList();
                StringBuilder sb = new StringBuilder();
                for (String key : infoMap.keySet()) {
                    sb.append(key + "\n");
                }
                Toast.makeText(content, sb.toString(), Toast.LENGTH_LONG).show();*/

            }
            return storeInfoList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

//        Toast.makeText(content, storeInfoList.get(0).getName(), Toast.LENGTH_LONG).show();

//        Toast.makeText(content,jsonData+"hahaha",Toast.LENGTH_LONG).show();
    }


}
