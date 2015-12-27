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
    // Заголовок
    private TextView titleTextView;
    // Выполняющийся таск загрузки файла
    private DownloadService downloadService;
    public BroadcastReceiver broadcastReceiver;
    public DownloadState downloadState;
    public static String downloadFilter = "downloadFilter";
    public int progress;

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
            Log.d(TAG, "Start new");
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
            progress = 0;
        } else {
            Log.d(TAG, "SavedInstances != null");
            progress = savedInstanceState.getInt("progress");
            downloadState = (DownloadState) savedInstanceState.getSerializable("currState");
            updateView(downloadState, progress);
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "broadcastreceiver");
                downloadState = (DownloadState) intent.getSerializableExtra("currState");
                progress = intent.getIntExtra("progress", 0);
                updateView(downloadState, progress);
                if (downloadState == DownloadState.ERROR || downloadState == DownloadState.DONE)
                    unregisterReceiver(broadcastReceiver);
            }
        };

        IntentFilter intentFilter = new IntentFilter(downloadFilter);
        registerReceiver(broadcastReceiver, intentFilter);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currState", downloadState);
        outState.putInt("progress", progress);
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

    void updateView(DownloadState state, int progress) {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
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