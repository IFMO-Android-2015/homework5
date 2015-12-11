package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;

public class DownloadService extends IntentService implements ProgressCallback {
    public static final String SERVICE_NAME = "DownloadService";

    public static final String BROADCAST_NAME = "download.broadcast";

    public static final String URL_FIELD = "URL";
    public static final String STATUS_FIELD = "status";
    public static final String PROGRESS_FIELD = "progress";
    public static final String ERROR_FIELD = "error";

    public static final int DOWNLOADING = 0;
    public static final int FINISHED = 1;
    public static final int ERROR = 2;

    private String currentURL;

    public DownloadService() {
        super(SERVICE_NAME);
    }

    private void sendLocalBroadcast(Intent message) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(message);
    }

    private Intent makeMessage(String url) {
        Intent message = new Intent(BROADCAST_NAME);
        message.putExtra(URL_FIELD, url);
        return message;
    }

    @Override
    public void onProgressChanged(int progress) {
        Intent message = makeMessage(currentURL);
        message.putExtra(STATUS_FIELD, DOWNLOADING);
        message.putExtra(PROGRESS_FIELD, progress);
        sendLocalBroadcast(message);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        currentURL = intent.getStringExtra(URL_FIELD);
        try {
            File destFile = FileUtils.createTempExternalFile(this, "gz");
            DownloadUtils.downloadFile(currentURL, destFile, this);

            Intent message = makeMessage(currentURL);
            message.putExtra(STATUS_FIELD, FINISHED);
            sendLocalBroadcast(message);
        } catch (IOException e) {
            Intent message = makeMessage(currentURL);
            message.putExtra(STATUS_FIELD, ERROR);
            message.putExtra(ERROR_FIELD, e.getMessage());
            sendLocalBroadcast(message);
        }
    }
}
