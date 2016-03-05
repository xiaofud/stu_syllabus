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

import com.hjsmallfly.syllabus.syllabus.R;

import java.io.File;
import java.util.List;


/**
 * Created by smallfly on 16-3-5.
 *
 */
public class BannerPagerAdapter extends PagerAdapter {

    private List<File> banners;
    private Context context;

    public BannerPagerAdapter(Context context, List<File> banners){
        this.context = context;
        this.banners = banners;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        File banner = banners.get(position);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.banner_layout, container, false);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.banner_image_view);
        Log.d("bannerPagerAdapter", banner.toString());

        // 读取图片文件
        Bitmap bitmap = BitmapFactory.decodeFile(banner.toString());
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
    }

    @Override
    public int getCount() {
        return banners.size();
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
