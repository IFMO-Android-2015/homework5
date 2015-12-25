package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.io.File;


public class DownloadService extends IntentService implements ProgressCallback {
    public static final String ACTION_DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
    public static final String ACTION_BROADCAST = "ru.ifmo.android_2015.homework5.action.BROADCAST";
    public static final String EXTRA_PARAM_URL = "ru.ifmo.android_2015.homework5.extra.PARAM_URL";
    public static final String EXTRA_PARAM_STATE = "ru.ifmo.android_2015.homework5.extra.PARAM_STATE";
    public static final String EXTRA_PARAM_PROGRESS = "ru.ifmo.android_2015.homework5.extra.PARAM_PROGRESS";

    private DownloadState state;
    private int progress;


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_PARAM_URL);
                state = DownloadState.DOWNLOADING;
                progress = 0;
                broadcastProgress();
                try {
                    File destFile = FileUtils.createTempExternalFile(this, "gz");
                    DownloadUtils.downloadFile(url, destFile, this);
                } catch (Exception e) {
                    Log.e(TAG, "Error downloading file: " + e, e);
                    state = DownloadState.ERROR;
                    broadcastProgress();
                    return;
                }
                state = DownloadState.DONE;
                progress = 100;
                broadcastProgress();
            }
        }
    }

    @Override
    public void onProgressChanged(int newProgress) {
        progress = newProgress;
        broadcastProgress();
    }

    private void broadcastProgress() {
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_PARAM_STATE, this.state);
        intent.putExtra(EXTRA_PARAM_PROGRESS, this.progress);
       // LocalBroadcastManager.getInstance(this).
        sendBroadcast(intent);
    }


    public DownloadService() {
        super("DownloadService");
    }


    private static final String TAG = "DownloadService";
}
