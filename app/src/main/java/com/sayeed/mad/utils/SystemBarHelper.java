package com.sayeed.mad.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sayeed.mad.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;


public class SystemBarHelper {

  private static float DEFAULT_ALPHA = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                                       ? 0.2f
                                       : 0.3f;


  public static void tintStatusBar(@NonNull Activity activity, @ColorInt int statusBarColor) {

    tintStatusBar(activity, statusBarColor, DEFAULT_ALPHA);
  }



  public static void tintStatusBar(Activity activity,
                                   @ColorInt int statusBarColor,
                                   @FloatRange(from = 0.0, to = 1.0) float alpha) {

    tintStatusBar(activity.getWindow(), statusBarColor, alpha);
  }



  public static void tintStatusBar(@NonNull Window window, @ColorInt int statusBarColor) {

    tintStatusBar(window, statusBarColor, DEFAULT_ALPHA);
  }



  public static void tintStatusBar(@NonNull Window window,
                                   @ColorInt int statusBarColor,
                                   @FloatRange(from = 0.0, to = 1.0) float alpha) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
    } else {
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    ViewGroup decorView = (ViewGroup) window.getDecorView();
    ViewGroup contentView = window.getDecorView()
        .findViewById(Window.ID_ANDROID_CONTENT);
    View rootView = contentView.getChildAt(0);
    if (rootView != null) {
      ViewCompat.setFitsSystemWindows(rootView, true);
    }

    setStatusBar(decorView, statusBarColor, true);
    setTranslucentView(decorView, alpha);
  }

  public static void tintStatusBarForDrawer(@NonNull Activity activity, @NonNull DrawerLayout drawerLayout,
                                            @ColorInt int statusBarColor) {

    tintStatusBarForDrawer(activity, drawerLayout, statusBarColor, DEFAULT_ALPHA);
  }



  public static void tintStatusBarForDrawer(@NonNull Activity activity, @NonNull DrawerLayout drawerLayout,
                                            @ColorInt int statusBarColor,
                                            @FloatRange(from = 0.0, to = 1.0) float alpha) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }

    Window window = activity.getWindow();
    ViewGroup decorView = (ViewGroup) window.getDecorView();
    ViewGroup drawContent = (ViewGroup) drawerLayout.getChildAt(0);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
      drawerLayout.setStatusBarBackgroundColor(statusBarColor);

      int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
      systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
      systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
      window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    } else {
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    setStatusBar(decorView, statusBarColor, true, true);
    setTranslucentView(decorView, alpha);

    drawerLayout.setFitsSystemWindows(false);
    drawContent.setFitsSystemWindows(true);
    ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
    drawer.setFitsSystemWindows(false);
  }


  public static void immersiveStatusBar(@NonNull Activity activity) {

    immersiveStatusBar(activity, DEFAULT_ALPHA);
  }



  public static void immersiveStatusBar(Activity activity,
                                        @FloatRange(from = 0.0, to = 1.0) float alpha) {

    immersiveStatusBar(activity.getWindow(), alpha);
  }



  public static void immersiveStatusBar(@NonNull Window window) {

    immersiveStatusBar(window, DEFAULT_ALPHA);
  }



  public static void immersiveStatusBar(@NonNull Window window,
                                        @FloatRange(from = 0.0, to = 1.0) float alpha) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);

      int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
      systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
      systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
      window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    } else {
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    ViewGroup decorView = (ViewGroup) window.getDecorView();
    ViewGroup contentView = window.getDecorView()
        .findViewById(Window.ID_ANDROID_CONTENT);
    View rootView = contentView.getChildAt(0);
    int statusBarHeight = getStatusBarHeight(window.getContext());
    if (rootView != null) {
      FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rootView.getLayoutParams();
      ViewCompat.setFitsSystemWindows(rootView, true);
      lp.topMargin = -statusBarHeight;
      rootView.setLayoutParams(lp);
    }

    setTranslucentView(decorView, alpha);
  }


  public static void setStatusBarDarkMode(Activity activity) {

    setStatusBarDarkMode(activity.getWindow());
  }



  public static void setStatusBarDarkMode(@NonNull Window window) {

    if (isFlyme4Later()) {
      setStatusBarDarkModeForFlyme4(window, true);
    } else if (isMIUI6Later()) {
      setStatusBarDarkModeForMIUI6(window, true);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      setStatusBarDarkModeForM(window);
    }
  }


  @TargetApi(Build.VERSION_CODES.M)
  public static void setStatusBarDarkModeForM(Window window) {

    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(Color.TRANSPARENT);

    int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
    systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    window.getDecorView().setSystemUiVisibility(systemUiVisibility);
  }



  public static boolean setStatusBarDarkModeForFlyme4(@Nullable Window window, boolean dark) {

    boolean result = false;
    if (window != null) {
      try {
        WindowManager.LayoutParams e = window.getAttributes();
        Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField(
            "MEIZU_FLAG_DARK_STATUS_BAR_ICON");
        Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
        darkFlag.setAccessible(true);
        meizuFlags.setAccessible(true);
        int bit = darkFlag.getInt(null);
        int value = meizuFlags.getInt(e);
        if (dark) {
          value |= bit;
        } else {
          value &= ~bit;
        }

        meizuFlags.setInt(e, value);
        window.setAttributes(e);
        result = true;
      } catch (Exception var8) {
        Log.e("StatusBar", "setStatusBarDarkIcon: failed");
      }
    }

    return result;
  }



  public static void setStatusBarDarkModeForMIUI6(Window window, boolean darkmode) {

    Class<? extends Window> clazz = window.getClass();
    try {
      int darkModeFlag = 0;
      Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
      Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
      darkModeFlag = field.getInt(layoutParams);
      Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
      extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  private static void setStatusBar(@NonNull ViewGroup container, @ColorInt
      int statusBarColor, boolean visible, boolean addToFirst) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      View statusBarView = container.findViewById(R.id.statusbar_view);
      if (statusBarView == null) {
        statusBarView = new View(container.getContext());
        statusBarView.setId(R.id.statusbar_view);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext()));
        if (addToFirst) {
          container.addView(statusBarView, 0, lp);
        } else {
          container.addView(statusBarView, lp);
        }
      }

      statusBarView.setBackgroundColor(statusBarColor);
      statusBarView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
  }



  private static void setStatusBar(@NonNull ViewGroup container,
                                   @ColorInt int statusBarColor, boolean visible) {

    setStatusBar(container, statusBarColor, visible, false);
  }



  private static void setTranslucentView(@NonNull ViewGroup container,
                                         @FloatRange(from = 0.0, to = 1.0) float alpha) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      View translucentView = container.findViewById(R.id.translucent_view);
      if (translucentView == null) {
        translucentView = new View(container.getContext());
        translucentView.setId(R.id.translucent_view);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext()));
        container.addView(translucentView, lp);
      }

      translucentView.setBackgroundColor(Color.argb((int) (alpha * 255), 0, 0, 0));
    }
  }


  public static int getStatusBarHeight(Context context) {

    int result = 0;
    int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resId > 0) {
      result = context.getResources().getDimensionPixelSize(resId);
    }
    return result;
  }



  public static boolean isFlyme4Later() {

    return Build.FINGERPRINT.contains("Flyme_OS_4")
        || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
        ||
        Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find();
  }

  public static boolean isMIUI6Later() {

    try {
      Class<?> clz = Class.forName("android.os.SystemProperties");
      Method mtd = clz.getMethod("get", String.class);
      String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
      val = val.replaceAll("[vV]", "");
      int version = Integer.parseInt(val);
      return version >= 6;
    } catch (Exception e) {
      return false;
    }
  }



  public static void setHeightAndPadding(@NonNull Context context, @NonNull View view) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      ViewGroup.LayoutParams lp = view.getLayoutParams();
      lp.height += getStatusBarHeight(context);//增高
      view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
          view.getPaddingRight(), view.getPaddingBottom());
    }
  }



  public static void setPadding(@NonNull Context context, @NonNull View view) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
          view.getPaddingRight(), view.getPaddingBottom());
    }
  }



  public static void forceFitsSystemWindows(Activity activity) {

    forceFitsSystemWindows(activity.getWindow());
  }



  public static void forceFitsSystemWindows(Window window) {

    forceFitsSystemWindows(
        (ViewGroup) window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT));
  }


  public static void forceFitsSystemWindows(@NonNull ViewGroup viewGroup) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      int count = viewGroup.getChildCount();
      for (int i = 0; i < count; i++) {
        View view = viewGroup.getChildAt(i);
        if (view instanceof ViewGroup) {
          forceFitsSystemWindows((ViewGroup) view);
        } else {
          if (ViewCompat.getFitsSystemWindows(view)) {
            ViewCompat.setFitsSystemWindows(view, false);
          }
        }
      }
    }
  }
}