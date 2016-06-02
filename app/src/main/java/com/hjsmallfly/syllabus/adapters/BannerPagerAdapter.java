package com.hjsmallfly.syllabus.adapters;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.syllabus.R;

import java.io.File;
import java.util.List;


/**
 * Created by smallfly on 16-3-5.
 *
 */
public class BannerPagerAdapter extends PagerAdapter {

    // -----------以文件读取图片的方式------------
    private List<File> bannerFileList;
    // -----------以文件读取图片的方式------------

    // -----------以 resource id 的形式-----------------
    private int[] resource_ids;
    // -----------以 resource id 的形式-----------------

    private Context context;

    /**
     * 以文件形式读取图片
     * @param context
     * @param bannerFileList   List<File>
     */
    public BannerPagerAdapter(Context context, List<File> bannerFileList){
        this.context = context;
        this.bannerFileList = bannerFileList;
    }

    public BannerPagerAdapter(Context context, int[] resource_ids){
        this.context = context;
        this.resource_ids = resource_ids;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.banner_layout, container, false);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.banner_image_view);

        // 有两种形式
        int resource_id = -1;
        File banner = null;

        if (bannerFileList != null){
            banner = bannerFileList.get(position);
            Log.d("bannerPagerAdapter", banner.toString());
        }else{
            resource_id = resource_ids[position];
            Log.d("bannerPagerAdapter", "resource id: " + resource_id);
        }

        Bitmap bitmap;
        // 读取图片文件
        if (banner != null)
            bitmap = BitmapFactory.decodeFile(banner.toString());
        else
            bitmap = BitmapFactory.decodeResource(context.getResources(), resource_id);

        // 如果图片不完整或者不是图片文件, 可能会出现null

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("banners", "click photo");
                Toast.makeText(context, "点击了图片", Toast.LENGTH_SHORT).show();
            }
        });

        if (bitmap != null) {
            // -----获取屏幕信息------
            DisplayMetrics displaymetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            // -----获取屏幕信息------
            // 计算图片原先的比例
            float photo_ratio = (float) bitmap.getWidth() / bitmap.getHeight();
            // 以屏幕的宽度作为图片的新宽度
            int new_width = width;
            // 根据比例计算出相应的高度
            int new_height = (int) (width / photo_ratio);
            // 生成新的图片
            bitmap = Bitmap.createScaledBitmap(bitmap, new_width, new_height, true);
            imageView.setImageBitmap(bitmap);
            container.addView(viewGroup);
            return viewGroup;
        }else{

            // 会运行到这里说明很可能数据出了问题, 删除之前缓存的banner信息
            FileOperation.delete_file(context, context.getString(R.string.BANNER_CACHED_FILE));

            // 设置默认图片
//            imageView.setImageResource(R.drawable.logo);
            Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_w);
//            Toast.makeText(BannerPagerAdapter.this.context, "运行到这行啦!", Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(bitmap1);
            container.addView(viewGroup);
            return viewGroup;
        }
    }


    @Override
    public int getCount() {
        // 因为有两种不同的设定资源的方式
        if (bannerFileList != null)
            return bannerFileList.size();
        else
            return resource_ids.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
