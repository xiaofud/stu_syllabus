package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.adapters.OAAdapter;
import com.hjsmallfly.syllabus.helpers.HttpPostTask;
import com.hjsmallfly.syllabus.helpers.JSONHelper;
import com.hjsmallfly.syllabus.helpers.StringDataHelper;
import com.hjsmallfly.syllabus.helpers.WebApi;
import com.hjsmallfly.syllabus.interfaces.PostDataGetter;
import com.hjsmallfly.syllabus.parsers.OAParser;
import com.hjsmallfly.syllabus.syllabus.OAObject;
import com.hjsmallfly.syllabus.syllabus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OAActivity extends AppCompatActivity  implements PostDataGetter, View.OnClickListener, AdapterView.OnItemClickListener {

    // 当前点击的OA_URL的地址
    public static OAObject CUR_OA_OBJECT;

    private List<OAObject> all_oa;
    private OAAdapter oa_adapter;

    // 默认是1
    private int current_page = 1;

    // 控件

    private ListView oa_list_view;
    private EditText page_number_edit;
    private Button go_to_page_button;
    private Button pre_page_button;
    private Button next_page_button;

    private void find_views(){
        oa_list_view = (ListView) findViewById(R.id.oa_list_view);
        page_number_edit = (EditText) findViewById(R.id.oa_page_index_edit);
        go_to_page_button = (Button) findViewById(R.id.oa_go_to_page_button);
        pre_page_button = (Button) findViewById(R.id.oa_pre_page_button);
        next_page_button = (Button) findViewById(R.id.oa_next_page_button);
    }

    private void setup_views(){
        // 一定要记得设置初始值
        page_number_edit.setText(current_page + "");

        // 监听器
        go_to_page_button.setOnClickListener(this);
        pre_page_button.setOnClickListener(this);
        next_page_button.setOnClickListener(this);

        oa_list_view.setOnItemClickListener(this);

        // debug
//        page_number_edit.setOnClickListener(this);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oa);
        find_views();
        setup_views();
        send_oa_request(current_page);
        set_page_edit_index(current_page);
    }

    private void set_page_edit_index(int index){
        String num_str = index + "";
        page_number_edit.setText(index + "");
        page_number_edit.setSelection(num_str.length());
    }

    private void go_to_page(){
        // 参数检查
        String page_str = page_number_edit.getText().toString();
        if (page_str.isEmpty()){
            Toast.makeText(OAActivity.this, "页码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        int page_index = Integer.parseInt(page_str);
        if (page_index == 0) {
            Toast.makeText(OAActivity.this, "页码要大于0", Toast.LENGTH_SHORT).show();
            return;
        }
        send_oa_request(page_index);
    }

    private void next_page(){
        // 这个与用户输入的页码是分开的
        current_page += 1;
        send_oa_request(current_page);
//        page_number_edit.setText(current_page + "");
        set_page_edit_index(current_page);
    }

    private void pre_page(){
        if (current_page - 1 <= 0){
            Toast.makeText(OAActivity.this, "已经是最前页啦", Toast.LENGTH_SHORT).show();
            return;
        }

        current_page -= 1;
        send_oa_request(current_page);
//        page_number_edit.setText(current_page + "");
        set_page_edit_index(current_page);
    }

    private void send_oa_request(int page_index){

        String url = WebApi.get_server_address() + getString(R.string.get_oa_list_api);
        HttpPostTask task = new HttpPostTask(url, this);

        HashMap<String, String> post_data = new HashMap<>();
        post_data.put("username", MainActivity.cur_username);
        MainActivity.set_local_token(this);
        post_data.put("token", MainActivity.token);
        post_data.put("pageindex", page_index + "");
        task.execute(post_data);
    }

    @Override
    public void handle_post_response(String response) {
        if (response.isEmpty()){
            Toast.makeText(OAActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
            return;
        }

        String error = JSONHelper.check_and_get_error(response);
        if (error != null){
            if (error.equals("wrong token")){
                Toast.makeText(OAActivity.this, StringDataHelper.ERROR_TOKEN, Toast.LENGTH_SHORT).show();
                return;
            }else{
                Toast.makeText(OAActivity.this, "发生错误: " +  error, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 解析oa
        OAParser parser = new OAParser();
        List<OAObject> parsed_oa_list =  parser.parse_oa(response);
        if (parsed_oa_list == null){
            Toast.makeText(OAActivity.this, "解析错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (all_oa == null){
            all_oa = new ArrayList<>(parsed_oa_list);
            oa_adapter = new OAAdapter(this, R.layout.oa_list_item, all_oa);
            oa_list_view.setAdapter(oa_adapter);
        }else{
            all_oa.clear();
            all_oa.addAll(parsed_oa_list);
            oa_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.oa_go_to_page_button:
                go_to_page();
                break;
            case R.id.oa_pre_page_button:
                pre_page();
                break;
            case R.id.oa_next_page_button:
                next_page();
                break;

            default:
                break;
        }
    }

    private void start_oa_webview(){
        Intent intent = new Intent(this, OAWebViewActivity.class);
        startActivity(intent);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OAObject oaObject = oa_adapter.getItem(position);
        CUR_OA_OBJECT = oaObject;
        start_oa_webview();
    }
}
