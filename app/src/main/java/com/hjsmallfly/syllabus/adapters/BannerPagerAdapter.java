package com.hjsmallfly.syllabus.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.hjsmallfly.syllabus.syllabus.Banner;
import com.hjsmallfly.syllabus.syllabus.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 * Created by smallfly on 16-3-5.
 *
 */
public class BannerPagerAdapter extends PagerAdapter {

    // -----------以文件读取图片的方式------------
    private List<Banner> bannerList;
    // -----------以文件读取图片的方式------------

    // -----------以 resource id 的形式-----------------
    private int[] resource_ids;
    // -----------以 resource id 的形式-----------------

    private Context context;

    /**
     * 以文件形式读取图片
     * @param context
     * @param bannerList   List<File>
     */
    public BannerPagerAdapter(Context context, List<Banner> bannerList){
        this.context = context;
        this.bannerList = bannerList;
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
//        int resource_id = -1;
        final Banner banner = bannerList.get(position);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("banner " + banner.getId());
                builder.setMessage(banner.getDescription());
                builder.setPositiveButton("去看看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse(banner.getGo_to_link());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
                builder.create().show();
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        Log.d("new_banner", "加载" + banner.getUrl());
        Picasso.with(context).load(banner.getUrl()).error(R.drawable.logo_w).resize(width, 0).into(imageView);
        container.addView(viewGroup);
        return viewGroup;
    }


    @Override
    public int getCount() {
        // 因为有两种不同的设定资源的方式
        if (bannerList != null)
            return bannerList.size();
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
