package com.sayeed.mad.services;

import android.accessibilityservice.AccessibilityService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityEvent;

import com.sayeed.mad.receiver.LockRestarterBroadcastReceiver;

public class LockAccessibilityService extends AccessibilityService {


    private static String mForegroundPackageName;
    private static LockAccessibilityService mInstance = null;

    public LockAccessibilityService() {
    }

    public static LockAccessibilityService getInstance() {
        if (mInstance == null) {
            synchronized (LockAccessibilityService.class) {
                if (mInstance == null) {
                    mInstance = new LockAccessibilityService();
                }
            }
        }
        return mInstance;
    }


    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {

        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    @Override
    public void onAccessibilityEvent(@NonNull AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                mForegroundPackageName = event.getPackageName().toString();
            }
        }
    }

    public String getForegroundPackage() {
        return mForegroundPackageName;
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent =new Intent(this, LockRestarterBroadcastReceiver.class);
        intent.putExtra("type","accessservice");
        sendBroadcast(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }

}
