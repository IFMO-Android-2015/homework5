package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;

public class DownloadService extends IntentService {
    public final int prog=100;
    public DownloadService() {
        super("MyService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    Intent newIntent = new Intent("downloadFile");
                    newIntent.putExtra("state", InitSplashActivity.DownloadState.DOWNLOADING);
                    newIntent.putExtra("progress", progress);
                    sendBroadcast(newIntent);
                }
            });
            Intent newIntent = new Intent("downloadFile");
            newIntent.putExtra("state", InitSplashActivity.DownloadState.DONE);
            newIntent.putExtra("progress", prog);
            sendBroadcast(newIntent);

        } catch (Exception e) {
            Intent newIntent = new Intent("downloadFile");
            newIntent.putExtra("state", InitSplashActivity.DownloadState.ERROR);
            newIntent.putExtra("progress", prog);
            sendBroadcast(newIntent);
        }
    }
}
