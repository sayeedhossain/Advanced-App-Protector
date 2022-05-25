package com.sayeed.mad.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import com.sayeed.mad.R;
import com.sayeed.mad.activities.main.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NotificationUtil {

    private static final String NOTIFICATION_CHANNEL_ID = "10101";


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createNotification(Service mContext,String title, String message) {
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 112 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "App lock background task ", importance);
        mNotificationManager.createNotificationChannel(notificationChannel);

        Notification.Builder mBuilder = new Notification.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);


        mContext.startForeground(145,mBuilder.build());
    }

    public static void cancelNotification(Service mContext){
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(145);
    }


}