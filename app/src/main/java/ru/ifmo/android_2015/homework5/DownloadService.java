package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

public class DownloadService extends IntentService {


    public DownloadService(String name) {
        super(name);
    }

    public DownloadService() {
        super("DownloadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals("DOWNLOAD")) {
                try {
                    InitSplashActivity.downloadFile(this, new ProgressCallback() {
                        @Override
                        public void onProgressChanged(int progress) {
                            sendProgress(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                        }
                    });
                } catch (Exception e) {
                    sendProgress(InitSplashActivity.DownloadState.ERROR, 100);
                    return;
                }
                sendProgress(InitSplashActivity.DownloadState.DONE, 100);
            }
        }
    }

    private void sendProgress(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent intent = new Intent(String.valueOf(R.string.action));
        intent.putExtra("download_state", downloadState);
        intent.putExtra("progress", progress);

        sendBroadcast(intent);
    }

}