package com.example.collageapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏 导航栏 透明 从theme.xml指定了
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        //全屏显示 让应用内容可以绘制到状态栏
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //状态栏的icon颜色 从theme.xml指定了 xml里的windowLightStatusBar
//        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
//        insetsController.setAppearanceLightStatusBars(false);


        setContentView(setLayoutResId());

        applyInsets(setContentIdRes());

        create();
    }


    private void applyInsets(int viewId) {
        View layout = findViewById(viewId);
        if (layout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(layout, (v, windowInsets) -> {
                //这里mask 用displayCutout和systemBars，这样系统的导航栏和缺角屏的距离都能判断到了
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.systemBars());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();

                mlp.leftMargin = insets.left;
                mlp.topMargin = insets.top;
                mlp.rightMargin = insets.right;
                mlp.bottomMargin = insets.bottom;
                v.setLayoutParams(mlp);

                //这个回调在某些机器上只能执行一个 因此第2个布局的inset也在这里设置了
                View layout2 = findViewById(setContentIdRes2());
                if (layout2 != null) {
                    ViewGroup.MarginLayoutParams mlp2 = (ViewGroup.MarginLayoutParams) layout2.getLayoutParams();
                    mlp2.leftMargin = insets.left;
                    mlp2.topMargin = insets.top;
                    mlp2.rightMargin = insets.right;
                    mlp2.bottomMargin = insets.bottom;
                    layout2.setLayoutParams(mlp2);
                }

                return WindowInsetsCompat.CONSUMED;
            });

        }
    }


    //启用沉浸模式
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            //沉浸模式 隐藏状态栏
//            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
//            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
//            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
//        }
//    }

    //另一个适配刘海和导航栏的布局
    protected int setContentIdRes2() {
        return 0;
    }


    @LayoutRes
    protected abstract int setLayoutResId();

    @IdRes
    protected abstract int setContentIdRes();


    protected abstract void create();


}