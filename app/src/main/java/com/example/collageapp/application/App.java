package com.example.collageapp.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by Yue on 2022/10/27.
 */
public class App extends Application {

    public static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
    }
}
