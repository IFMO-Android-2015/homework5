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
    private DownloadState state = DownloadState.DOWNLOADING;
    DownloadReceiver receiver;
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        if (savedInstanceState != null) {
            // Пытаемся получить ранее запущенный сервис
        } else {
            Intent download = new Intent(this, DownloadFileService.class);
            state = DownloadState.DOWNLOADING;
            startService(download);
        }

        if (state == DownloadState.DOWNLOADING) {
            receiver = new DownloadReceiver();
            registerReceiver(receiver, new IntentFilter("download_intent"));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = (DownloadState) savedInstanceState.getSerializable("state");
        int progress = savedInstanceState.getInt("progress");
        updateView(state, progress);
    }

    void updateView(DownloadState state, int progress) {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("state", state);
        outState.putInt("progress", progressBarView.getProgress());
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return receiver;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }

    }

    public class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            state = (DownloadState) intent.getSerializableExtra("state");
            if (state != DownloadState.DOWNLOADING) {
                unregisterReceiver(receiver);
            }
            updateView(state, progress);
        }
    }




    /**`
     * Скачивает список городов во временный файл.
     */
    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}
