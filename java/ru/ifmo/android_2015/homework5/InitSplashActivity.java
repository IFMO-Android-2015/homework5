package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;

/**
 * Экран, выполняющий инициализацию при первом запуске приложения. В процессе инициализации
 * скачивается файл с данными, нужными для работы приложения. Пока идет инициализация, показывается
 * сплэш-скрин с индикатором прогресса.
 */
public class InitSplashActivity extends Activity {

    // Урл для скачивания файла с данными, нужными для инициализации приложения при первом запуске.
    // GZIP-архив, содержащий список городов в формате JSON.
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;
    private DownloadState downloadState;
    private BroadcastReceiver broadcastReceiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);
        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarView.setMax(100);

        if (savedInstanceState == null) {
            downloadState = DownloadState.DOWNLOADING;
            Intent intent = new Intent(this, DownloadFileService.class);
            startService(intent);
        }
        if (savedInstanceState == null || savedInstanceState.getSerializable("downloadstate") == DownloadState.DOWNLOADING) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    downloadState = (DownloadState) intent.getSerializableExtra("downloadstate");
                    updateView(downloadState, intent.getIntExtra("progress", 0));
                    if (intent.getSerializableExtra("downloadstate") != DownloadState.DOWNLOADING) {
                        unregisterReceiver(broadcastReceiver);
                    }
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter("downloadfileservice"));
        }
    }

    /**
     * Состояние загрузки в DownloadFileTask
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

    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("downloadstate", downloadState);
        outState.putInt("progress", progressBarView.getProgress());
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        downloadState = (DownloadState) savedInstanceState.getSerializable("downloadstate");
        updateView(downloadState, savedInstanceState.getInt("progress"));
    }

    void updateView(DownloadState downloadState, int progress) {
        progressBarView.setProgress(progress);
        titleTextView.setText(downloadState.titleResId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadState == DownloadState.DOWNLOADING) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    /**
     * Скачивает список городов во временный файл.
     */
    static void downloadFile(Context context, ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "cities");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}
