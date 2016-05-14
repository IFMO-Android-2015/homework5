package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;


public class DownloadService extends IntentService {

    Intent intent;
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try{
                InitSplashActivity.downloadFile(this, new ProgressCallback() {
                    @Override
                    public void onProgressChanged(int progress) {
                        sendProgressChanges(progress, InitSplashActivity.DownloadState.DOWNLOADING);
                    }
                });
                sendProgressChanges(100, InitSplashActivity.DownloadState.DONE);
            }
            catch (Exception e){
                sendProgressChanges(0, InitSplashActivity.DownloadState.ERROR);
            }

        }
    }

    protected void sendProgressChanges(int state, InitSplashActivity.DownloadState ds){
        Intent intent = new Intent("Download service");
        intent.putExtra("state", state);
        intent.putExtra("ds", ds);
        sendBroadcast(intent);
    }
}
