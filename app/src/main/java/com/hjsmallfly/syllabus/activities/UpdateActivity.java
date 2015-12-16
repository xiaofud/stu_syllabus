package com.hjsmallfly.syllabus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.ClipBoardHelper;
import com.hjsmallfly.syllabus.helpers.UpdateHelper;
import com.hjsmallfly.syllabus.interfaces.UpdateHandler;
import com.hjsmallfly.syllabus.syllabus.R;
import com.hjsmallfly.syllabus.syllabus.SyllabusVersion;

public class UpdateActivity extends AppCompatActivity implements UpdateHandler, View.OnClickListener {

    private int version_state;
    private UpdateHelper updateHelper;
    private SyllabusVersion remote_version;

    // views
    private TextView cur_version_text;
    private TextView new_version_text;
    private Button copy_button;
    private Button update_button;

    private void find_views(){
        cur_version_text = (TextView) findViewById(R.id.current_version_text);
        new_version_text = (TextView) findViewById(R.id.new_version_info);
        copy_button = (Button) findViewById(R.id.copy_download_address_button);
        update_button = (Button) findViewById(R.id.update_button);
    }

    private void setup_views(){
        // add listeners
        copy_button.setOnClickListener(this);
        update_button.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        updateHelper = new UpdateHelper(this, this);

        find_views();
        setup_views();

        get_current_version();
        check_update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void check_update(){
        updateHelper.check_for_update();
    }

    private void download_apk(String address){
        updateHelper.download(address, remote_version);
    }

    private void get_current_version(){
        SyllabusVersion cur_version =  UpdateHelper.get_current_version(this);
        String version_str;
        if (cur_version != null){
             version_str = "[当前版本]:\n版本名称: " + cur_version.version_name + "\n发布者: " + getString(R.string.publisher);
            String description = "没有描述信息!";
            if (cur_version.description != null)
                description = cur_version.description;
            version_str +=  "\n描述信息: " +  description;

        }else{
            version_str = "获取版本出错";
        }

        cur_version_text.setText(version_str);

    }

    /**
     * 用于检查更新工作
     * @param flag
     * @param version
     */
    @Override
    public void deal_with_update(int flag, SyllabusVersion version) {
        version_state = flag;
        remote_version = version;
        // 存在更新
        if (flag == UpdateHandler.EXIST_UPDATE){
            String new_version_info = "[新版本信息]:\n版本名称: " + version.version_name + "\n发布者: " + version.version_releaser + "\n描述: " + version.description;
            new_version_text.setText(new_version_info);
            remote_version = version;
            update_button.setText("发现新版本!快点我更新!");

            copy_button.setVisibility(View.VISIBLE);

        }else if (flag == UpdateHandler.ALREADY_UPDATED){
            new_version_text.setText("已经是最新版本啦!");
            remote_version = version;
            update_button.setText("已经是最新版本啦!");

            copy_button.setVisibility(View.VISIBLE);

        }else if (flag == UpdateHandler.CONNECTION_ERROR){
            new_version_text.setText("没有成功连接到服务器");
            remote_version = null;
            update_button.setText("点我重试检查更新");

            copy_button.setVisibility(View.GONE);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_button:
                if (version_state == UpdateHandler.EXIST_UPDATE){
                    if (remote_version != null)
                        download_apk(remote_version.dowload_address);
                    Toast.makeText(UpdateActivity.this, "开始下载文件", Toast.LENGTH_SHORT).show();
                }else if (version_state == UpdateHandler.ALREADY_UPDATED){
                    Toast.makeText(UpdateActivity.this, "已经是最新版本啦", Toast.LENGTH_SHORT).show();
                }else if (version_state == UpdateHandler.CONNECTION_ERROR){
                    Toast.makeText(UpdateActivity.this, "重新检查版本情况", Toast.LENGTH_SHORT).show();
                    check_update();
                }
                break;
            case R.id.copy_download_address_button:

                if (remote_version == null)
                    break;

                // copy to clipboard
                ClipBoardHelper.setContent(this, remote_version.dowload_address);
                String flash_message = "";
                if (version_state == UpdateHandler.ALREADY_UPDATED)
                    flash_message = "已经复制当前版本下载地址到剪贴板中";
                else if (version_state == UpdateHandler.EXIST_UPDATE)
                    flash_message = "已经复制新版本下载地址到剪贴板中";
                Toast.makeText(UpdateActivity.this, flash_message, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
