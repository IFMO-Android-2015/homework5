package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;

import static ru.ifmo.android_2015.homework5.DownloadState.*;

public class DownloadService extends IntentService implements ProgressCallback {

    public DownloadService() {
        super("Download service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InitSplashActivity.downloadFile(this, this);
            sendProgress(DONE, 100);
        } catch (IOException e) {
            sendProgress(ERROR, 0);
        }
    }

    private void sendProgress(DownloadState state, int progress) {
        Intent intent = new Intent(HOMEWORK);
        intent.putExtra(DOWNLOAD_STATE, state);
        intent.putExtra(PROGRESS, progress);
        sendBroadcast(intent);
    }

    @Override
    public void onProgressChanged(int progress) {
        sendProgress(DOWNLOADING, progress);
    }
}
