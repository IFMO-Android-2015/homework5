package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.IOException;

public class DownloadFileService extends IntentService {

    private static final String LOG_TAG = "DownloadFileService";

    public static final String URL = "url";
    public static final String PENDING_INTENT = "pending";
    public static final String PROGRESS = "progress";

    public static final int DONE = 0;
    public static final int DOWNLOADING = 1;
    public static final int ERROR = 2;

    public DownloadFileService() {
        super("DownloadFileService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final PendingIntent reply = intent.getParcelableExtra(PENDING_INTENT);
        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    try {
                        if (progress == 100) {
                            reply.send(getApplicationContext(), DownloadFileService.DONE, new Intent().putExtra(PROGRESS, progress));
                        } else {
                            reply.send(getApplicationContext(), DownloadFileService.DOWNLOADING, new Intent().putExtra(PROGRESS, progress));
                        }
                    } catch (PendingIntent.CanceledException e) {
                        Log.e(LOG_TAG, "Reply canceled!");
                    }
                }
            });
        } catch (Exception e) {
            try {
                reply.send(getApplicationContext(), DownloadFileService.ERROR, new Intent());
            } catch (PendingIntent.CanceledException e1) {
                Log.e(LOG_TAG, "Reply canceled!");
            }
        }
    }
}
