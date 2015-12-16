package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;



public class DownloadService extends IntentService implements ProgressCallback {
    int state;
    Intent intent = new Intent(InitSplashActivity.BROADCAST_ACTION);

    public void onCreate() {
        Log.d(TAG, " onCreate in service");
        super.onCreate();
    }

    public void onDestroy() {
        Log.d(TAG, " onDestroy in service");
        super.onDestroy();
    }

    public DownloadService() {
        super("myservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            state = InitSplashActivity.DOWNLOADING;
            InitSplashActivity.downloadFile(this, this);
            state = InitSplashActivity.DONE;

        } catch (Exception e) {
            Log.e(TAG, "Error downloading file: " + e, e);
            state = InitSplashActivity.ERROR;
        }
        intent.putExtra(InitSplashActivity.STATUS, state);
        intent.putExtra(InitSplashActivity.PROGRESS, 100);
        sendBroadcast(intent);
        stopSelf();

    }
    @Override
    public void onProgressChanged(int progress) {
        if (state != InitSplashActivity.DONE) {
            intent.putExtra(InitSplashActivity.STATUS, state);
            intent.putExtra(InitSplashActivity.PROGRESS, progress);
        }
        sendBroadcast(intent);
    }

    private final String TAG = "DownloadService";
}