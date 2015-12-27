package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;

public class DownloadingService extends IntentService {

    public DownloadingService() {
        super("DownloadingService");
    }

    @Override
    protected void onHandleIntent(Intent ev) {
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    send(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            send(InitSplashActivity.DownloadState.DONE, 120);
        } catch (Exception e) {
            send(InitSplashActivity.DownloadState.ERROR, 120);
        }
    }

    private void send(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent ev = new Intent("service");
        ev.putExtra("state", downloadState);
        ev.putExtra("progress", progress);
        sendBroadcast(ev);
    }

    public void onDestroy() {
        super.onDestroy();
    }

}