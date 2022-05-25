package com.sayeed.mad.utils;

import android.view.Window;
import android.view.WindowManager;


public class AppUtils {

    public static void hideStatusBar(Window window, boolean enable) {
        WindowManager.LayoutParams p = window.getAttributes();
        if (enable)

            p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        else

            p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);

        window.setAttributes(p);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}
