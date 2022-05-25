

package io.sayeed.hossain.crashreporter;

import android.content.Context;
import android.content.Intent;

import io.sayeed.hossain.crashreporter.ui.CrashReporterActivity;
import io.sayeed.hossain.crashreporter.utils.CrashReporterExceptionHandler;
import io.sayeed.hossain.crashreporter.utils.CrashReporterNotInitializedException;
import io.sayeed.hossain.crashreporter.utils.CrashUtil;

public class CrashReporter {

    private static Context applicationContext;

    private static String crashReportPath;

    private static boolean isNotificationEnabled = true;

    private CrashReporter() {

    }

    public static void initialize(Context context) {
        applicationContext = context;
        setUpExceptionHandler();
    }

    public static void initialize(Context context, String crashReportSavePath) {
        applicationContext = context;
        crashReportPath = crashReportSavePath;
        setUpExceptionHandler();
    }

    private static void setUpExceptionHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashReporterExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashReporterExceptionHandler());
        }
    }

    public static Context getContext() {
        if (applicationContext == null) {
            try {
                throw new CrashReporterNotInitializedException("Initialize CrashReporter : call CrashReporter.initialize(context, crashReportPath)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applicationContext;
    }

    public static String getCrashReportPath() {
        return crashReportPath;
    }

    public static boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }


    public static void logException(Exception exception) {
        CrashUtil.logException(exception);
    }

    public static Intent getLaunchIntent() {
        return new Intent(applicationContext, CrashReporterActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void disableNotification() {
        isNotificationEnabled = false;
    }

}
