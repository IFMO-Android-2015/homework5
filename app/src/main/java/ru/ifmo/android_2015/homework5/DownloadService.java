package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    sendProgressUpdate(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            sendProgressUpdate(InitSplashActivity.DownloadState.DONE, 100);
        } catch (IOException e) {
            sendProgressUpdate(InitSplashActivity.DownloadState.ERROR, 100);
            Log.e(TAG, "Download failed " + e, e);
        }
    }

    private void sendProgressUpdate(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent intent = new Intent("HOMEWORK5");
        intent.putExtra("DOWNLOAD_STATE", downloadState);
        intent.putExtra("PROGRESS", progress);
        sendBroadcast(intent);
    }

    private String TAG = "DownloadService";
}
