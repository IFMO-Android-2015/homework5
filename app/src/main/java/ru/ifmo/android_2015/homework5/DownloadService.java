package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by heat_wave on 12/13/15.
 */

public class DownloadService extends IntentService implements ProgressCallback {

    public static final String ACTION_DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
    public static final String BROADCAST_ACTION = "ru.ifmo.android_2015.homework5.action.BROADCAST";
    public static final String PARAM_URL = "ru.ifmo.android_2015.homework5.extra.URL";
    public static final String PARAM_TITLE = "ru.ifmo.android_2015.homework5.extra.TITLE";
    public static final String PARAM_PROGRESS = "ru.ifmo.android_2015.homework5.extra.PROGRESS";

    private DownloadState state;
    private int progress;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_DOWNLOAD)) {
                final String paramURL = intent.getStringExtra(PARAM_URL);
                this.state = DownloadState.DOWNLOADING;
                progress = 0;
                broadcastProgress();
                try {
                    File destFile = FileUtils.createTempExternalFile(this, ".gz");
                    DownloadUtils.downloadFile(paramURL, destFile, this);
                } catch (IOException e) {
                    this.state = DownloadState.ERROR;
                    broadcastProgress();
                    return;
                }
                this.state = DownloadState.DONE;
                this.progress = 100;
                broadcastProgress();
            }
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        this.progress = progress;
        broadcastProgress();
    }

    private void broadcastProgress() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(PARAM_TITLE, this.state.titleResId);
        intent.putExtra(PARAM_PROGRESS, this.progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        // ID строкового ресурса для заголовка окна прогресса
        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }
}