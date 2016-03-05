package com.hjsmallfly.syllabus.helpers;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by smallfly on 16-3-5.
 * 下载URL指定的资源文件
 * 记得加入需要的权限哟
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 */

public class URLDownloader {

    /**
     * 只有成功下载的文件会添加到List里面
     * @param urls
     * @param directory
     * @param filenames
     * @param timeout_ms
     * @return
     */
    public static List<File> download_multiple(List<String> urls, String directory, List<String> filenames, int timeout_ms){
        ArrayList<File> files = new ArrayList<>();
        for(int i = 0 ; i < urls.size() ; ++i){
            File file = download(urls.get(i), directory, filenames.get(i), timeout_ms);
            if (file != null)
                files.add(file);
        }
        return files;
    }

    public static File download(String url, String directory, String filename, int timeout_ms){
        // 检查sd卡的状态
//        Storage state if the media is present and mounted at its mount point with
//        read/write access
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("download", "sdcard not mounted");
            return null;
        }
        // sd 卡根目录
        String sdCardRoot = Environment.getExternalStorageDirectory() + File.separator;
        Log.d("download", "sdCardRoot is: " + sdCardRoot);
        // 文件存储目录
        String file_save_path = sdCardRoot + directory;
        Log.d("download", "file_save_path is: " + file_save_path);
        try {
            URL download_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) download_url.openConnection();
            connection.setConnectTimeout(timeout_ms);
            connection.connect();
            Log.d("download", "the response code is " + connection.getResponseCode());
            // 文件大小
            int length = connection.getContentLength();
            Log.d("download", "content length: " + length);
            InputStream is = connection.getInputStream();

            // 检查存文件的目录是否存在
            File dir_path = new File(file_save_path);
            if (!dir_path.exists()) {
                if (dir_path.mkdir()) {
                    Log.d("download", "建立文件夹失败");
                }
            }
            File file = new File(file_save_path, filename);
            Log.d("download", "filepath is: " + file.toString());
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024 * 4];
            // 记录已经下载的字节数
            int count = 0;
            // 写入到文件中
            int bytes_read;
            Log.d("download", "开始下载文件");
            while ((bytes_read = is.read(buf)) > 0) {
                fos.write(buf, 0, bytes_read);
                count += bytes_read;
            }
            // 下载完成
            fos.flush();
            fos.close();
            is.close();
            return file;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
