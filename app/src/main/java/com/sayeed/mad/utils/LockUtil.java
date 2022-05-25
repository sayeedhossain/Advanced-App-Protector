package com.sayeed.mad.utils;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.sayeed.mad.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class LockUtil {

    public static boolean isStatAccessPermissionSet(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
                return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }


    public static boolean isNoOption(@NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }
        return false;
    }


    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    public static boolean isNotificationSettingOn(Context mContext) {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int width,int height) {
        int w = 20;
        int h = 20;
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        int[] pixex = new int[w * h];
        List<Integer> trIndexs = new ArrayList<Integer>();
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int color = bitmap.getPixel(j, i);
                int alpha = Color.alpha(color);
                if (alpha < 200) {
                    trIndexs.add(i * h + j);
                } else if (trIndexs.size() > 0) {
                    for (Integer tr : trIndexs) {
                        pixex[tr] = color;
                    }
                    trIndexs.clear();
                    pixex[i * h + j] = color;
                } else {
                    pixex[i * h + j] = color;
                }
            }
        }

        Bitmap bitmap2 = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap2);
        RectF rectF = new RectF(0, 0,width,height);
        canvas2.drawBitmap(Bitmap.createBitmap(pixex, w, h, Bitmap.Config.ARGB_8888), null, rectF, null);
        return bitmap2;
    }

    private static final String TAG="LockUtil";
    public static void blur(Context mContent, @NonNull Bitmap bkg, View view,int width,int height) {
        //long startMs = System.currentTimeMillis();
        float radius = 50;
        float scaleFactor = 8;
        Bitmap overlay = Bitmap.createBitmap(
                (int) (width / scaleFactor),
                (int) (height/ scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackgroundDrawable(new BitmapDrawable(mContent.getResources(), overlay));
    }

    public static Bitmap big(@NonNull Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(2.5f, 2.5f);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 4,
                bitmap.getHeight() / 4, bitmap.getWidth() / 2 - 1,
                bitmap.getHeight() / 2 - 1, matrix, true);
        return handleImage(resizeBmp, 85);
    }

    public static Bitmap handleImage(Bitmap bm, int hue) {
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix mHueMatrix = new ColorMatrix();
        ColorMatrix mAllMatrix = new ColorMatrix();
        float mHueValue = hue * 1.0F / 127;
        mHueMatrix.reset();
        mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);

        mAllMatrix.reset();
        mAllMatrix.postConcat(mHueMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;
    }

    public static void goHome(BaseActivity activity) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(homeIntent);
        activity.finish();
    }


}
