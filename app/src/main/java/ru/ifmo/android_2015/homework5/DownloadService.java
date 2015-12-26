package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

/**
 * Created by qurbonzoda on 26.12.15.
 */
public class DownloadService extends IntentService {
    public DownloadService() {
        super(TAG);
        Log.d(TAG, "constructor");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.d(TAG, "onHandleIntent");
        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    Log.d(TAG, "onProgressChanged");
                    Intent intent1 = new Intent(InitSplashActivity.PROGRESS).putExtra(InitSplashActivity.DownloadState.DOWNLOADING.name(), progress);
                    getApplicationContext().sendBroadcast(intent1);
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "ERROR");
            e.printStackTrace();
            Intent intent1 = new Intent(InitSplashActivity.PROGRESS).putExtra(InitSplashActivity.DownloadState.ERROR.name(), 0);
            getApplicationContext().sendBroadcast(intent1);
        }
    }

    public static final String TAG = "DownloadService";
}
