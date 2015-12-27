package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class DownloadService extends IntentService {
    public DownloadService() {
        super("name");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        try {
            downloadFile(this, url, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    sendBroadcast(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            sendBroadcast(InitSplashActivity.DownloadState.DONE, 100);
        } catch (IOException e) {
            Log.e("DownloadService", "Failed to download file");
            sendBroadcast(InitSplashActivity.DownloadState.ERROR, 100);
            e.printStackTrace();
        }
    }

    private void sendBroadcast(InitSplashActivity.DownloadState state, int progress) {
        Intent intent = new Intent("MyFilter");
        intent. putExtra("state", state);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }

    static void downloadFile(Context context, String url,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(url, destFile, progressCallback);
    }
}
