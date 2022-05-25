package com.sayeed.mad.mvp.p;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.model.CommLockInfo;
import com.sayeed.mad.db.CommLockInfoManager;
import com.sayeed.mad.mvp.contract.MainContract;
import com.sayeed.mad.utils.SpUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private PackageManager mPackageManager;
    private CommLockInfoManager mLockInfoManager;
    private Context mContext;
    private LoadAppInfoAsyncTask mLoadAppInfo;
    private LoadLockAsyncTask mLoadLockAsyncTask;

    public MainPresenter(MainContract.View view, Context mContext) {
        mView = view;
        this.mContext = mContext;
        mPackageManager = mContext.getPackageManager();
        mLockInfoManager = new CommLockInfoManager(mContext);
    }

    @Override
    public void loadAppInfo(Context context, boolean isSort) {
        mLoadAppInfo = new LoadAppInfoAsyncTask(isSort);
        mLoadAppInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loadLockAppInfo(Context context) {
        mLoadLockAsyncTask = new LoadLockAsyncTask();
        mLoadLockAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        if (mLoadAppInfo != null && mLoadAppInfo.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadAppInfo.cancel(true);
        }
        if (mLoadLockAsyncTask != null && mLoadLockAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadLockAsyncTask.cancel(true);
        }
    }

    private class LoadAppInfoAsyncTask extends AsyncTask<Void, String, List<CommLockInfo>> {

        private boolean isSort = false;

        public LoadAppInfoAsyncTask(boolean isSort) {
            this.isSort = isSort;
        }

        @Override
        protected List<CommLockInfo> doInBackground(Void... params) {
            List<CommLockInfo> commLockInfos = mLockInfoManager.getAllCommLockInfos();
            Iterator<CommLockInfo> infoIterator = commLockInfos.iterator();
            int favoriteNum = 0;
            int sysAppNum = 0;
            int userAppNum = 0;

            while (infoIterator.hasNext()) {
                CommLockInfo info = infoIterator.next();
                try {
                    ApplicationInfo appInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(appInfo) == null) {
                        infoIterator.remove();
                        continue;
                    } else {
                        info.setAppInfo(appInfo);
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            info.setSysApp(true);
                            info.setTopTitle("System Application");
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle("User application");
                        }
                    }

                    if (info.isLocked()) {
                        favoriteNum++;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }
            SpUtil.getInstance().putInt(AppConstants.LOCK_FAVITER_NUM, favoriteNum);

            if (isSort) {
                List<CommLockInfo> sysList = new ArrayList<>();
                List<CommLockInfo> userList = new ArrayList<>();
                for (CommLockInfo info : commLockInfos) {
                    if (info.isSysApp()) {
                        sysList.add(info);
                        sysAppNum++;
                    } else {
                        userList.add(info);
                        userAppNum++;
                    }
                }
                SpUtil.getInstance().putInt(AppConstants.LOCK_SYS_APP_NUM, sysAppNum);
                SpUtil.getInstance().putInt(AppConstants.LOCK_USER_APP_NUM, userAppNum);
                commLockInfos.clear();
                commLockInfos.addAll(sysList);
                commLockInfos.addAll(userList);
            }
            return commLockInfos;
        }

        @Override
        protected void onPostExecute(List<CommLockInfo> commLockInfos) {
            super.onPostExecute(commLockInfos);
            mView.loadAppInfoSuccess(commLockInfos);
        }
    }

    private class LoadLockAsyncTask extends AsyncTask<String, Void, List<CommLockInfo>> {

        @NonNull
        @Override
        protected List<CommLockInfo> doInBackground(String... params) {
            List<CommLockInfo> commLockInfos = mLockInfoManager.getAllCommLockInfos();
            Iterator<CommLockInfo> infoIterator = commLockInfos.iterator();

            while (infoIterator.hasNext()) {
                CommLockInfo info = infoIterator.next();
                try {
                    ApplicationInfo appInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(appInfo) == null) {
                        infoIterator.remove();
                        continue;
                    } else {
                        info.setAppInfo(appInfo);
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            info.setSysApp(true);
                            info.setTopTitle("System Application");
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle("User Application");
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }

            List<CommLockInfo> list = new ArrayList<>();
            for (CommLockInfo info : commLockInfos) {
                if (info.isLocked()) {
                    list.add(info);
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<CommLockInfo> commLockInfos) {
            super.onPostExecute(commLockInfos);
            mView.loadAppInfoSuccess(commLockInfos);
        }

    }
}
