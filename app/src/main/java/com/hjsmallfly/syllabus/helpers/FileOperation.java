package com.hjsmallfly.syllabus.helpers;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.activities.PersonalInfoActivity;
import com.hjsmallfly.syllabus.activities.SyllabusActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 处理文件的存储与读取
 */
public class FileOperation {

    public static final String APP_FOLDER = "Syllabus";

    public static String get_app_folder(boolean with_slash){
        if (with_slash)
            return Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/";
        else
            return Environment.getExternalStorageDirectory() + "/" + APP_FOLDER;
    }

    public static boolean is_sd_mounted(){
        String status = Environment.getExternalStorageState();
        // sd 卡装载好 而且有读写权限。
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean create_app_folder(){
        if (is_sd_mounted()) {
            String base_dir = Environment.getExternalStorageDirectory() + "/" + APP_FOLDER;
            File dir = new File(base_dir);
            if (!dir.exists())
                return dir.mkdir();
            else
                return true;
        }
        return false;
    }

    public static boolean copy_file(File from, File to){
        try {
            InputStream in = new FileInputStream(from);
            OutputStream out = new FileOutputStream(to);
            byte[] buf = new byte[1024];
            int numread;
            while ((numread = in.read(buf)) > 0){
                out.write(buf, 0, numread);
            }
            in.close();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static Uri getTempUri(){
        String status = Environment.getExternalStorageState();
        // 已挂载而且有读写权限
        if (status.equals(Environment.MEDIA_MOUNTED)){
            File f = new File(get_app_folder(true)+ SyllabusActivity.WALL_PAPER_FILE_TEMP);
            try {
                f.createNewFile();
                return Uri.fromFile(f);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static Uri get_avatar_uri() {
        if (is_sd_mounted()){
            File f = new File(get_app_folder(true) + PersonalInfoActivity.AVATAR_FILE_NAME);
            if (!f.exists())
                try {
                    if (f.createNewFile()){
                        return Uri.fromFile(f);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            return Uri.fromFile(f);
        }else{
            return null;
        }
    }


    public static boolean hasFile(File file){
        if (file.exists())
            return true;
        return false;
    }

    public static boolean hasFile(Context context, String filename){
        File file = new File(context.getFilesDir(), filename);
        Log.d(MainActivity.TAG, file.toString());
        return hasFile(file);
    }

    /**
     * 保存最新的用户名和密码
     */
    public static boolean save_user(Context context, String user_file, String password_file, String username, String passwd){
        boolean flag;
        flag = save_to_file(context, user_file, username);
        if (flag){
            flag = save_to_file(context, password_file, passwd);
        }
        if (flag){
            // 保存成功
            Log.d(MainActivity.TAG, "成功保存用户");
        }else{
            Log.d(MainActivity.TAG, "保存用户失败");
        }
        return flag;
    }

    public static String[] load_user(Context context, String user_file, String password_file){
        if (hasFile(context, user_file) && hasFile(context, password_file)){
            String[] user = new String[2];
            user[0] = read_from_file(context, user_file);
            user[1] = read_from_file(context, password_file);
            return user;
        }
        return null;
    }

    public static boolean save_to_file(Context context, String filename, String data){
        try{
            Log.d(MainActivity.TAG, "saving " + filename);
            FileOutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            out.write(data.getBytes("UTF-8"));
            out.flush();
            out.close();
            return true;
        }catch (FileNotFoundException e){
            Log.d(MainActivity.TAG, e.toString());
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取 filename 里面的字符串, 失败返回null
     * @param context   上下文对象
     * @param filename  文件名
     * @return  文件内容或者null
     */
    public static String read_from_file(Context context, String filename){
        try {
            FileInputStream inStream = context.openFileInput(filename);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while( (length=inStream.read(buffer)) != -1)   {
                stream.write(buffer, 0, length);  // 写入字节流中
            }
            stream.close();
            inStream.close();
            String data = stream.toString();
//            Toast.makeText(MainActivity.this, "读取" + filename, Toast.LENGTH_SHORT).show();
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean delete_file(Context context, String filename){
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        return file.delete();

    }

    public static String generate_week_file(String username, String year_string, String semester_str){
        return username + "_" + year_string + "_" + semester_str + "_week_info.txt";
    }


}
