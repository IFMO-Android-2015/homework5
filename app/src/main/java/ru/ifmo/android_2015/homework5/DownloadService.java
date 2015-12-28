package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ruslandavletshin on 27/12/15.
 */
public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        try {
            InitSplashActivity.downloadFile(this, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    publishProgress(InitSplashActivity.DownloadState.DOWNLOADING, progress);
                }
            });
            publishProgress(InitSplashActivity.DownloadState.DONE, 100);
        } catch (Exception e) {
            publishProgress(InitSplashActivity.DownloadState.ERROR, 100);
        }
    }

    private void publishProgress(final InitSplashActivity.DownloadState downloadState, final int progress) {
        final Intent intent = new Intent("service");
        intent.putExtra("download_state", downloadState);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }
}
