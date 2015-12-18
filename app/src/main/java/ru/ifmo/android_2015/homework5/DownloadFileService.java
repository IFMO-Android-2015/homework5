package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Asus on 12.12.2015.
 */
public class DownloadFileService extends IntentService {


    private static final String TAG = "DownloadFileService";

    public DownloadFileService() {
        super("DownloadFileService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.e(TAG, "Downloading using service");
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    updateProgress(DownloadState.DOWNLOADING, progress);
                }
            });
            Log.e(TAG, "Downloading is finish");
            updateProgress(DownloadState.DONE, 100);
        } catch (Exception e) {
            Log.e(TAG, "Failed downloading" + e, e);
            updateProgress(DownloadState.ERROR, 100);
        }
    }

    private void updateProgress(DownloadState downloadState, int progress) {
        Intent intent = new Intent("download_intent");
        intent.putExtra("progress", progress);
        intent.putExtra("state", downloadState);
        Log.e(TAG, "send broadcast" + progress + " " + downloadState);
        sendBroadcast(intent);
    }
}
