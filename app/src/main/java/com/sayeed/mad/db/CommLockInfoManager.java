package com.sayeed.mad.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.model.CommLockInfo;
import com.sayeed.mad.model.FaviterInfo;
import com.sayeed.mad.utils.DataUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.litepal.crud.DataSupport.where;



public class CommLockInfoManager {

    private PackageManager mPackageManager;
    private Context mContext;
    @NonNull
    private Comparator commLockInfoComparator = new Comparator() {

        @Override
        public int compare(Object lhs, Object rhs) {
            CommLockInfo leftCommLockInfo = (CommLockInfo) lhs;
            CommLockInfo rightCommLockInfo = (CommLockInfo) rhs;

            if (leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return 1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            }else if (!leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            }
            return 0;
        }
    };


    public CommLockInfoManager(Context mContext) {
        this.mContext = mContext;
        mPackageManager = mContext.getPackageManager();
    }

    public synchronized List<CommLockInfo> getAllCommLockInfos() {
        List<CommLockInfo> commLockInfos = DataSupport.findAll(CommLockInfo.class);
        Collections.sort(commLockInfos, commLockInfoComparator);
        return commLockInfos;
    }

    public synchronized void deleteCommLockInfoTable(@NonNull List<CommLockInfo> commLockInfos) {
        for (CommLockInfo info : commLockInfos) {
            DataSupport.deleteAll(CommLockInfo.class, "packageName = ?", info.getPackageName());
        }
    }

    public synchronized void instanceCommLockInfoTable(@NonNull List<ResolveInfo> resolveInfos) throws PackageManager.NameNotFoundException {
        List<CommLockInfo> list = new ArrayList<>();

        for (ResolveInfo resolveInfo : resolveInfos) {
            boolean isfaviterApp = isHasFaviterAppInfo(resolveInfo.activityInfo.packageName);
            CommLockInfo commLockInfo = new CommLockInfo(resolveInfo.activityInfo.packageName, false, isfaviterApp);
            ApplicationInfo appInfo = mPackageManager.getApplicationInfo(commLockInfo.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            String appName = mPackageManager.getApplicationLabel(appInfo).toString();

            if (!commLockInfo.getPackageName().equals(AppConstants.APP_PACKAGE_NAME) ) {
                if (isfaviterApp) {
                    commLockInfo.setLocked(true);
                } else {
                    commLockInfo.setLocked(false);
                }
                commLockInfo.setAppName(appName);
                commLockInfo.setSetUnLock(false);

                list.add(commLockInfo);
            }
        }
        list = DataUtil.clearRepeatCommLockInfo(list);

        DataSupport.saveAll(list);
    }

    private boolean isHasFaviterAppInfo(String packageName) {
        List<FaviterInfo> infos = DataSupport.where("packageName = ?", packageName).find(FaviterInfo.class);
        return infos.size() > 0;
    }

    public void lockCommApplication(String packageName) {
        updateLockStatus(packageName, true);
    }

    public void unlockCommApplication(String packageName) {
        updateLockStatus(packageName, false);
    }

    private void updateLockStatus(String packageName, boolean isLock) {
        ContentValues values = new ContentValues();
        values.put("isLocked", isLock);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

    public boolean isSetUnLock(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);
        for (CommLockInfo commLockInfo : lockInfos) {
            if (commLockInfo.isSetUnLock()) {
                return true;
            }
        }
        return false;
    }

    public boolean isLockedPackageName(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);
        for (CommLockInfo commLockInfo : lockInfos) {
            if (commLockInfo.isLocked()) {
                return true;
            }
        }
        return false;
    }

    public List<CommLockInfo> queryBlurryList(String appName) {
        return DataSupport.where("appName like ?", "%" + appName + "%").find(CommLockInfo.class);
    }

    public void setIsUnLockThisApp(String packageName, boolean isSetUnLock) {
        ContentValues values = new ContentValues();
        values.put("isSetUnLock", isSetUnLock);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

}
