package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DownloadFileService extends IntentService implements ProgressCallback {
    public static final String ACTION_DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
    public static final String EXTRA_URL = "ru.ifmo.android_2015.homework5.extra.URL";

    public static final String EXTRA_PROGRESS = "ru.ifmo.android_2015.homework5.extra.PROGRESS_VALUE";
    public static final String EXTRA_STATE = "ru.ifmo.android_2015.homework5.extra.PROGRESS_STATE";

    public static final String BROADCAST_PROGRESS = "ru.ifmo.android_2015.homework5.action.PROGRESS";

    private static final String TAG = "DownloadFileService";

    private int state = 0; //0 - downloading; 1 - done; 2 - error

    public DownloadFileService() {
        super("DownloadFileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        final String url = intent.getStringExtra(EXTRA_URL);
        sendState(0);
        try {
            File destFile = FileUtils.createTempExternalFile(this, ".gz");
            DownloadUtils.downloadFile(url, destFile, this);
        } catch (IOException e) {
            state = 2;
            sendState(228);
            return;
        }
        state = 1;
        sendState(100);
    }

    private void sendState(int progress) {
        Intent intent = new Intent(BROADCAST_PROGRESS)
                .putExtra(EXTRA_STATE, this.state)
                .putExtra(EXTRA_PROGRESS, progress);
        sendBroadcast(intent);
    }

    @Override
    public void onProgressChanged(int progress) {
        sendState(progress);
    }
}
