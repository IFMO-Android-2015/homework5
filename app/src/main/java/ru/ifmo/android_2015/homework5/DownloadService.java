package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Лиза on 18.12.2015.
 */
public class DownloadService extends IntentService {
    final String LOG_TAG = "myLogs";

    public DownloadService() {
        super("DownloadService");
    }

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    ProgressUpdate(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            ProgressUpdate(InitSplashActivity.DownloadState.DONE, 100);
        } catch (Exception e) {
            ProgressUpdate(InitSplashActivity.DownloadState.ERROR, 100);
        }
    }

    private void ProgressUpdate(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent intent = new Intent(InitSplashActivity.BROADCAST);
        intent.putExtra(InitSplashActivity.DOWNLOAD, downloadState);
        intent.putExtra(InitSplashActivity.PROGRESS, progress);
        sendBroadcast(intent);
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

}
