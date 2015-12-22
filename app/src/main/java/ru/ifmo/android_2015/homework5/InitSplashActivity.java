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
    private static final String TAG = "InitSplash";
    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;
    private FileDownloaderBroadcastReceiver receiver = new FileDownloaderBroadcastReceiver();
    private DownloadState downloadState;
    private int progress;

    /**
     * Скачивает список городов во временный файл.
     */
    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private void updateUI() {
        progressBarView.setProgress(progress);
        switch (downloadState) {
            case DONE:
                titleTextView.setText(getString(R.string.done));
                break;
            case DOWNLOADING:
                titleTextView.setText(getString(R.string.downloading));
                break;
            case ERROR:
                titleTextView.setText(getString(R.string.error));
                break;
        }
    }

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
            Intent intent = new Intent(this, FileDownloaderService.class);
            startService(intent);
        } else {
            downloadState = (DownloadState) savedInstanceState.getSerializable(getString(R.string.downloadState));
            progress = savedInstanceState.getInt(getString(R.string.progress));
        }
        if (downloadState == DownloadState.DOWNLOADING) {
            receiver = new FileDownloaderBroadcastReceiver();
            registerReceiver(receiver, new IntentFilter("FileDownloaderServiceProgress"));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putSerializable(getString(R.string.downloadState), downloadState);
        state.putInt(getString(R.string.progress), progress);
        super.onSaveInstanceState(state);
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

    private class FileDownloaderBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            downloadState = (DownloadState) intent.getSerializableExtra(getString(R.string.downloadState));
            progress = intent.getIntExtra(getString(R.string.progress), 0);
            if (downloadState != DownloadState.DOWNLOADING)
                unregisterReceiver(receiver);
            updateUI();
        }
    }
}
