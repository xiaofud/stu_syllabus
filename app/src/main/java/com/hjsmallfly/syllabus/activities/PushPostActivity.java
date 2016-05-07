package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjsmallfly.syllabus.adapters.URIGridImageViewAdapter;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.ImageUploader;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.pojo.BmobPhoto;
import com.hjsmallfly.syllabus.pojo.Photo;
import com.hjsmallfly.syllabus.pojo.PushPostTask;
import com.hjsmallfly.syllabus.restful.PushPostApi;
import com.hjsmallfly.syllabus.syllabus.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushPostActivity extends AppCompatActivity {

    private final static int SELECT_IMAGE_REQUEST_CODE = 1;     // 作为选择图片操作的返回值
    private final static String[] TYPE_ARRAY = new String[]{"普通动态", "网站/推文等"};
//    public final static int TYPE_TOPIC = 0;
//    public final static int TYPE_ACTIVITY = 1;

    // =========== 控件 ===========
    private Spinner select_type_spinner;
    private EditText post_url_edit;
    private EditText post_content;
    private GridView photos_view;
    private Button add_photo_button;
    private Button push_button;
    // =========== 控件 ===========

    private List<Uri> photo_uris;
    private URIGridImageViewAdapter adapter;
    private List<File> photo_files;
    private List<File> tmp_files;   // 用于删除之用
    private int post_type = PushPostApi.POST_TYPE_TOPIC;


    private List<String> uploaded_photo_urls;

    private PushPostApi pushPostApi;

    private Gson gson = new Gson();



    private void init_views(){
        select_type_spinner = (Spinner) findViewById(R.id.content_type_spinner);
        post_url_edit = (EditText) findViewById(R.id.post_url_edit);
        post_content = (EditText) findViewById(R.id.new_post_content);
        photos_view = (GridView) findViewById(R.id.post_photos_grid_view);
        add_photo_button = (Button) findViewById(R.id.add_photo_button);
        push_button = (Button) findViewById(R.id.push_post_button);

        push_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = post_content.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(PushPostActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = "";

                if (post_type == PushPostApi.POST_TYPE_ACTIVITY) {
                    url = post_url_edit.getText().toString().trim();
                    if (!valid_url(url)) {
                        Toast.makeText(PushPostActivity.this, "请输入合法的URL", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                if (photo_files != null && photo_files.size() > 0) {
                    upload_photos();    // 包含了push文字信息到服务器
                    return;
                }
//                Toast.makeText(PushPostActivity.this, "仅发布文字消息", Toast.LENGTH_SHORT).show();
                post(); // 仅仅发布文字信息

            }
        });

        add_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo_uris != null && photo_uris.size() >= 3) {
                    Toast.makeText(PushPostActivity.this, "目前最多发三张图", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_IMAGE_REQUEST_CODE);
            }
        });

        select_type_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, TYPE_ARRAY));
        select_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String message = null;
                if (position == PushPostApi.POST_TYPE_TOPIC) {
                    post_url_edit.setVisibility(View.GONE);
                }
                else {
                    post_url_edit.setVisibility(View.VISIBLE);
                }
                post_type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        init_views();

        pushPostApi = SyllabusRetrofit.retrofit.create(PushPostApi.class);
    }

    public static boolean valid_url(String url){
        return Patterns.WEB_URL.matcher(url).matches();
    }

    private File compress_photo(String image_path, int divide_by, String tmp_file_name){
        Bitmap large = BitmapFactory.decodeFile(image_path);
        if (large == null){
            return null;
        }
        File tmp_file = new File(FileOperation.get_app_folder(false), tmp_file_name);
        if (tmp_file.exists() && !tmp_file.delete()){
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int width = large.getWidth();
        int height = large.getHeight();
        if (width * height > 1024 * 1024){  // 先裁剪一下
            Bitmap scaled = Bitmap.createScaledBitmap(large, width / divide_by, height / divide_by, true);
            scaled.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        }else{
            large.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        }
        try {
            FileOutputStream os = new FileOutputStream(tmp_file);
            os.write(stream.toByteArray());
            os.close();
            return tmp_file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在gridView中显示选择的照片, 同时记录要上传的文件
     * @param data activity 返回的数据
     */
    private void preview_photos(Intent data){
        // 添加一张图片到gridView里面
        if (this.photo_uris == null) {
            this.photo_uris = new ArrayList<>();
            this.photo_files = new ArrayList<>();
            // 建立adapter
            this.adapter = new URIGridImageViewAdapter(this, this.photo_uris);
            photos_view.setAdapter(this.adapter);
        }


        String real_path = FileOperation.getRealPathFromURI(this, data.getData());
        if (real_path.isEmpty()){
            Toast.makeText(PushPostActivity.this, "无法读取图片路径(见到这条消息, 请微信联系STU_nwad)", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(real_path);
        File tmp_file = null;
        // 如果大于2MB
        if (file.length() > 1024 * 512){
//                        Toast.makeText(PushPostActivity.this, "所选图片大于2MB, 请选择较小的图片", Toast.LENGTH_SHORT).show();
//            Toast.makeText(PushPostActivity.this, "图片大于1MB, 进行压缩", Toast.LENGTH_SHORT).show();
            Log.d("new_post", "图片大于512KB, 进行压缩");
            tmp_file = compress_photo(real_path, 2, System.currentTimeMillis() + ".jpg");
            if (tmp_file != null){
                if (this.tmp_files == null)
                    this.tmp_files = new ArrayList<>();
                tmp_files.add(tmp_file);
                Log.d("new_post", "压缩后的大小为" + tmp_file.length() / (1024.0 * 1024) + "MB");
            }else {
                Log.d("new_post", "压缩失败");
                return;
            }
        }

        this.photo_uris.add(data.getData());
        if (tmp_file == null)
            this.photo_files.add(file);
        else
            this.photo_files.add(tmp_file);

        this.adapter.notifyDataSetChanged();

//        Log.d("new_post", file.length() + " " + real_path);
    }

    private void clear_tmp_files(){
        if (tmp_files != null && tmp_files.size() > 0){
            for(File file: tmp_files){
                if (!file.delete()){
                    Log.d("new_post", "删除 " + file.toString() + " 失败");
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SELECT_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    preview_photos(data);
                }
                break;
            default:
                break;
        }
    }


    public String generate_photo_list(List<Photo> photo_list){
        HashMap<String, List<HashMap<String, String>>> photo_list_map = new HashMap<>();
        List<HashMap<String, String>> photos = new ArrayList<>();
        for(int i = 0 ; i < photo_list.size() ; ++i){
            HashMap<String, String> photo = new HashMap<>(2);
            photo.put("size_big", photo_list.get(i).getSize_big());
            photo.put("size_small", photo_list.get(i).getSize_small());
            photos.add(photo);
        }
        photo_list_map.put("photo_list", photos);
        String json = gson.toJson(photo_list_map);
        Log.d("complex_json", json);
        return json;
    }

    private void post(){

        if (MainActivity.user_id == -1){
            Toast.makeText(this, "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
            return;
        }

        // 当post_type为推文的时候这个输入框的内容是作为描述信息
        String content_or_description = post_content.getText().toString().trim();
        String photo_list_json = null;
        if (uploaded_photo_urls != null && uploaded_photo_urls.size() > 0){
            List<Photo> photoList = new ArrayList<>();
            for(int i = 0 ; i < uploaded_photo_urls.size() ; ++i)
                photoList.add(new Photo(uploaded_photo_urls.get(i), uploaded_photo_urls.get(i)));
            photo_list_json = generate_photo_list(photoList);
        }
        PushPostTask pushPostTask;// = new PushPostTask(content_or_description, "None", MainActivity.user_id, MainActivity.token, PushPostApi.POST_TYPE_TOPIC, photo_list_json);

        // 判断类型
        if (post_type == PushPostApi.POST_TYPE_TOPIC){
            // 普通动态
            pushPostTask = new PushPostTask(content_or_description, "None", MainActivity.user_id, MainActivity.token, PushPostApi.POST_TYPE_TOPIC, photo_list_json);
        }else{
            // 推文等
            String url = post_url_edit.getText().toString().trim();
            if (!url.startsWith("http")){
                url = "http://" + url;
            }
            pushPostTask = new PushPostTask(url, content_or_description, MainActivity.user_id, MainActivity.token, PushPostApi.POST_TYPE_ACTIVITY, photo_list_json);
//            Toast.makeText(PushPostActivity.this, "推文", Toast.LENGTH_SHORT).show();
        }

        Call<Void> postCall = pushPostApi.post(pushPostTask);
        postCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(PushPostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    clear_tmp_files();
                    SocialActivity.need_to_update_posts = true;
                    finish();
                }else if (response.code() == 401){
                    Toast.makeText(PushPostActivity.this, "登录超时, 请同步一次课表~", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PushPostActivity.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (uploaded_photo_urls != null)
                    uploaded_photo_urls.clear();
                Toast.makeText(PushPostActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });


//        PushPostTask pushPostTask = new PushPostTask(content_or_description, "None", 1, "000000", 1, )
    }

    private void upload_photos(){
        if (this.uploaded_photo_urls == null)
            this.uploaded_photo_urls = new ArrayList<>();
        MediaType mediaType = MediaType.parse("image/*");
        Toast.makeText(PushPostActivity.this, "开始上传图片", Toast.LENGTH_SHORT).show();
        for(int i = 0 ; i < photo_files.size() ; ++i){
            File file = photo_files.get(i);
            Log.d("new_post", "开始上传: " + file.toString());
            Callback<BmobPhoto> callback = new Callback<BmobPhoto>() {
                @Override
                public void onResponse(Call<BmobPhoto> call, Response<BmobPhoto> response) {

                    if (response.isSuccessful()){
                        uploaded_photo_urls.add
                                (ImageUploader.IMAGE_URL_PREFIX_WITH_TAILING_SLASH + response.body().url);
                        Log.d("new_post", "上传成功");
                        if (uploaded_photo_urls.size() >= photo_files.size()){
                            // 说明全部上传成功了
//                            Toast.makeText(PushPostActivity.this, "图片全部上传完毕", Toast.LENGTH_SHORT).show();
                            post();
                        }
                    }else{
                        Log.d("new_post", "上传失败 " + response.code()  + " " + response.message());
                        Toast.makeText(PushPostActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                        uploaded_photo_urls.clear();
                    }
                }

                @Override
                public void onFailure(Call<BmobPhoto> call, Throwable t) {
                    Toast.makeText(PushPostActivity.this, "网络错误, 请重试", Toast.LENGTH_SHORT).show();
                }
            };
            String filename =
                    MainActivity.cur_username + "_" + System.currentTimeMillis() + ".jpg";
            ImageUploader.upload_image(mediaType, filename, file, callback);
        }
    }

}
