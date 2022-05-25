package com.sayeed.mad.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sayeed.mad.LockApplication;
import com.sayeed.mad.activities.lock.GestureUnlockActivity;
import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.db.CommLockInfoManager;
import com.sayeed.mad.receiver.LockRestarterBroadcastReceiver;
import com.sayeed.mad.utils.NotificationUtil;
import com.sayeed.mad.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public class LockService extends IntentService {

    public static final String UNLOCK_ACTION = "UNLOCK_ACTION";
    public static final String LOCK_SERVICE_LASTTIME = "LOCK_SERVICE_LASTTIME";
    public static final String LOCK_SERVICE_LASTAPP = "LOCK_SERVICE_LASTAPP";
    private static final String TAG = "LockService";
    public static boolean isActionLock = false;
    public boolean threadIsTerminate = false;
    @Nullable
    public String savePkgName;
    UsageStatsManager sUsageStatsManager;
    Timer timer = new Timer();

    private long lastUnlockTimeSeconds = 0;
    private String lastUnlockPackageName = "";
    private boolean lockState;
    private ServiceReceiver mServiceReceiver;
    private CommLockInfoManager mLockInfoManager;


    @Nullable
    private ActivityManager activityManager;

    public LockService() {
        super("LockService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lockState = SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE);
        mLockInfoManager = new CommLockInfoManager(this);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        mServiceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(UNLOCK_ACTION);
        registerReceiver(mServiceReceiver, filter);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            sUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        }

        threadIsTerminate = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotification(this, "App Lock", "App Lock running in background");
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        runForever();
    }

    private void runForever() {

        while (threadIsTerminate) {
            String packageName = getLauncherTopApp(LockService.this, activityManager);
            if (lockState && !TextUtils.isEmpty(packageName) && !inWhiteList(packageName)) {
                boolean isLockOffScreenTime = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, false);
                boolean isLockOffScreen = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN, false);
                savePkgName = SpUtil.getInstance().getString(AppConstants.LOCK_LAST_LOAD_PKG_NAME, "");

                if (isLockOffScreenTime && !isLockOffScreen) {
                    long time = SpUtil.getInstance().getLong(AppConstants.LOCK_CURR_MILLISECONDS, 0);
                    long leaverTime = SpUtil.getInstance().getLong(AppConstants.LOCK_APART_MILLISECONDS, 0);
                    if (!TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName) && !savePkgName.equals(packageName)) {
                        if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                            boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                            if (!isSetUnLock) {
                                if (System.currentTimeMillis() - time > leaverTime) {
                                    mLockInfoManager.lockCommApplication(savePkgName);
                                }
                            }
                        }
                    }
                }

                if (isLockOffScreenTime && isLockOffScreen) {
                    long time = SpUtil.getInstance().getLong(AppConstants.LOCK_CURR_MILLISECONDS, 0);
                    long leaverTime = SpUtil.getInstance().getLong(AppConstants.LOCK_APART_MILLISECONDS, 0);
                    if (!TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName) && !savePkgName.equals(packageName)) {
                        if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                            boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                            if (!isSetUnLock) {
                                if (System.currentTimeMillis() - time > leaverTime) {
                                    mLockInfoManager.lockCommApplication(savePkgName);
                                }
                            }
                        }
                    }
                }


                if (!isLockOffScreenTime && isLockOffScreen && !TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName)) {
                    if (!savePkgName.equals(packageName)) {
                        isActionLock = false;
                        if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                            boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                            if (!isSetUnLock) {
                                mLockInfoManager.lockCommApplication(savePkgName);
                            }
                        }
                    } else {
                        isActionLock = true;
                    }
                }
                if (!isLockOffScreenTime && !isLockOffScreen && !TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName) && !savePkgName.equals(packageName)) {
                    if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                        boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                        if (!isSetUnLock) {
                            mLockInfoManager.lockCommApplication(savePkgName);
                        }
                    }
                }
                if (mLockInfoManager.isLockedPackageName(packageName)) {
                    passwordLock(packageName);
                    continue;
                }
            }
            try {
                Thread.sleep(210);
            } catch (Exception ignore) {
            }
        }
    }

    private boolean inWhiteList(String packageName) {
        return packageName.equals(AppConstants.APP_PACKAGE_NAME);
    }

    public String getLauncherTopApp(@NonNull Context context, @NonNull ActivityManager activityManager) {
        //isLockTypeAccessibility = SpUtil.getInstance().getBoolean(AppConstants.LOCK_TYPE, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    @NonNull
    private List<String> getHomes() {
        List<String> names = new ArrayList<>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    private void passwordLock(String packageName) {
        LockApplication.getInstance().clearAllActivity();
        Intent intent = new Intent(this, GestureUnlockActivity.class);

        intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName);
        intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadIsTerminate = false;
        timer.cancel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.cancelNotification(this);
        }
        lockState = SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE);
        if (lockState) {
            Intent intent = new Intent(this, LockRestarterBroadcastReceiver.class);
            intent.putExtra("type", "lockservice");
            sendBroadcast(intent);
        }
        unregisterReceiver(mServiceReceiver);
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        threadIsTerminate = false;
        timer.cancel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.cancelNotification(this);
        }
        lockState = SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE);
        if (lockState) {
            Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
            restartServiceTask.setPackage(getPackageName());
            PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1495, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            myAlarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1500,
                    restartPendingIntent);
        }

    }

    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();

            boolean isLockOffScreen = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN, false);
            boolean isLockOffScreenTime = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, false);

            switch (action) {
                case UNLOCK_ACTION:
                    lastUnlockPackageName = intent.getStringExtra(LOCK_SERVICE_LASTAPP);
                    lastUnlockTimeSeconds = intent.getLongExtra(LOCK_SERVICE_LASTTIME, lastUnlockTimeSeconds);
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    SpUtil.getInstance().putLong(AppConstants.LOCK_CURR_MILLISECONDS, System.currentTimeMillis());

                    if (!isLockOffScreenTime && isLockOffScreen) {
                        String savePkgName = SpUtil.getInstance().getString(AppConstants.LOCK_LAST_LOAD_PKG_NAME, "");
                        if (!TextUtils.isEmpty(savePkgName)) {
                            if (isActionLock) {
                                mLockInfoManager.lockCommApplication(lastUnlockPackageName);
                            }
                        }
                    }
                    break;
            }
        }
    }
}
