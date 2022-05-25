package com.sayeed.mad.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.Nullable;

import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.db.CommLockInfoManager;
import com.sayeed.mad.model.CommLockInfo;
import com.sayeed.mad.model.FaviterInfo;
import com.sayeed.mad.utils.NotificationUtil;
import com.sayeed.mad.utils.SpUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LoadAppListService extends IntentService {

    public static final String ACTION_START_LOAD_APP = "io.github.subhamtyagi.privacyapplock.service.action.LOADAPP";
    long time = 0;
    private PackageManager mPackageManager;
    @Nullable
    private CommLockInfoManager mLockInfoManager;

    public LoadAppListService() {
        super("LoadAppListService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPackageManager = getPackageManager();
        mLockInfoManager = new CommLockInfoManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotification(this,"App Lock","App lock Services running in background");
        }
    }

    @Override
    protected void onHandleIntent(Intent handleIntent) {

        time = System.currentTimeMillis();
        boolean isInitFaviter = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_INIT_FAVITER, false);
        boolean isInitDb = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_INIT_DB, false);
        if (!isInitFaviter) {
            SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_INIT_FAVITER, true);
            initFavoriteApps();
        }


        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent, 0);

        if (isInitDb) {
            List<ResolveInfo> appList = new ArrayList<>();
            List<CommLockInfo> dbList = null;
            if (mLockInfoManager != null) {
                dbList = mLockInfoManager.getAllCommLockInfos();
            }

            for (ResolveInfo resolveInfo : resolveInfos) {
                if (!resolveInfo.activityInfo.packageName.equals(AppConstants.APP_PACKAGE_NAME)) {
                    appList.add(resolveInfo);
                }
            }


            if (appList.size() > dbList.size()) {
                List<ResolveInfo> reslist = new ArrayList<>();
                HashMap<String, CommLockInfo> hashMap = new HashMap<>();
                for (CommLockInfo info : dbList) {
                    hashMap.put(info.getPackageName(), info);
                }
                for (ResolveInfo info : appList) {
                    if (!hashMap.containsKey(info.activityInfo.packageName)) {
                        reslist.add(info);
                    }
                }
                try {
                    if (reslist.size() != 0)
                        mLockInfoManager.instanceCommLockInfoTable(reslist);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (appList.size() < dbList.size()) {
                List<CommLockInfo> commlist = new ArrayList<>();
                HashMap<String, ResolveInfo> hashMap = new HashMap<>();
                for (ResolveInfo info : appList) {
                    hashMap.put(info.activityInfo.packageName, info);
                }
                for (CommLockInfo info : dbList) {
                    if (!hashMap.containsKey(info.getPackageName())) {
                        commlist.add(info);
                    }
                }

                if (commlist.size() != 0)
                    mLockInfoManager.deleteCommLockInfoTable(commlist);
            }
        } else {
            SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_INIT_DB, true);
            try {
                if (mLockInfoManager != null) {
                    mLockInfoManager.instanceCommLockInfoTable(resolveInfos);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLockInfoManager = null;
    }


    public void initFavoriteApps() {
        List<String> packageList = new ArrayList<>();
        List<FaviterInfo> faviterInfos = new ArrayList<>();


        packageList.add("com.android.gallery3d");
        packageList.add("com.android.mms");
        packageList.add("com.android.contacts");
        packageList.add("com.android.email");
        packageList.add("com.android.vending");

        packageList.add("com.android.dialer");
        packageList.add("com.android.camera");

        packageList.add("com.google.android.apps.photos");
        packageList.add("com.google.android.gm");
        packageList.add("com.google.android.youtube");
        packageList.add("com.google.android.apps.tachyon");//duo


        packageList.add("org.thoughtcrime.securesms");//signal
        packageList.add("org.telegram.messenger");
        packageList.add("com.whatsapp");
        packageList.add("com.twitter.android");
        packageList.add("com.facebook.katana");
        packageList.add("com.facebook.orca");


        packageList.add("org.fdroid.fdroid");
        packageList.add("org.mozilla.firefox");
        packageList.add("org.schabi.newpipe");
        packageList.add("eu.faircode.email");//fair mail
        packageList.add("com.simplemobile.gallery.pro");// simple gallery

        packageList.add("com.mediatek.filemanager");
        packageList.add("com.sec.android.gallery3d");
        packageList.add("com.sec.android.app.myfiles");

        for (String packageName : packageList) {
            FaviterInfo info = new FaviterInfo();
            info.setPackageName(packageName);
            faviterInfos.add(info);
        }

        DataSupport.deleteAll(FaviterInfo.class);
        DataSupport.saveAll(faviterInfos);
    }
}
