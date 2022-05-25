package com.sayeed.mad.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;



public class ScreenUtil {

    public static int getPhoneHeight(@NonNull Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.heightPixels;
    }


    public static int getPhoneWidth(@NonNull Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.widthPixels;
    }


    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }


    public static int getDisplayOrient (Context context) {
        return context.getResources().getConfiguration().orientation;
    }

}
