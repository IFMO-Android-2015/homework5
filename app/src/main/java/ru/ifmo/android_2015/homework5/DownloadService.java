package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Cawa on 26.12.2015.
 */
public class DownloadService extends IntentService implements ProgressCallback {
    public static String DOWNLOAD_URL = "downloadUrl";
    public static String BROADCAST_ACTION = "ru.ifmo.android_2015.homework.broadcast_action";
    public static String DOWNLOADING_ACTION = "ru.ifmo.android_2015.homework.downloading_action";
    public static String PROGRESS = "progress";
    public static String DOWNLOAD_STATE = "downloadState";
    private static final String TAG = DownloadService.class.getSimpleName();
    private int progress;
    private InitSplashActivity.DownloadState state;

    public DownloadService() {
        super("DownloadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(DOWNLOADING_ACTION)) {
                progress = 0;
                state = InitSplashActivity.DownloadState.DOWNLOADING;
                sendProgress();
                try {
                    downloadFile(intent.getStringExtra(DOWNLOAD_URL));
                } catch (IOException e) {
                    state = InitSplashActivity.DownloadState.ERROR;
                    sendProgress();
                }
                state = InitSplashActivity.DownloadState.DONE;
                progress = 100;
                sendProgress();
            }
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        Log.d(TAG, "onProgressChanged");
        this.progress = progress;
        sendProgress();
    }

    void sendProgress() {
        Log.d(TAG, "sendProgress");
        Intent intent = new Intent(BROADCAST_ACTION);;
        intent.putExtra(PROGRESS, progress);
        intent.putExtra(DOWNLOAD_STATE, state.titleResId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void downloadFile(String url) throws IOException {
        Log.d(TAG, "downloadFile");
        File destFile = FileUtils.createTempExternalFile(this, "gz");
        DownloadUtils.downloadFile(url, destFile, this);
    }
}