package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.e(TAG, "Downloading using service");
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    sendProgress(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            sendProgress(InitSplashActivity.DownloadState.DONE, 100);
        } catch (Exception e) {
            sendProgress(InitSplashActivity.DownloadState.ERROR, 100);
            Log.e(TAG, "Failed downloading" + e, e);
        }
    }

    private void sendProgress(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent intent = new Intent("HomeworkDownloadService");
        intent.putExtra("downloadState", downloadState);
        intent.putExtra("progress", progress);
        Log.d(TAG, "Download state " + downloadState + " progress " + progress);
        sendBroadcast(intent);
    }

    private static final String TAG = "DownloadService";
}
