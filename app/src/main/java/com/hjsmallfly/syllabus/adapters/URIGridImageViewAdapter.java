package com.hjsmallfly.syllabus.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hjsmallfly.syllabus.views.SquaredImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by smallfly on 16-3-27.
 *
 */
public class URIGridImageViewAdapter extends BaseAdapter {
    private final Context context;
    private List<Uri> uris;

    public URIGridImageViewAdapter(Context context, List<Uri> uri_list) {
        this.context = context;
        this.uris = uri_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
//            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        // Get the image URL for the current position.
        Uri uri = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(uri) //
//                .placeholder(R.drawable.placeholder) //
//                .error(R.drawable.error) //
                .fit() //
                .tag(context) //
//                .resize(200, 200)
                .centerCrop()
                .into(view);
//        Log.d("GRID_IMAGE_VIEW", uri);
        return view;
    }

    @Override public int getCount() {
        return uris.size();
    }

    @Override public Uri getItem(int position) {
        return uris.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
