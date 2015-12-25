package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DownloadService extends IntentService implements ProgressCallback {

    public static final String FILE_TAG = "FILE_URL";
    public static final String STATUS_TAG = "STATUS";
    public static final String PROGRESS_TAG = "PROGRESS";
    public static final String ACTION = "ACTION.DOWNLOADFILE";

    public static final int DOWNLOADING = 0;
    public static final int DONE = 1;
    public static final int ERROR = -1;

    @Override
    public void onProgressChanged(int progress) {
        Intent intent = new Intent(ACTION);

        intent.putExtra(PROGRESS_TAG, progress);
        intent.putExtra(STATUS_TAG, DOWNLOADING);

        sendBroadcast(intent);
    }

    public DownloadService() {
        super(TAG);
        Log.d(TAG, "onCreate");
    }

    public void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        String fileUrl = intent.getStringExtra(FILE_TAG);

        try {
            File destFile = FileUtils.createTempExternalFile(getApplicationContext(), "gz");
            DownloadUtils.downloadFile(fileUrl, destFile, this);

            sendBroadcast(new Intent(ACTION).putExtra(PROGRESS_TAG, 100).putExtra(STATUS_TAG, DONE));
        } catch (IOException e) {
            Log.e(TAG, "Downloading error occurred");

            sendBroadcast(new Intent(ACTION).putExtra(PROGRESS_TAG, -1).putExtra(STATUS_TAG, ERROR));

            e.printStackTrace();
        }
    }

    private static final String TAG = DownloadService.class.getSimpleName();
}
