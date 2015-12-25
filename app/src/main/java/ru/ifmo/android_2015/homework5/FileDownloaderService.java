package ru.ifmo.android_2015.homework5;


import android.app.IntentService;
import android.content.Intent;

import ru.ifmo.android_2015.homework5.InitSplashActivity.DownloadState;

public class FileDownloaderService extends IntentService {
    public FileDownloaderService() {
        super("FileDownloaderService");
    }
    public static final String TAG = "File Downloader Service";
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    broadcastProgress(DownloadState.DOWNLOADING, progress);
                }
            });
            broadcastProgress(DownloadState.DONE, 100);
        } catch (Exception ex) {
            broadcastProgress(DownloadState.ERROR, 0);
        }
    }

    private void broadcastProgress(DownloadState downloadState, int progress) {
        Intent intent = new Intent("FileDownloaderServiceProgress");
        intent.putExtra(getString(R.string.downloadState), downloadState);
        intent.putExtra(getString(R.string.progress), progress);
        sendBroadcast(intent);
    }
}
