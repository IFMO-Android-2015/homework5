package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadService extends IntentService {
    public DownloadService() {
        super(TAG);
    }

    private String filter;

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            filter = InitSplashActivity.EXTRA_FILTER;
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
        Intent intent = new Intent(filter);
        intent.putExtra(InitSplashActivity.EXTRA_STATE, downloadState);
        intent.putExtra(InitSplashActivity.EXTRA_PROGRESS, progress);
        Log.d(TAG, "Download state " + downloadState + " progress " + progress);
        sendBroadcast(intent);
    }

    private static final String TAG = "Service";
}