package com.example.collageapp.util;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by Yue on 2022/10/25.
 */
public class SizeUtil {

   public static int dp2px(final float dpValue) {
      final float scale = Resources.getSystem().getDisplayMetrics().density;
      return (int) (dpValue * scale + 0.5f);
   }


   public static int px2dp(final float pxValue) {
      final float scale = Resources.getSystem().getDisplayMetrics().density;
      return (int) (pxValue / scale + 0.5f);
   }

   public static int getScreenWidth(Application application) {
      WindowManager wm = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
      if (wm == null) return -1;
      Point point = new Point();
      wm.getDefaultDisplay().getRealSize(point);
      return point.x;
   }


   public static int getScreenHeight(Application application) {
      WindowManager wm = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
      if (wm == null) return -1;
      Point point = new Point();
      wm.getDefaultDisplay().getRealSize(point);
      return point.y;
   }


   public static WindowSizeClass computeWindowSizeClasses(int px) {
      float widthDp = px / Resources.getSystem().getDisplayMetrics().density;
      return computeWindowSizeClasses(widthDp);
   }

   public static WindowSizeClass computeWindowSizeClasses(float dp) {
      if (dp < 600f) {
         return WindowSizeClass.COMPACT;
      } else if (dp < 840f) {
         return  WindowSizeClass.MEDIUM;
      } else {
         return  WindowSizeClass.EXPANDED;
      }
   }

}
