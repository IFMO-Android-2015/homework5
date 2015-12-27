package ru.ifmo.android_2015.homework5;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DownloadService extends Service implements Runnable  {
    private static final String TAG = DownloadService.class.getSimpleName();

    private Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        try {
            File destFile = FileUtils.createTempExternalFile(this.getApplicationContext(), "gz");
            DownloadUtils.downloadFile(InitSplashActivity.CITIES_GZ_URL, destFile, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
