package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadService extends IntentService {
    public static final String ACTION_DOWNLOAD_PROGRESS_UPDATED = "download_progress_updated";
    public static final String EXTRA_DOWNLOAD_STATE = "state";
    public static final String EXTRA_PROGRESS = "progress";
    // To implement ordering
    public static final String EXTRA_INTENT_INDEX = "index";

    // Урл для скачивания файла с данными, нужными для инициализации приложения при первом запуске.
    // GZIP-архив, содержащий список городов в формате JSON.
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";


    private long lastIntentIndex = 0;

    public DownloadService() {
        super("DownloadServiceWorker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String url = CITIES_GZ_URL;
        try {
            final File file = FileUtils.createTempExternalFile(this, "gz");
            DownloadUtils.downloadFile(url, file, new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    sendProgressUpdate(DownloadState.DOWNLOADING, progress);
                }
            });
            sendProgressUpdate(DownloadState.DONE, 100);
        } catch (IOException e) {
            Log.w(TAG, e);
            sendProgressUpdate(DownloadState.ERROR, 100);
        }
    }

    private void sendProgressUpdate(DownloadState downloadState, int progress) {
        Intent intent = new Intent(ACTION_DOWNLOAD_PROGRESS_UPDATED);
        intent.putExtra(EXTRA_PROGRESS, progress);
        intent.putExtra(EXTRA_DOWNLOAD_STATE, downloadState);
        intent.putExtra(EXTRA_INTENT_INDEX, ++lastIntentIndex);
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

    private static String TAG = "DownloadService";
}
