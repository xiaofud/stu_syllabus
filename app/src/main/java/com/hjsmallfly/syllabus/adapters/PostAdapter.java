package com.hjsmallfly.syllabus.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hjsmallfly.syllabus.activities.GlobalDiscussActivity;
import com.hjsmallfly.syllabus.activities.MainActivity;
import com.hjsmallfly.syllabus.activities.PostContentActivity;
import com.hjsmallfly.syllabus.helpers.ClipBoardHelper;
import com.hjsmallfly.syllabus.helpers.SyllabusRetrofit;
import com.hjsmallfly.syllabus.pojo.CreatedReturnValue;
import com.hjsmallfly.syllabus.pojo.PhotoList;
import com.hjsmallfly.syllabus.pojo.Post;
import com.hjsmallfly.syllabus.pojo.PostThumbUp;
import com.hjsmallfly.syllabus.pojo.ThumbUpTask;
import com.hjsmallfly.syllabus.restful.DeletePostApi;
import com.hjsmallfly.syllabus.restful.PushThumbUpApi;
import com.hjsmallfly.syllabus.restful.UnLikeApi;
import com.hjsmallfly.syllabus.syllabus.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by STU_nwad on 2015/10/11.
 *
 */
public class PostAdapter extends ArrayAdapter<Post> {

    private Gson gson = new GsonBuilder().create();
    private PushThumbUpApi thumbUpApi;
//    private UnLikeApi unLikeApi;

    class ViewHolder{
        private ImageView avatarView;   // 头像

        private ImageView like_image_view;
        private TextView like_count_text_view;

        private ImageView comment_image_view;
        private TextView comment_count_text_view;

        private GridView post_photos_grid_view;

        private TextView publisher_text;
        private TextView pub_time_text;
        private TextView content_text;


    }

    private int layout_id;

//    private static LinearLayout.LayoutParams params;

    // 构造函数
    public PostAdapter(Context context, int resource, List<Post> objects) {
        super(context, resource, objects);
        this.layout_id = resource;
        thumbUpApi = SyllabusRetrofit.retrofit.create(PushThumbUpApi.class);
//        unLikeApi = SyllabusRetrofit.retrofit.create(UnLikeApi.class);
    }


    public static int get_like_id(int uid, Post post){
        for(int i = 0 ; i < post.thumbUps.size() ; ++i){
            if (post.thumbUps.get(i).uid == uid)
                return post.thumbUps.get(i).id;
        }
        return -1;
    }

    private String trim_string_to_max_len(String str, int max_lines, int max_length, String suffix){
        int lines = 0;
        int line_position = -1;
        for(int i = 0 ; i < str.length() ; ++i)
            if (str.charAt(i) == '\n'){
                ++lines;
                if (lines == max_lines)
                    line_position = i;
            }

        if (lines < max_lines) {
            if (str.length() >= max_length)
                return str.substring(0, max_length - 1) + suffix;
            else
                return str;
        } else {
            return max_length - 1 > line_position ? str.substring(0, line_position) + suffix : str.substring(0, max_length - 1) + suffix;
//            return str.substring(0, line_position) + suffix;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Post post = getItem(position);  // 传进来的那个数据源
        Log.d("post_adapter", "post_id " + post.id + " position: " + position);
        View view;
        ViewHolder viewHolder;
        // 判断之前有没有缓存过这个数据
        if (convertView == null){
            // 新建一个view，用自定义的布局
            view = LayoutInflater.from(getContext()).inflate(layout_id, null);
            // 缓存这个view
            viewHolder = new ViewHolder();
            viewHolder.avatarView = (ImageView) view.findViewById(R.id.discuss_avatar_image_view);

            viewHolder.like_image_view = (ImageView) view.findViewById(R.id.like_image_view);
            viewHolder.like_count_text_view = (TextView) view.findViewById(R.id.like_count_text_view);

            viewHolder.comment_image_view = (ImageView) view.findViewById(R.id.comment_image_view);
            viewHolder.comment_count_text_view = (TextView) view.findViewById(R.id.comment_count_text_view);

            viewHolder.post_photos_grid_view = (GridView) view.findViewById(R.id.discuss_grid_image_view);

            viewHolder.publisher_text = (TextView) view.findViewById(R.id.discuss_speaker_text);
            viewHolder.pub_time_text = (TextView) view.findViewById(R.id.discuss_time_text);
            viewHolder.content_text = (TextView) view.findViewById(R.id.discuss_content);
            view.setTag(viewHolder);
        }else{  // 之前缓存过的view
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 设置头像
        if (post.postUser.image != null && !post.postUser.image.isEmpty()){
            // 显示图片
            Picasso.with(getContext()).load(post.postUser.image).error(R.mipmap.syllabus_icon2).into(viewHolder.avatarView);
        }else{
            viewHolder.avatarView.setImageResource(R.mipmap.syllabus_icon2);
        }

        final int like_id = get_like_id(1, post);

        // 判断用户是否点过赞, 设置相应的图片
        if (like_id != -1){ // 点过赞
            viewHolder.like_image_view.setImageResource(R.drawable.liked);
            // 暂时不能删除赞
//            if (!viewHolder.like_image_view.hasOnClickListeners()){
//                viewHolder.like_image_view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        final ImageView image_view = (ImageView) v;
//                        Call<Void> unlike_it = unLikeApi.unlike_this(like_id, 1, "000000");
//                        unlike_it.enqueue(new Callback<Void>() {
//                            @Override
//                            public void onResponse(Call<Void> call, Response<Void> response) {
//                                if (response.isSuccessful()){
//                                    image_view.setImageResource(R.drawable.to_like);
//                                    Toast.makeText(getContext(), "已经删除赞", Toast.LENGTH_SHORT).show();
//                                }else if (response.code() == 401){
//                                    Toast.makeText(getContext(), "登录超时, 请同步一下课表", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Void> call, Throwable t) {
//                                Toast.makeText(getContext(), "网络错误, 请重试", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }
        }else{
            viewHolder.like_image_view.setImageResource(R.drawable.to_like);
        }


        // 设置监听器
//        if (!viewHolder.like_image_view.hasOnClickListeners()){
        // 因为会重用, 所以暂时先每次都添加解决冲突
        viewHolder.like_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.user_id == -1){
                    Toast.makeText(getContext(), "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ImageView imageView = (ImageView) v;
                ThumbUpTask task = new ThumbUpTask(post.id, MainActivity.user_id, MainActivity.token);
                Call<CreatedReturnValue> thumbUpCall = thumbUpApi.like_this(task);
                thumbUpCall.enqueue(new Callback<CreatedReturnValue>() {
                    @Override
                    public void onResponse(Call<CreatedReturnValue> call, Response<CreatedReturnValue> response) {
                        if (response.isSuccessful()) {
//                            Toast.makeText(getContext(), "点赞成功", Toast.LENGTH_SHORT).show();
                            int id = response.body().id;
                            imageView.setImageResource(R.drawable.liked);
                            post.thumbUps.add(new PostThumbUp(id, 1));
                            notifyDataSetChanged();
                        } else if (response.code() == 401) {
                            Toast.makeText(getContext(), "登录超时, 请同步一下课表", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 403) {
                            Toast.makeText(getContext(), "已经赞过了", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreatedReturnValue> call, Throwable t) {
                        Toast.makeText(getContext(), "网络错误, 请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
//        }

        // 先判断这个post有没有图片
        PhotoList photoList = gson.fromJson(post.photoListJson, PhotoList.class);

        if (photoList != null){
            // 说明这个post有图片
            // 将显示图片的控件设置为可见
            viewHolder.post_photos_grid_view.setVisibility(View.VISIBLE);
            if (viewHolder.post_photos_grid_view.getAdapter() == null) {
                // 第一次处理这个view
                List<String> thumbnails = photoList.get_thumbnails();
                viewHolder.post_photos_grid_view.setAdapter(new GridImageViewAdapter(getContext(), thumbnails));
            }else{
                GridImageViewAdapter adapter = (GridImageViewAdapter) viewHolder.post_photos_grid_view.getAdapter();
                adapter.update_urls(photoList.get_thumbnails());
            }
        }else{
            // 没有图片, 则该控件不应该显示出来
            viewHolder.post_photos_grid_view.setVisibility(View.GONE);
        }



//        // 防止view被重用, 导致不同的post显示相同的图片
//        if (photoList == null)
//            viewHolder.post_photos_grid_view.setVisibility(View.GONE);
//        else
//            viewHolder.post_photos_grid_view.setVisibility(View.VISIBLE);



        viewHolder.publisher_text.setText(post.postUser.nickname);
        viewHolder.pub_time_text.setText(post.postTime);
        viewHolder.content_text.setText(trim_string_to_max_len(post.content.trim(), 6, 140, "(点击查看全文)"));   // 去除没必要的空字符

        String like_count = post.thumbUps.size() + "";
        String comment_count = post.comments.size() + "";
        viewHolder.like_count_text_view.setText(like_count);
        viewHolder.comment_count_text_view.setText(comment_count);

        DisplayPostListener listener = new DisplayPostListener(post, position);
        CopyOrDeleteLongListener longListener = new CopyOrDeleteLongListener(post);

        // 点击就会跳转到具体页面的控件
        viewHolder.comment_count_text_view.setOnClickListener(listener);
        viewHolder.comment_image_view.setOnClickListener(listener);
        viewHolder.content_text.setOnClickListener(listener);
        viewHolder.content_text.setOnLongClickListener(longListener);
        
        // 设置监听器
//        viewHolder.comment_image_view.setOnClickListener(listener);
//        view.setOnClickListener(listener);

        return view;    // view 里面的对象的属性是通过viewHolder修改的
    }

    private class CopyOrDeleteLongListener implements View.OnLongClickListener{
//        private Context context;
        private Post post;
        private DeletePostApi deletePostApi;
//        private int position;

        public CopyOrDeleteLongListener(Post post){
            this.post = post;
//            this.position = position;
        }


        @Override
        public boolean onLongClick(View v) {


            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());
            if (MainActivity.cur_username.equals("14xfdeng") || MainActivity.cur_username.equals("13yjli3") || MainActivity.cur_username.equals("14jhwang")){
                builder.setTitle("请选择一个操作" + "(" + post.postUser.account + ")");
            }else
                builder.setTitle("请选择一个操作");

            builder.setPositiveButton("复制", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipBoardHelper.setContent(getContext(), post.content);
                    Toast.makeText(getContext(), "已将内容复制到剪贴板上", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (MainActivity.user_id == -1){
                        Toast.makeText(getContext(), "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (deletePostApi == null){
                        deletePostApi = SyllabusRetrofit.retrofit.create(DeletePostApi.class);
                        Call<Void> call = deletePostApi.delete_post(post.id, MainActivity.user_id, MainActivity.token);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()){
                                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                    remove(post);
                                    notifyDataSetChanged();
                                }else if (response.code() == 401){
                                    Toast.makeText(getContext(), "登录超时, 请同步一次课表", Toast.LENGTH_SHORT).show();
                                }else if (response.code() == 403){
                                    Toast.makeText(getContext(), "不能删除别人的资源", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(), "网络错误, 请重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            builder.create().show();
            return false;
        }
    }

    private class DisplayPostListener implements View.OnClickListener{

        private Post post_to_display;
        private int position;
        
        public DisplayPostListener(Post post, int position){
            this.post_to_display = post;
            this.position = position;
        }
        
        @Override
        public void onClick(View v) {
            PostContentActivity.post = this.post_to_display;
            GlobalDiscussActivity.ENSURE_POSITION = position;
            getContext().startActivity(new Intent(getContext(), PostContentActivity.class));
        }
    }
    
}
