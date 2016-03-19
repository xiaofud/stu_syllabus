package com.hjsmallfly.syllabus.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.hjsmallfly.syllabus.interfaces.UpdateHandler;
import com.hjsmallfly.syllabus.parsers.VersionParser;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.SyllabusVersion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by STU_nwad on 2015/10/14.
 * 用于检查更新，下载更新的apk
 */
public class UpdateHelper {

    private Context context;
    private UpdateHandler updateHandler;
    private File downloaded_apk_file;

    private VersionParser version_parser;
    private SyllabusVersion cur_version;

    public UpdateHelper(Context context, UpdateHandler updateHandler){
        this.context = context;
        this.version_parser = new VersionParser(context);
        this.updateHandler = updateHandler;
        cur_version = get_current_version(context);
    }

    public static SyllabusVersion get_current_version(Context context){
        try {
            PackageInfo package_info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int version_code = package_info.versionCode;
            String version_name = package_info.versionName;
            SyllabusVersion version = new SyllabusVersion();
            version.version_code = version_code;
            version.version_name = version_name;
//            Toast.makeText(context, "版本号: " + version_code + " 版本名字: " + version_name , Toast.LENGTH_SHORT).show();
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void installAPK(){

        if (downloaded_apk_file != null && downloaded_apk_file.exists()){
            // 隐式的 intent
            Intent install_apk = new Intent(Intent.ACTION_VIEW);
            // 安装 apk 文件
            install_apk.setDataAndType(Uri.parse("file://" + downloaded_apk_file.toString()), "application/vnd.android.package-archive");
            context.startActivity(install_apk);
        }
    }

    public void check_for_update(){
        CheckUpdate task = new CheckUpdate(WebApi.get_server_address() + context.getString(R.string.version_control_address));
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void download(String address, SyllabusVersion version){
        Download task = new Download(address);
        task.execute(version);
    }

    /**
     * 用于检查是否有更新
     */
    class CheckUpdate extends AsyncTask<Void, Void, String>{

        private String address;

        public CheckUpdate(String address){
            this.address = address;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpCommunication.perform_get_call(address, 1000);
        }

        @Override
        protected void onPostExecute(String response){

            if (HttpCommunication.is_internet_flow_used_up()){
                response = "";
            }

            if (response.isEmpty()){
//                Toast.makeText(context, "无法访问服务器", Toast.LENGTH_SHORT).show();
                updateHandler.deal_with_update(UpdateHandler.CONNECTION_ERROR, null);
                return;
            }

            SyllabusVersion version = version_parser.parse_version(response);
            if (version != null){
                // 网站上的比现在的版本更新
                if (version.version_code > cur_version.version_code){
                    updateHandler.deal_with_update(UpdateHandler.EXIST_UPDATE, version);
                }else{
                    updateHandler.deal_with_update(UpdateHandler.ALREADY_UPDATED, version);
                }
            }
        }
    }

    /**
     * 下载文件
     */
    class Download extends AsyncTask<SyllabusVersion, Integer, File>{

        private String address;
        private ProgressDialog progressDialog;

        private boolean progress_first_time = true;
        private boolean cancel_download = false;

        public Download(String address){
            this.address = address;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(UpdateHelper.this.context);
            progressDialog.setMessage("下载完成的apk将保存在sd卡的download目录下(进度条单位是KB)");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // 不能有setIndeterminate(true), 不然进度条不会走
//            progressDialog.setIndeterminate(true);
//            progress.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel_download = true;
                    Toast.makeText(UpdateHelper.this.context, "下载被取消", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.d("debug", "after progress.show()");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progress_first_time) {
                // 最大值
                progressDialog.setMax(values[1]);
                progress_first_time = false;
            }
            progressDialog.setProgress(values[0]);
//            progressDialog.setMessage("数值的单位是KB");
            Log.d("progress_", "" + values[0]);
        }

        @Override
        protected File doInBackground(SyllabusVersion... params) {
            SyllabusVersion version = params[0];
            // SD 卡是否存在，和是否具有读写权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                String sd_path = Environment.getExternalStorageDirectory() + "/";
                String file_save_path = sd_path + "download";
                try {
                    URL download_url = new URL(address);
                    HttpURLConnection connection = (HttpURLConnection) download_url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.connect();
//                    Log.d("download", "the response code is " + connection.getResponseCode());
                    // 文件大小
                    int length = connection.getContentLength();
                    InputStream is = connection.getInputStream();

                    // 检查存文件的目录是否存在
                    File dir_path = new File(file_save_path);
                    if (!dir_path.exists())
                        dir_path.mkdir();

                    File apk_file = new File(file_save_path, version.apk_file_name);
                    FileOutputStream fos = new FileOutputStream(apk_file);

                    byte[] buf = new byte[1024 * 4];
                    // 记录已经下载的字节数
                    int count = 0;
                    // 写入到文件中
                    int numread;
                    Log.d("download", "开始下载文件");
                    int delay = 0;
                    while ((numread = is.read(buf)) > 0){
                        if (cancel_download){
                            fos.close();
                            is.close();
                            return null;
                        }
                        fos.write(buf, 0, numread);
                        ++delay;
                        count += numread;
                        // 不需要每下载一点数据就重新更新进度条,这样会影响效率
                        if (delay % 10 == 0 || count == length)
                            publishProgress(count / 1024, length / 1024);
                    }
                    // 下载完成
                    fos.close();
                    is.close();
                    return apk_file;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }else{  // 没有sd卡, 或者没有权限
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file){
            if (file == null){
                Toast.makeText(context, "文件下载失败", Toast.LENGTH_SHORT).show();
                downloaded_apk_file = null;
                return;
            }

            if (file.exists()){
                progressDialog.dismiss();
                Toast.makeText(context, "成功下载文件: " + file.toString(), Toast.LENGTH_SHORT).show();
                downloaded_apk_file = file;
                installAPK();
            }

        }
    }

}
