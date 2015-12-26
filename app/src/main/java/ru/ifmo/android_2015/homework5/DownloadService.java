package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent i) {
        Intent intent = new Intent("ru.ifmo.android_2015.homework5");
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    Intent intent = new Intent("ru.ifmo.android_2015.homework5");
                    intent.putExtra("downloadState", InitSplashActivity.DownloadState.DOWNLOADING);
                    intent.putExtra("progress", progress);
                    sendBroadcast(intent);
                }
            });
            intent.putExtra("downloadState", InitSplashActivity.DownloadState.DONE);
        } catch (Exception e) {
            intent.putExtra("downloadState", InitSplashActivity.DownloadState.ERROR);
        }
        intent.putExtra("progress", 100);
        sendBroadcast(intent);
    }
}
