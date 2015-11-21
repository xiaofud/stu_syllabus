package com.hjsmallfly.syllabus.helpers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by smallfly on 15-11-21.
 * 用于处理剪贴板
 */
public class ClipBoardHelper {

    public static void setContent(Context context, String content){
        ClipboardManager cbm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", content);
        cbm.setPrimaryClip(clipData);
    }

}
