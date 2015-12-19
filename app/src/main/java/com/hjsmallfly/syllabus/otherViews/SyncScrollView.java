package com.hjsmallfly.syllabus.otherViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by daidaijie on 2015/12/9.
 */
public class SyncScrollView extends ScrollView {
    private View mView;

    public SyncScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public SyncScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mView != null) {
            mView.scrollTo(l, t);
        }
    }

    public void setScrollView(View view) {
        mView = view;
    }
}
