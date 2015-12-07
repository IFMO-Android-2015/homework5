package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DownloadFileService extends IntentService {

    public DownloadFileService() {
        super("DownloadFileService");

    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {



                @Override
                public void onProgressChanged(int progress) {
                    sendProgressUpdate(InitSplashActivity.DownloadState.DOWNLOADING,progress);
                }


            });
            sendProgressUpdate(InitSplashActivity.DownloadState.DONE,100);
        } catch (Exception e) {
            sendProgressUpdate(InitSplashActivity.DownloadState.ERROR,100);
        }
    }

    private void sendProgressUpdate(InitSplashActivity.DownloadState downloadState, int progress) {
        Intent intent = new Intent(InitSplashActivity.BROADCAST_ACTION);
        intent.putExtra(InitSplashActivity.DOWNLOAD_STATE, downloadState);
        intent.putExtra(InitSplashActivity.PROGRESS, progress);
        sendBroadcast(intent);
    }

}
