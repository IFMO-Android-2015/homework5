package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by Матвей on 23.12.2015.
 */
public class DownloadService extends IntentService implements ProgressCallback {
    private DownloadState downloadState;
    private int progress;

    public static String DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
    public static String BROADCAST = "ru.ifmo.android_2015.homework5.action.BROADCAST";
    public static String URL = "ru.ifmo.android_2015.homework5.extra.URL";
    public static String TITLE = "ru.ifmo.android_2015.homework5.extra.TITLE";
    public static String PROGRESS = "ru.ifmo.android_2015.homework5.extra.PROGRESS";

    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onProgressChanged(int progress) {
        this.progress = progress;
        changeProgress();
    }

    private void changeProgress() {
        Intent intent = new Intent(BROADCAST);
        intent.putExtra(TITLE, downloadState.titleResId);
        intent.putExtra(PROGRESS, this.progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(action.equals(DOWNLOAD)) {
                String url = intent.getStringExtra(URL);
                downloadState = DownloadState.DOWNLOADING;
                InitSplashActivity.Working = true;
                this.onProgressChanged(0);
                try {
                    File dest = File.createTempFile("tmp", ".gz", this.getExternalFilesDir(null));
                    DownloadUtils.downloadFile(url, dest, this);
                } catch (IOException e) {
                    downloadState = DownloadState.ERROR;
                    this.changeProgress();
                    return;
                } finally {
                    InitSplashActivity.Working = false;
                }
                downloadState = DownloadState.DONE;
                this.onProgressChanged(100);
            }
        }
    }
}
