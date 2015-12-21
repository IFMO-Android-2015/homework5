package ru.ifmo.android_2015.homework5;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * This instance created to bind LeakCanary with ApplicationContext to track possible
 * memory-leaks while InitSplashActivity recreates
 *
 * @author creed
 * @date 21.12.15
 */
public class MyApplication extends Application {
    public MyApplication() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
