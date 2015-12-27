package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by dns on 27.12.2015.
 */
public class DownloadService extends IntentService implements ProgressCallback {
    public static final String ACTION_DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
    public static final String ACTION_BROADCAST = "ru.ifmo.android.2015.homework5.action.BROADCAST";
    public static final String URL_PARAMETER = "ru.ifmo.android.2015.homework5.extra.URL";
    public static final String TITLE_PARAMETER = "ru.ifmo.android.2015.homework5.extra.TITLE";
    public static final String PROGRESS_PARAMETER = "ru.ifmo.android.2015.homework5.extra.PROGRESS";

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
    private DownloadState state;
    private int progress;

    public DownloadService() {
        super("DownloadService");
    }

    public static void addDownload(Context context, String URL) {
        Intent intent = new Intent(context, DownloadService.class)
                .setAction(ACTION_DOWNLOAD)
                .putExtra(URL_PARAMETER, URL);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_DOWNLOAD)) {
                final String URL_param = intent.getStringExtra(URL_PARAMETER);
                this.state = DownloadState.DOWNLOADING;
                progress = 0;
                publishProgress();
                try {
                    download_File(URL_param);
                } catch (IOException e) {
                    this.state = DownloadState.ERROR;
                    publishProgress();
                    return;
                }
                this.state = DownloadState.DONE;
                this.progress = 100;
                publishProgress();
            }
        }
    }

    private void download_File(String URL) throws IOException {
        File destination = FileUtils.createTempExternalFile(this, ".gz");
        DownloadUtils.downloadFile(URL, destination, this);
    }

    @Override
    public void onProgressChanged(int progress) {
        this.progress = progress;
        publishProgress();
    }

    private void publishProgress() {
        Intent intent = new Intent(ACTION_BROADCAST)
                .putExtra(TITLE_PARAMETER, this.state.titleResId)
                .putExtra(PROGRESS_PARAMETER, this.progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
