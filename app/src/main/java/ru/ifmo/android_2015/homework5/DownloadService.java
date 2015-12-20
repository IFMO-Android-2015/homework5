package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DownloadService extends IntentService implements ProgressCallback {
    private static final String TAG = DownloadService.class.getSimpleName();
    public static final String FILE_TAG = "FILE_URL";
    public static final String PROGRESS_TAG = "PROGRESS";
    public static final String ACTION = "ACTION.DOWNLOAD_FILE";

    public DownloadService() {
        super(TAG);
        Log.d(TAG, "DownloadService created");
    }

    @Override
    public void onProgressChanged(int progress) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(PROGRESS_TAG, progress);
        sendBroadcast(intent);
    }

    public void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent()");
        try {
            File destFile = FileUtils.createTempExternalFile(getApplicationContext(), "gz");
            DownloadUtils.downloadFile(intent.getStringExtra(FILE_TAG), destFile, this);
            sendBroadcast(new Intent(ACTION).putExtra(PROGRESS_TAG, 100));
            System.out.println("Try block exit");
        } catch (IOException e) {
            Log.e(TAG, "onHandleIntent() error");
            sendBroadcast(new Intent(ACTION).putExtra(PROGRESS_TAG, -1));
            e.printStackTrace();
        }
    }
}