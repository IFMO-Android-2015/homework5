package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadFileService extends IntentService {

    private static final String TAG = "DownloadFileService";

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
        Log.d(TAG, "Download state " + downloadState + " Progress " + progress);
        Intent intent = new Intent("downloadfileservice");
        intent.putExtra("downloadstate", downloadState);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }
}
