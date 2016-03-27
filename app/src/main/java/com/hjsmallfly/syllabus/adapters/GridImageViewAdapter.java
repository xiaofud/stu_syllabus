package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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

    public GridImageViewAdapter(Context context, List<String> url_list) {
        this.context = context;
        this.urls = url_list;
        // Ensure we get a different ordering of images on each run.
//        Collections.addAll(urls, );
//        Collections.shuffle(urls);

        // Triple up the list.
//        ArrayList<String> copy = new ArrayList<String>(urls);
//        urls.addAll(copy);
//        urls.addAll(copy);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
//            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
//                .placeholder(R.drawable.placeholder) //
//                .error(R.drawable.error) //
                .fit() //
                .tag(context) //
//                .resize(200, 200)
                .centerCrop()
                .into(view);
        Log.d("GRID_IMAGE_VIEW", url);

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
}

