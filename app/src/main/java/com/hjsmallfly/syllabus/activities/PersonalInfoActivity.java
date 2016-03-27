package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.helpers.ImageUploader;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.pojo.BmobPhoto;
import com.hjsmallfly.syllabus.pojo.UpdateUserBody;
import com.hjsmallfly.syllabus.pojo.User;
import com.hjsmallfly.syllabus.restful.GetUserApi;
import com.hjsmallfly.syllabus.restful.UpdateUserApi;
import com.hjsmallfly.syllabus.syllabus.R;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {


    private final static int SELECT_IMAGE_REQUEST_CODE = 1;     // 这个随自己定义
    private final static int CROP_PHOTO_REQUEST_CODE = 2;

    // 头像默认大小
    public final static int AVATAR_DEFAULT_WIDTH = 300;
    public final static int AVATAR_DEFAULT_HEIGHT = 300;

    public final static String AVATAR_FILE_NAME = "avatar.jpg";

    // ========== 控件 ==========
    private ImageView avatarImageView;
    private EditText username_edit;
    private EditText nickname_edit;
    private EditText profile_edit_text;
    private Button update_user_button;
//    private Button upload_avatar_button;

    // ========== 控件 ==========

    // ========== APIS ==========
    private GetUserApi getUserApi;
    private UpdateUserApi updateUserApi;
    // ========== APIS ==========

    private User user;

    // 用于判断是否需要更新头像
    private boolean need_to_upload_avatar = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        find_views();
        setup_views();

        // 读取默认的头像
        load_default_avatar();

        // 创建api对象
        getUserApi = SyllabusRetrofit.retrofit.create(GetUserApi.class);
        updateUserApi = SyllabusRetrofit.retrofit.create(UpdateUserApi.class);

        get_user_info();

    }

    // 友盟的统计功能
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update_information_action){
            get_user_info();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void find_views(){
        avatarImageView = (ImageView) findViewById(R.id.avatar);
        username_edit = (EditText) findViewById(R.id.username_info_edit);
        nickname_edit = (EditText) findViewById(R.id.nickname_info_edit);
        update_user_button = (Button) findViewById(R.id.personal_info_update_button);
//        upload_avatar_button = (Button) findViewById(R.id.upload_avatar_button);
        profile_edit_text = (EditText) findViewById(R.id.profile_edit_text);

    }

    private void setup_views(){
        // add listener
        update_user_button.setOnClickListener(this);
        // 一开始不能点击, 获取了之前的用户数据才能点击
        update_user_button.setEnabled(false);
        avatarImageView.setOnClickListener(this);
//        upload_avatar_button.setOnClickListener(this);
    }

    private void load_default_avatar(){
        File f = new File(FileOperation.get_app_folder(false), AVATAR_FILE_NAME);
        if (f.exists()){
            setAvatar(f);
        }
    }

    private void update_user(User user){
        UpdateUserBody updateUserBody =
                new UpdateUserBody(user.id, user.id, user.nickname,
                        user.gender, user.profile, MainActivity.token, user.image);
        Call<Void> update_call = updateUserApi.update(updateUserBody);
        update_call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    need_to_upload_avatar = false;
                    Toast.makeText(PersonalInfoActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toast.makeText(PersonalInfoActivity.this, "请求错误", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(PersonalInfoActivity.this, "登录超时, 请同步一次课表(" + MainActivity.token + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update_user_info(){
        String nickname = nickname_edit.getText().toString();
        if (nickname.trim().isEmpty()){
            Toast.makeText(PersonalInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else if(nickname.length() > 20){
            Toast.makeText(PersonalInfoActivity.this, "昵称不能超过20个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        String profile = profile_edit_text.getText().toString().trim();
        if (profile.length() > 40){
            Toast.makeText(PersonalInfoActivity.this, "个性签名不能超过40个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        if (need_to_upload_avatar){
            // 上传图片的时候会更新用户
            upload_avatar();
        }else{
            User user = this.user;
            user.nickname = nickname;
            user.profile = profile;
            update_user(user);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.personal_info_update_button){
            // 提交修改
            update_user_info();
        }else if(v.getId() == R.id.avatar) {
            // 选择图片
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_IMAGE_REQUEST_CODE);
        }
//        }else if (v.getId() == R.id.upload_avatar_button){
//            upload_avatar();
//        }
    }

    /**
     * 上传头像
     */
    private void upload_avatar(){
        File avatar = new File(FileOperation.get_app_folder(false), AVATAR_FILE_NAME);
        MediaType mediaType = MediaType.parse("image/jpg");
        String filename = MainActivity.cur_username + System.currentTimeMillis() + ".jpg";
        if (avatar.exists()){
            // 禁用按钮, 防止多次点击
//            upload_avatar_button.setEnabled(false);
            Log.d(ImageUploader.DEBUG_TAG, "开始上传图片");
            Toast.makeText(PersonalInfoActivity.this, "开始上传头像", Toast.LENGTH_SHORT).show();
            ImageUploader.upload_image(mediaType, filename, avatar,
                    new Callback<BmobPhoto>() {
                        @Override
                        public void onResponse(Call<BmobPhoto> call, Response<BmobPhoto> response) {
                            Log.d(ImageUploader.DEBUG_TAG, "onResponse");
                            if (response.isSuccessful()){
//                                Toast.makeText(PersonalInfoActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                                BmobPhoto bmobPhoto = response.body();
//                                Toast.makeText(PersonalInfoActivity.this, bmobPhoto.url, Toast.LENGTH_SHORT).show();
                                User user = PersonalInfoActivity.this.user;
                                user.image = ImageUploader.IMAGE_URL_PREFIX_WITH_TAILING_SLASH + bmobPhoto.url;
                                update_user(user);
                                Log.d(ImageUploader.DEBUG_TAG, bmobPhoto.url);
                            }else{
//                                Toast.makeText(PersonalInfoActivity.this, "failed: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(PersonalInfoActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                                Log.d(ImageUploader.DEBUG_TAG, response.code() + " " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<BmobPhoto> call, Throwable t) {
                            Toast.makeText(PersonalInfoActivity.this, "网络错误, 请重新上传", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SELECT_IMAGE_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
//                    Toast.makeText(PersonalInfoActivity.this, "选择了图片", Toast.LENGTH_SHORT).show();
                    // 裁剪
                    startPhotoZoom(data.getData(), AVATAR_DEFAULT_WIDTH, AVATAR_DEFAULT_HEIGHT);
                } else if (resultCode == RESULT_CANCELED) {
//                    Toast.makeText(PersonalInfoActivity.this, "取消了选择", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case CROP_PHOTO_REQUEST_CODE:{
//                Toast.makeText(PersonalInfoActivity.this, "存储好了头像", Toast.LENGTH_SHORT).show();
                File avatar_file = new File(FileOperation.get_app_folder(false), AVATAR_FILE_NAME);
                if (avatar_file.exists()){
                    // 需要上传头像
                    need_to_upload_avatar = true;
                    setAvatar(avatar_file);
                }else{
                    Toast.makeText(PersonalInfoActivity.this, "选择头像失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void startPhotoZoom(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);

        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        Uri picture_uri = FileOperation.get_avatar_uri();
        if (picture_uri == null)
            return;

        intent.putExtra(MediaStore.EXTRA_OUTPUT, picture_uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, CROP_PHOTO_REQUEST_CODE);
    }

    private void setAvatar(File avatar){
        Bitmap bitmap = BitmapFactory.decodeFile(avatar.toString());
        if (bitmap != null) {
            avatarImageView.setImageBitmap(bitmap);
        }else{
            Log.d("avatar", "the avatar file has broken");
            if (avatar.delete()){
                Log.d("avatar", "deleted the broken avatar file");
            }else
                Log.d("avatar", "failed to delete the broken avatar file");
        }
    }


    private void get_user_info(){

        Call<User> userCall = getUserApi.get_user(MainActivity.cur_username);
//        Toast.makeText(PersonalInfoActivity.this, "开始获取用户信息", Toast.LENGTH_SHORT).show();
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    User user = response.body();
                    PersonalInfoActivity.this.user = user;
                    username_edit.setText(user.account);
                    nickname_edit.setText(user.nickname);
                    profile_edit_text.setText(user.profile);
                    // 这样才能修改
                    update_user_button.setEnabled(true);
                    return;
                }else if (response.code() == 404){
                    Toast.makeText(PersonalInfoActivity.this, "亲, 请登录查询一次课表", Toast.LENGTH_SHORT).show();
                }else if (response.code() == 400){
                    Toast.makeText(PersonalInfoActivity.this, "bad request", Toast.LENGTH_SHORT).show();
                }
                update_user_button.setEnabled(false);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                update_user_button.setEnabled(false);
            }
        });

    }

//    @Override
//    public void handle_user(UserInformation userInformation) {
//        if (userInformation == null){
//            return;
//        }
//
//        username_edit.setText(userInformation.username);
//        if (userInformation.nick_name != null){
//            nickname_edit.setText(userInformation.nick_name);
//        }
//    }



//    @Override
//    public void handle_user_alter(String response) {
//        if (response.isEmpty()){
//            Toast.makeText(PersonalInfoActivity.this, "网络连接错误!", Toast.LENGTH_SHORT).show();
//            return ;
//        }
//
//        String error = JSONHelper.check_and_get_error(response);
//        if (error != null){
//            if (error.equals("no such user"))
//                Toast.makeText(PersonalInfoActivity.this, "没有该用户!", Toast.LENGTH_SHORT).show();
//            else if (error.equals("wrong token"))
//                Toast.makeText(PersonalInfoActivity.this, StringDataHelper.ERROR_TOKEN, Toast.LENGTH_SHORT).show();
//            else if (error.equals("the nickname has been used")){
//                Toast.makeText(PersonalInfoActivity.this, "用户名已经被使用", Toast.LENGTH_SHORT).show();
//            }else if(error.equals("not authorized to use this name")){
//                Toast.makeText(PersonalInfoActivity.this, "不允许使用该用户名", Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//
//        // 友盟
//        MobclickAgent.onEvent(this, "Setting_Nickname");
//
//        Toast.makeText(PersonalInfoActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
//
//
//    }
}
