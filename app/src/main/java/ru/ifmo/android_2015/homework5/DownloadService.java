package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import java.io.IOException;

/**
 * Created by ilnarkadyrov on 12/24/15.
 */
public class DownloadService extends IntentService{

    public static final String PENDING = "Pending";
    public static final String DOWNLOADING_PROGRESS = "Download";
    public static final String DOWNLOAD_SERVICE = "Download_Service";

    public static final int Done = 0;
    public static final int Error = -1;
    public static final int Downloading = 1;
    public static final int PROGRESS = 100;

    public DownloadService(){
        super(DOWNLOAD_SERVICE);
    }

    public DownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final PendingIntent pendingIntent = intent.getParcelableExtra(PENDING);
        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    try {
                        pendingIntent.send(getApplicationContext(),
                                (progress == PROGRESS) ?
                                        DownloadService.Done :
                                        DownloadService.Downloading,
                                new Intent().putExtra(DOWNLOADING_PROGRESS, progress));
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            try {
                pendingIntent.send(getApplicationContext(), DownloadService.Error, new Intent());
            } catch (PendingIntent.CanceledException e1) {
                e1.printStackTrace();
            }
        }
    }

}
