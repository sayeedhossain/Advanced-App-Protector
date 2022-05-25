package io.sayeed.hossain.crashreporter.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;


public class AppUtils {
    public static String getDeviceDetails(Context context) {
        return "Device Information\n"
                + "\nAPP.VERSION : " + getAppVersion(context)
                + "\nVERSION.RELEASE : " + Build.VERSION.RELEASE
                + "\nVERSION.INCREMENTAL : " + Build.VERSION.INCREMENTAL
                + "\nVERSION.SDK.NUMBER : " + Build.VERSION.SDK_INT
               // + "\nBRAND : " + Build.BRAND
                + "\nCPU_ABI : " + Build.CPU_ABI
                + "\nCPU_ABI2 : " + Build.CPU_ABI2
                + "\nDISPLAY : " + Build.DISPLAY
               // + "\nMANUFACTURER : " + Build.MANUFACTURER
               // + "\nMODEL : " + Build.MODEL
                + "\nPRODUCT : " + Build.PRODUCT;
    }



    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
