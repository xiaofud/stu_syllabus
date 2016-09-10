package com.hjsmallfly.syllabus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.hjsmallfly.syllabus.adapters.PostRecyclerAdapter;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.mvp.contract.PostsContract;
import com.hjsmallfly.syllabus.mvp.model.PostsModel;
import com.hjsmallfly.syllabus.mvp.presenter.PostsPresenter;
import com.hjsmallfly.syllabus.pojo.PostList;
import com.hjsmallfly.syllabus.restful.GetPostsApi;
import com.hjsmallfly.syllabus.syllabus.R;
import com.umeng.analytics.MobclickAgent;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialActivity extends AppCompatActivity implements PostsContract.PostsView {

    // =========== 用于给其他类控制这个类的UI ===========
    public static boolean need_to_update_posts = false;
    public static final int VERY_BIG_INTEGER = 99999999;
    public static final int NUMBER_OF_POSTS_PER_PULL = 10;  // 每次拉取的文章数量

    // =========== 用于给其他类控制这个类的UI ===========


    private RecyclerView postsRecyclerView;
    private Button new_post_button;
    private Button view_more_button;

    private PostList postList;

    private PostRecyclerAdapter postRecyclerAdapter;

    private int currentMinID = VERY_BIG_INTEGER;


    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private PostsContract.PostsPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_discuss);

        find_views();
        setup_views();
        PostsModel postsModel = new PostsModel();
        presenter = new PostsPresenter(this, postsModel);

        // 拉取数据
        presenter.loadMorePosts(NUMBER_OF_POSTS_PER_PULL, VERY_BIG_INTEGER, true);
    }

    // 友盟的统计功能
    @Override
    protected void onResume() {
        super.onResume();
        if (need_to_update_posts){
//            get_posts(VERY_BIG_INTEGER, true);
            presenter.loadMorePosts(NUMBER_OF_POSTS_PER_PULL, VERY_BIG_INTEGER, true);
            need_to_update_posts = false;
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
        postsRecyclerView = (RecyclerView) findViewById(R.id.postsRecyclerView);
        new_post_button = (Button) findViewById(R.id.new_post_button);
        view_more_button = (Button) findViewById(R.id.view_more_button);
    }

    private void setup_views(){
        new_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.user_id == -1){
                    Toast.makeText(SocialActivity.this, "亲, 请同步一下课表", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(SocialActivity.this, PushPostActivity.class));
            }
        });

        view_more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.loadMorePosts(NUMBER_OF_POSTS_PER_PULL, currentMinID, false);
            }
        });

        recyclerViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        postsRecyclerView.setLayoutManager(recyclerViewLayoutManager);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update_global_discussion_action:
                presenter.loadMorePosts(NUMBER_OF_POSTS_PER_PULL, VERY_BIG_INTEGER, true);
                return true;

            case R.id.personal_info_action:
                if (MainActivity.user_id == -1){
                    Toast.makeText(SocialActivity.this, "亲, 请同步一次课表", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent person_intent = new Intent(this, PersonalInfoActivity.class);
                startActivity(person_intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void setPresenter(PostsContract.PostsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updatePosts(PostList postList, boolean refresh) {

        if (this.postList == null){
            // 第一次
            this.postList = postList;
            postRecyclerAdapter = new PostRecyclerAdapter(this.postList.postList);
            postsRecyclerView.setAdapter(postRecyclerAdapter);

        }else{
            if (refresh){
                this.postList.postList.clear();
                this.postList.postList.addAll(postList.postList);
                postRecyclerAdapter.notifyDataSetChanged();
            }else{
                int sizeBefore = this.postList.postList.size();
                this.postList.postList.addAll(postList.postList);
                postRecyclerAdapter.notifyItemRangeChanged(sizeBefore, postList.postList.size());
            }
        }

        recyclerViewLayoutManager.scrollToPosition(this.postList.postList.size() - postList.postList.size());

        // 最底下的id是最小的
        currentMinID = postList.postList.get(postList.postList.size() - 1).id;

    }

    public void showMessage(String message){
        Toast.makeText(SocialActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInternetError() {
        showMessage("网络连接错误");
    }

    @Override
    public void showNoMore() {
        showMessage("没有更多动态啦");
    }

    @Override
    public void showUnknownError(int code) {
        showMessage("未知错误: " + code);
    }
}
