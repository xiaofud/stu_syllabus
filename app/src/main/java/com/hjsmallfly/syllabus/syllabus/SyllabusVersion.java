package com.hjsmallfly.syllabus.syllabus;

/**
 * Created by STU_nwad on 2015/10/14.
 * 版本信息
 */
public class SyllabusVersion {

//    {
//        "versionCode": "1",
//            "versionDate": 1444788160,
//            "versionDescription": "\u6dfb\u52a0\u81ea\u52a8\u66f4\u65b0\u529f\u80fd\n\u66f4\u6362\u58c1\u7eb8\u529f\u80fd\n\u8bbe\u7f6e\u9ed8\u8ba4\u8bfe\u8868\u529f\u80fd\n\u6307\u5b9a\u7f13\u5b58\u6587\u4ef6\u5220\u9664\u529f\u80fd",
//            "versionName": "ver 1.0",
//            "versionReleaser": "smallfly"
//    }

    public int version_code;
    public String version_name;
    public String description;  // 当前版本的描述
    public long version_release_date = -1;   // 发布日期
    public String version_releaser = "";  // 发布者
    public String dowload_address = "";
    public String apk_file_name = "";

    public SyllabusVersion(){
        version_code = -1;
        version_name = "";
        description = "";
    }

    public SyllabusVersion(int code, String name, String description){
        this.version_code = code;
        this.version_name = name;
        this.description = description;
    }

    @Override
    public String toString(){
        return version_name + "(" + version_code  + ")" + " " + description;
    }

}
