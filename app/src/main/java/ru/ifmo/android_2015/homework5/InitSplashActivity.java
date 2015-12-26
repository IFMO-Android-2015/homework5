package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.PendingIntent;
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

    public static final String PROGRESS = "progress";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;
    private BroadcastReceiver receiver = null;

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
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "onReceive");
                    if (intent.hasExtra(DownloadState.ERROR.name())) {
                        titleTextView.setText(DownloadState.ERROR.titleResId);
                        return;
                    }
                    int value = intent.getIntExtra(DownloadState.DOWNLOADING.name(), 0);
                    titleTextView.setText((value == 100 ? DownloadState.DONE : DownloadState.DOWNLOADING).titleResId);
                    progressBarView.setProgress(value);
                }
            };

            IntentFilter intentFilter = new IntentFilter(InitSplashActivity.PROGRESS);
            registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
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
