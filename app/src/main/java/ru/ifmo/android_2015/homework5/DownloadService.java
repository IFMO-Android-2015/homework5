package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class DownloadService extends IntentService implements ProgressCallback {

    public static final String ACTION_DOWNLOAD = "action.Download";
    public static final String ACTION_PROGRESS = "action.Progress";
    public static final String PROGRESS = "Progress";
    public static final String STATUS = "Status";
    public static final String URL = "Url";


    public enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        // ID строкового ресурса для заголовка окна прогресса
        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onProgressChanged(int progress) {
        Intent intent = new Intent(ACTION_PROGRESS);
        intent.putExtra(PROGRESS, progress);
        intent.putExtra(STATUS, DownloadState.DOWNLOADING.titleResId);
        sendLocalBroadcast(intent);
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.getAction().equals(ACTION_DOWNLOAD)) {
            String Url = intent.getStringExtra(URL);
            Intent broadcastIntent = new Intent(ACTION_PROGRESS);
            try {
                File destFile = FileUtils.createTempExternalFile(getApplicationContext(), "gz");
                DownloadUtils.downloadFile(Url, destFile, this);

                broadcastIntent.putExtra(PROGRESS, 100);
                broadcastIntent.putExtra(STATUS, DownloadState.DONE.titleResId);
                sendLocalBroadcast(broadcastIntent);
            } catch (IOException ex) {
                Log.d("", ex.getMessage());
                broadcastIntent.putExtra(PROGRESS, -1);
                broadcastIntent.putExtra(STATUS, DownloadState.ERROR.titleResId);
                sendLocalBroadcast(broadcastIntent);
            }
        }
    }
}
