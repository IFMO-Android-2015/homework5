package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for loading files in
 * a service on a separate handler thread.
 */
public class DownloadFileService extends IntentService implements ProgressCallback {
    private static final String ACTION_DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
    private static final String EXTRA_PARAM_URL = "ru.ifmo.android_2015.homework5.extra.URL";

    public static final String PARAM_TITLE = "ru.ifmo.android_2015.homework5.extra.TITLE_RES_ID";
    public static final String PARAM_PROGRESS = "ru.ifmo.android_2015.homework5.extra.PROGRESS_VALUE";

    public static final String BROADCAST_ACTION = "ru.ifmo.android_2015.homework5.action.REPORT_PROGRESS";

    private DownloadState state;
    private int progress;

    public DownloadFileService() {
        super("DownloadFileService");
    }

    /**
     * Starts this service to perform file download with the given url. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void queueDownload(Context context, String url) {
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_PARAM_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM_URL);
                this.state = DownloadState.DOWNLOADING;
                progress = 0;
                sendProgress();
                try {
                    downloadFile(param1);
                } catch (IOException e) {
                    this.state = DownloadState.ERROR;
                    sendProgress();
                    return;
                }
                this.state = DownloadState.DONE;
                this.progress = 100;
                sendProgress();
            }
        }
    }

    private void downloadFile(String url) throws IOException {
        File destFile = FileUtils.createTempExternalFile(this, ".gz");
        DownloadUtils.downloadFile(url, destFile, this);
    }

    @Override
    public void onProgressChanged(int progress) {
        this.progress = progress;
        sendProgress();
    }

    /**
     * Отправляет прогресс загрузки через LocalBroadcast
     */
    private void sendProgress() {
        Intent intent = new Intent(BROADCAST_ACTION)
                .putExtra(PARAM_TITLE, this.state.titleResId)
                .putExtra(PARAM_PROGRESS, this.progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Состояние загрузки
     */
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
