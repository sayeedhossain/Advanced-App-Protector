package com.sayeed.mad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.services.BackgroundManager;
import com.sayeed.mad.services.LockService;
import com.sayeed.mad.utils.SpUtil;

public class LockRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean lockState=SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE);
        if (intent != null && lockState) {
            String type = intent.getStringExtra("type");
            if (type.contentEquals("lockservice"))
                BackgroundManager.getInstance().init(context).startService(LockService.class);

            else if (type.contentEquals("startlockserviceFromAM")) {
                if (!BackgroundManager.getInstance().init(context).isServiceRunning(LockService.class)) {
                    BackgroundManager.getInstance().init(context).startService(LockService.class);
                }

                BackgroundManager.getInstance().init(context).startAlarmManager();
            }
        }
    }
}
