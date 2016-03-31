package com.hjsmallfly.syllabus.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.activities.DisplayImageActivity;
import com.hjsmallfly.syllabus.helpers.BitmapHelper;
import com.hjsmallfly.syllabus.helpers.FileOperation;
import com.hjsmallfly.syllabus.views.SquaredImageView;
import com.squareup.picasso.Picasso;

import java.util.List;



/**
 * Created by smallfly on 16-3-26.
 *
 */
public class GridImageViewAdapter extends BaseAdapter {
    private final Context context;
    private List<String> urls;


    private ClickDisplayImageListener displayListener;
    private LongClickSaveImageListener longClickSaveImageListener;

    public GridImageViewAdapter(Context context, List<String> url_list) {
        this.context = context;
        this.urls = url_list;
        displayListener = new ClickDisplayImageListener();
        longClickSaveImageListener = new LongClickSaveImageListener();

    }

    public void update_urls(List<String> urls){
        this.urls.clear();
        this.urls.addAll(urls);
        notifyDataSetChanged();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        view.setOnClickListener(displayListener);
        view.setOnLongClickListener(longClickSaveImageListener);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
//                .fit() //
//                .resize(300, 300)
                .tag(context) //
//                .centerCrop()
                .into(view);
        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    private class ClickDisplayImageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DisplayImageActivity.class);
            Drawable drawable = ((ImageView)v).getDrawable();
            if (drawable == null)
                return;
            DisplayImageActivity.drawable = drawable;
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        }
    }

    private class LongClickSaveImageListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("是否保存到SD卡中");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String filename = FileOperation.get_app_folder(true) + System.currentTimeMillis() + ".jpg";
                    Drawable drawable = ((ImageView) v).getDrawable();
                    Bitmap bitmap = BitmapHelper.drawableToBitmap(drawable);
                    if (FileOperation.save_bitmap(bitmap, filename))
                        Toast.makeText(context, "图片保存为了: " + filename, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();

                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return true;
        }
    }
}

