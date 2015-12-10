package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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
    private DownloadState downloadState;
    // Заголовок
    private TextView titleTextView;
    // Выполняющийся таск загрузки файла
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
            Log.d(TAG, "Start service");
            downloadState = DownloadState.DOWNLOADING;
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        }

        if (downloadState != DownloadState.DONE || downloadState != DownloadState.ERROR) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    downloadState = (DownloadState) intent.getSerializableExtra("downloadState");
                    int progress = intent.getIntExtra("progress", 0);
                    if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                        unregisterReceiver(broadcastReceiver);
                    }
                    updateView(downloadState, progress);
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter("Homework5"));
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        downloadState = (DownloadState) savedInstanceState.getSerializable("downloadState");
        updateView(downloadState, savedInstanceState.getInt("progress"));
    }

    void updateView(DownloadState state, int progress) {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("downloadState", downloadState);
        outState.putInt("progress", progressBarView.getProgress());
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        // Этот метод вызывается при смене конфигурации, когда текущий объект
        // Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
        // и его можно будет использовать в новом объекте Activity
        return broadcastReceiver;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Failed to destroy");
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

    /**
     * Скачивает список городов во временный файл.
     */
    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}
