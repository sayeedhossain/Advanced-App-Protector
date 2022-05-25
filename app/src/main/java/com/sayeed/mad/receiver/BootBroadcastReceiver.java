package com.sayeed.mad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.services.BackgroundManager;
import com.sayeed.mad.services.LoadAppListService;
import com.sayeed.mad.services.LockService;
import com.sayeed.mad.utils.LogUtil;
import com.sayeed.mad.utils.SpUtil;


public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        LogUtil.i("Boot service....");
        BackgroundManager.getInstance().init(context).startService(LoadAppListService.class);
        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            BackgroundManager.getInstance().init(context).startService(LockService.class);
            BackgroundManager.getInstance().init(context).startAlarmManager();
        }
    }
}
