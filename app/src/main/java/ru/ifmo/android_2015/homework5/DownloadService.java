package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;

public class DownloadService extends IntentService implements ProgressCallback {

    int state;
    Intent intent = new Intent(InitSplashActivity.BROADCAST_ACTION);

    public DownloadService() {
        super("DownloadService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            state = InitSplashActivity.DOWNLOADING_STATE;
            File destFile = FileUtils.createTempExternalFile(this, "gz");
            DownloadUtils.downloadFile(InitSplashActivity.CITIES_GZ_URL, destFile, this);
        } catch (Exception e) {
            state = InitSplashActivity.ERROR_STATE;
            intent.putExtra(InitSplashActivity.STATE, state);
            sendBroadcast(this.intent);
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progress == InitSplashActivity.MAX_PROGRESS) {
            state = InitSplashActivity.FINISHED_STATE;
        }
        intent.putExtra(InitSplashActivity.STATE, state);
        intent.putExtra(InitSplashActivity.PROGRESS, progress);
        sendBroadcast(intent);
    }
}