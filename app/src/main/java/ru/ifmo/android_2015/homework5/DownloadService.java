package ru.ifmo.android_2015.homework5;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadService extends IntentService {

    private static final String TAG = "DownloadService";

    public DownloadService () {
        super(TAG);
    }

    public DownloadService (String name) {
        super(name);
    }

    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Download");
        try {

            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    changeProgress(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            changeProgress(InitSplashActivity.DownloadState.DONE, 100);
        } catch (Exception e) {
            changeProgress(InitSplashActivity.DownloadState.ERROR, 100);
        }
    }

    void changeProgress(InitSplashActivity.DownloadState downloadState, int progress) {
        Log.d(TAG, "Change");
        Intent intent = new Intent(InitSplashActivity.downloadFilter);
        intent.putExtra("currState", downloadState);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }
}
