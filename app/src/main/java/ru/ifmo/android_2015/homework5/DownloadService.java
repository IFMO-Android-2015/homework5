package ru.ifmo.android_2015.homework5;


import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;


public class DownloadService extends IntentService {

    private static final String LOG_TAG = "DownloadServiceDebug";
    public static final String P_INTENT = "PIntent";
    public static final String DOWNLOAD_PROGRESS = "DownloadProgress";

    public static final int Error = 0;
    public static final int Progress = 1;

    public DownloadService() {
        super("DownloadService");
    }

    public DownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final PendingIntent pi = intent.getParcelableExtra(P_INTENT);

        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    try {
                        pi.send(getApplicationContext(), DownloadService.Progress, new Intent().putExtra(DOWNLOAD_PROGRESS, progress));
                    } catch (PendingIntent.CanceledException e) {
                        Log.e(LOG_TAG, "Can't send progress");
                    }
                }
            });
        } catch (IOException e) {
            try {
                pi.send(DownloadService.Error);
            } catch (PendingIntent.CanceledException e1) {
                Log.e(LOG_TAG, "Can't send error");
            }
        }
    }
}