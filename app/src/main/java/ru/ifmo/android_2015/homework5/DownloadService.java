package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.IOException;

public class DownloadService extends IntentService implements ProgressCallback {
    private static final String TAG = "DownloadService";
    public static final String ACTION = "ru.ifmo.android_2015.homework5.action.DOWNLOAD_SERVICE";

    public static final String EXTRA_DOWNLOAD_STATE = "ru.ifmo.android_2015.homework5.extra.DOWNLOAD_STATE";
    public static final String EXTRA_PROGRESS = "ru.ifmo.android_2015.homework5.extra.PROGRESS";

    private InitSplashActivity.DownloadState state = InitSplashActivity.DownloadState.DOWNLOADING;
    private int progress;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.d(TAG, "Downloading...");
            InitSplashActivity.downloadFile(getApplicationContext(), this);
            state = InitSplashActivity.DownloadState.DONE;
            progress = 100;
        } catch (IOException e) {
            Log.e(TAG, "Error downloading file: " + e, e);
            state = InitSplashActivity.DownloadState.ERROR;
        }
        sendProgress();
        stopSelf();
    }

    @Override
    public void onProgressChanged(int progress) {
        this.progress = progress;
        sendProgress();
    }

    private void sendProgress() {
        Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_DOWNLOAD_STATE, state);
        intent.putExtra(EXTRA_PROGRESS, progress);
        sendBroadcast(intent);
    }
}
