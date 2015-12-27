package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by anton on 28/12/15.
 */
public class Downloader extends IntentService implements ProgressCallback {

    public static final String ACTION_DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
    public static final String BROADCAST_ACTION = "ru.ifmo.android_2015.homework5.action.BROADCAST";
    public static final String PARAM_URL = "ru.ifmo.android_2015.homework5.extra.URL";
    public static final String PARAM_TITLE = "ru.ifmo.android_2015.homework5.extra.TITLE";
    public static final String PARAM_PROGRESS = "ru.ifmo.android_2015.homework5.extra.PROGRESS";

    private int progress;
    private DownloadState downloadState;

    public Downloader() {
        super("Downloader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if(action.equals(ACTION_DOWNLOAD)) {
            final String paramURL = intent.getStringExtra(PARAM_URL);
            this.downloadState = DownloadState.DOWNLOADING;
            progress = 0;
            broadcastProgress();
            try {
                File destination = FileUtils.createTempExternalFile(this, ".gz");
                DownloadUtils.downloadFile(paramURL, destination, this);
            } catch (IOException e) {
                this.downloadState = DownloadState.FAILED;
                broadcastProgress();
                return;
            }
            this.downloadState = DownloadState.COMPLETED;
            this.progress = 100;
            broadcastProgress();
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        this.progress = progress;
        broadcastProgress();
    }

    private void broadcastProgress() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(PARAM_TITLE, this.downloadState.titleResID);
        intent.putExtra(PARAM_PROGRESS, this.progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        COMPLETED(R.string.done),
        FAILED(R.string.error);

        final int titleResID;

        DownloadState(int titleResID) {
            this.titleResID = titleResID;
        }

    }
}
