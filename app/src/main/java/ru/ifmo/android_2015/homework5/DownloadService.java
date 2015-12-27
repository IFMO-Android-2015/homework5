package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Богдан on 27.12.2015.
 */
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
                    send(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            send(InitSplashActivity.DownloadState.DONE, 100);
        } catch (Exception e) {
            send(InitSplashActivity.DownloadState.ERROR, 0);
        }
    }

    private void send(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent intent = new Intent("service");
        intent.putExtra("state", downloadState);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
