package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class MyService extends IntentService {

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.d("MyService", "Downloading by Service");

            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    Intent progressIntent = new Intent("service");
                    progressIntent.putExtra("state", InitSplashActivity.DownloadState.DOWNLOADING);
                    progressIntent.putExtra("progress", progress);
                    Log.d("Service", "Download state: DOWNLOADING; progress: " + progress);
                    sendBroadcast(progressIntent);
                }
            });
            Intent progressIntent = new Intent("service");
            progressIntent.putExtra("state", InitSplashActivity.DownloadState.DONE);
            progressIntent.putExtra("progress", 100);
            Log.d("MyService", "Download state: DONE");
            sendBroadcast(progressIntent);
        } catch (Exception e) {
            Intent progressIntent = new Intent("service");
            progressIntent.putExtra("state", InitSplashActivity.DownloadState.ERROR);
            progressIntent.putExtra("progress", 100);
            Log.e("MyService", "Download state: ERROR");
            sendBroadcast(progressIntent);
        }
    }
}