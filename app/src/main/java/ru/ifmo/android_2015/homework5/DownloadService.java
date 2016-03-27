package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    publishProgress(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            publishProgress(InitSplashActivity.DownloadState.DONE, 100);
        } catch (Exception e) {
            publishProgress(InitSplashActivity.DownloadState.ERROR, 100);
        }
    }

    private void publishProgress(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent intent = new Intent("service");
        intent.putExtra("state", downloadState);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }
}