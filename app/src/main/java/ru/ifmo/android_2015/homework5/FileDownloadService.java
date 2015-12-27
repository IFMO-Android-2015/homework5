package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;


public class FileDownloadService extends IntentService {

    private static final String LOG_TAG = "FileDownloadService";
    public static final String PENDING_INTENT = "PendingIntent";
    public static final String DOWNLOAD_PROGRESS = "DownloadProgress";

    public static final int Done = 0;
    public static final int Error = 1;
    public static final int InProgress = 2;

    public FileDownloadService() {
        super("FileDownloadService");
    }

    public FileDownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final PendingIntent pending = intent.getParcelableExtra(PENDING_INTENT);

        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    try {
                        pending.send(
                                getApplicationContext(),
                                (progress == 100)?FileDownloadService.Done:FileDownloadService.InProgress,
                                new Intent().putExtra(DOWNLOAD_PROGRESS,
                                        progress
                                )
                        );
                    } catch (PendingIntent.CanceledException e) {
                        Log.e(LOG_TAG, "Cant reply!");
                    }
                }
            });
        } catch (IOException e) {
            try {
                pending.send(getApplicationContext(), FileDownloadService.Error, new Intent());
            } catch (PendingIntent.CanceledException e1) {
                Log.e(LOG_TAG, "Cant reply!");
            }
        }
    }
}