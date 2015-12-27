package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

enum State {
    DOWNLOADING(R.string.downloading),
    ERROR(R.string.error),
    DONE(R.string.done);

    final int titleResId;
    State(int id) {
        this.titleResId = id;
    }
}

public class DownloadService extends IntentService implements ProgressCallback {
    public static final String ACTION_DOWNLOAD = "DOWNLOAD";
    public static final String ACTION_BROADCAST = "BROADCAST";
    public static final String EXTRA_PARAM_URL = "PARAM_URL";
    public static final String EXTRA_PARAM_STATE = "PARAM_STATE";
    public static final String EXTRA_PARAM_PROGRESS = "PARAM_PROGRESS";

    private State state;
    private int progress;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_PARAM_URL);
                state = State.DOWNLOADING;
                progress = 0;
                broadcastProgress();
                try {
                    File destFile = FileUtils.createTempExternalFile(this, "gz");
                    DownloadUtils.downloadFile(url, destFile, this);
                } catch (Exception e) {
                    state = State.ERROR;
                    broadcastProgress();
                    return;
                }
                state = State.DONE;
                progress = 100;
                broadcastProgress();
            }
        }
    }

    @Override
    public void onProgressChanged(int changedProgress) {
        progress = changedProgress;
        broadcastProgress();
    }

    private void broadcastProgress() {
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_PARAM_STATE, this.state);
        intent.putExtra(EXTRA_PARAM_PROGRESS, this.progress);
        sendBroadcast(intent);
    }
}
