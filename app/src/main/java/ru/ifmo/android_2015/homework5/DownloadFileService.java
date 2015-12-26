package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadFileService extends IntentService {

    private static final String TAG = "DownloadService";

    public DownloadFileService() {
        super(TAG);
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
            Log.e(TAG, "Failed downloading" + e, e);
            sendProgress(InitSplashActivity.DownloadState.ERROR, 100);
        }
    }

    private void sendProgress(InitSplashActivity.DownloadState downloadState, int progress) {
        Log.d(TAG, "Download state " + downloadState + " progress " + progress);
        Intent intent = new Intent("downloadservice");
        intent.putExtra("state", downloadState);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }
}
