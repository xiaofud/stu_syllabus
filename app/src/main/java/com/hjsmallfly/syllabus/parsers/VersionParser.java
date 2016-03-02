package com.hjsmallfly.syllabus.parsers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hjsmallfly.syllabus.syllabus.SyllabusVersion;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by STU_nwad on 2015/10/14.
 * 解析远程服务器上关于 app 的版本信息
 */
public class VersionParser {

    private Context context;

    public VersionParser(Context context){
        this.context = context;
    }


    public  SyllabusVersion parse_version(String json_data){
//        if(json_data.isEmpty()) {
//            Toast.makeText(context, "读取的信息是空的", Toast.LENGTH_SHORT).show();
//            return null;
//        }
//        Log.d("UPDATE", json_data);
        if (json_data.contains("流量")){
            // 坑爹，如果流量已经用完的话 用外网连接 就会发生这个问题
            Toast.makeText(context, "检查更新的时候发现: 校内流量已经用完-_-!", Toast.LENGTH_SHORT).show();
            return null;
        }
        JSONTokener json_parser = new JSONTokener(json_data);
        try {
            JSONObject version_json = (JSONObject) json_parser.nextValue();
            // 版本号
            int version_code = version_json.getInt("versionCode");
            // 版本名
            String version_name = version_json.getString("versionName");
            // 描述
            String description = version_json.getString("versionDescription");
            // 发布者
            String version_publisher = version_json.getString("versionReleaser");
            // 发布日期
            long pub_date = version_json.getLong("versionDate");
            // 下载地址
            String download = version_json.getString("download_address");
            // 文件名
            String file_name = version_json.getString("apk_file_name");

            SyllabusVersion version = new SyllabusVersion(version_code, version_name, description);
            version.version_releaser = version_publisher;
            version.version_release_date = pub_date;
            version.dowload_address = download;
            version.apk_file_name = file_name;
            return version;
        } catch (JSONException e) {
            Toast.makeText(context, "版本信息解析失败", Toast.LENGTH_SHORT).show();
            Log.d("parse_version", e.toString());
            e.printStackTrace();
            return null;
        }
    }

}
