package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.hjsmallfly.syllabus.adapters.PostAdapter;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.pojo.PhotoList;
import com.hjsmallfly.syllabus.pojo.PostList;
import com.hjsmallfly.syllabus.restful.GetPostsApi;
import com.hjsmallfly.syllabus.syllabus.R;
import com.umeng.analytics.MobclickAgent;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GlobalDiscussActivity extends AppCompatActivity {

    // =========== 用于给其他类控制这个类的UI ===========
    public static boolean need_to_update_posts = false;
    public static  int ENSURE_POSITION = -1;
    public static final int VERY_BIG_INTEGER = 99999999;
    public static final int NUMBER_OF_POSTS_PER_PULL = 5;  // 每次拉取的文章数量
    // =========== 用于给其他类控制这个类的UI ===========



    private ListView global_list_view;
    private Button new_post_button;
    private Button view_more_button;

    private PostAdapter postAdapter;

    private PostList postList;



    // APIS
    private GetPostsApi getPostsApi;

    private int current_max_id = VERY_BIG_INTEGER;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_discuss);

        find_views();
        setup_views();

        getPostsApi = SyllabusRetrofit.retrofit.create(GetPostsApi.class);

        // 拉取数据
        get_posts(VERY_BIG_INTEGER, true);
//        get_banners();
    }

    // 友盟的统计功能
    @Override
    protected void onResume() {
        super.onResume();
        if (need_to_update_posts){
            get_posts(VERY_BIG_INTEGER, true);
            need_to_update_posts = false;
        }

        if (GlobalDiscussActivity.ENSURE_POSITION != -1 && postAdapter != null){
            // 设置回之前设定的位置

            if (GlobalDiscussActivity.ENSURE_POSITION < postAdapter.getCount()){
                // 数据有可能更新了
                postAdapter.notifyDataSetChanged();
                global_list_view.setSelection(GlobalDiscussActivity.ENSURE_POSITION);
            }

        }

        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_global_discussion, menu);
        return true;
    }



    private void find_views(){
        global_list_view = (ListView) findViewById(R.id.global_discuss_list_view);
        new_post_button = (Button) findViewById(R.id.new_post_button);
        view_more_button = (Button) findViewById(R.id.view_more_button);
    }

    private void setup_views(){
        new_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.user_id == -1){
                    Toast.makeText(GlobalDiscussActivity.this, "亲, 请同步一下课表", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(GlobalDiscussActivity.this, PushPostActivity.class));
            }
        });

        view_more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_posts(current_max_id, false);
            }
        });

    }

    private void get_posts(int before_id, final boolean re_pull){

        Call<PostList> postListCall = getPostsApi.get_posts(NUMBER_OF_POSTS_PER_PULL, before_id);
        postListCall.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.isSuccessful()) {
                    PostList tmp = response.body();
                    // 记录原来列表的最后一项
                    int origin_max_item_id = global_list_view.getCount();
                    if (GlobalDiscussActivity.this.postList != null){



                        // 更新内容
                        if (re_pull)    // 意味着要拉取最新的数据
                            GlobalDiscussActivity.this.postList.postList.clear();
                        current_max_id = tmp.postList.get(tmp.postList.size() - 1).id;    // 降序排序
                        GlobalDiscussActivity.this.postList.postList.addAll(tmp.postList);
                    }else {   // 第一次请求
                        GlobalDiscussActivity.this.postList = response.body();
                        current_max_id = GlobalDiscussActivity.this.postList.postList.get(tmp.postList.size() - 1).id;
                    }

                    if (re_pull) {
                        display_posts(0);   // 显示第一个
                    }else{
                        display_posts(origin_max_item_id - 1);  // 点按钮之前的最后一个item的position
                    }
                } else if(response.code() == 404){
                    Toast.makeText(GlobalDiscussActivity.this, "没新动态啦", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GlobalDiscussActivity.this, "code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                Toast.makeText(GlobalDiscussActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void display_posts(int selection_id){
        // 显示最新的posts
        if (this.postList != null){
//            if (postAdapter == null){
            if (postAdapter == null) {
                postAdapter = new PostAdapter(this, R.layout.discuss_item_layout, this.postList.postList);
                global_list_view.setAdapter(postAdapter);
            }
            postAdapter.notifyDataSetChanged();
//            Toast.makeText(GlobalDiscussActivity.this, "select: " + selection_id, Toast.LENGTH_SHORT).show();
            global_list_view.setSelection(selection_id);
        }
//        else {
//            postAdapter.notifyDataSetChanged();
//            Toast.makeText(GlobalDiscussActivity.this, "select: " + selection_id, Toast.LENGTH_SHORT).show();
//            global_list_view.setSelection(selection_id);
//        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update_global_discussion_action:
                get_posts(VERY_BIG_INTEGER, true);
                return true;

            case R.id.personal_info_action:
                if (MainActivity.user_id == -1){
                    Toast.makeText(GlobalDiscussActivity.this, "亲, 请同步一次课表", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent person_intent = new Intent(this, PersonalInfoActivity.class);
                startActivity(person_intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
