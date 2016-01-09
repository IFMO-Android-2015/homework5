package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    public DownloadState downloadState;
    int progress;
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
            progress = 0;
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra("url", CITIES_GZ_URL);
            startService(intent);
        } else {
            downloadState = (DownloadState) savedInstanceState.getSerializable("state");
            progress = savedInstanceState.getInt("progress");
            updateView(downloadState, progress);
        }

        if (downloadState == DownloadState.DOWNLOADING) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    downloadState = (InitSplashActivity.DownloadState) intent.getSerializableExtra("state");
                    progress = intent.getIntExtra("progress", 0);
                    if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                        unregisterReceiver(this);
                    }
                    updateView(downloadState, progress);
                }

            };
            registerReceiver(broadcastReceiver, new IntentFilter("MyFilter"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("state", downloadState);
        outState.putInt("progress", progress);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        updateView(downloadState, progress);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void updateView(DownloadState state, int progress) {
        titleTextView.setText(state.titleResId);
        if (downloadState != DownloadState.DOWNLOADING) {
            progressBarView.setVisibility(View.INVISIBLE);
        } else {
            progressBarView.setProgress(progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
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

    private static final String TAG = "InitSplash";
}