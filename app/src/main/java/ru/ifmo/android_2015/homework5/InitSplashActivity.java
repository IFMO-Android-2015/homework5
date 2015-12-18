package ru.ifmo.android_2015.homework5;

import android.app.Activity;
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

    public final static String BROADCAST = "ru.ifmo.android_2015.homework5";
    public final static String DOWNLOAD = "download_state";
    public final static String PROGRESS = "progress";

    private DownloadState download;
    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;
    private BroadcastReceiver broadcastReceiver;
    private boolean flag;

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

        if (!flag) {
            broadcastReceiver = new BroadcastReceiver();
            registerReceiver(broadcastReceiver, broadcastReceiver.intentFilter);
        }

    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return broadcastReceiver;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Failed to destroy");
        }

    }

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

    private class BroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadState downloadState = (DownloadState) intent.getSerializableExtra(DOWNLOAD);
            int progress = intent.getIntExtra(PROGRESS, 0);
            if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                unregisterReceiver(broadcastReceiver);
                flag = true;
            }
            updateView(downloadState, progress);
        }

        IntentFilter intentFilter = new IntentFilter(BROADCAST);

        void updateView(DownloadState state, int progress) {
            titleTextView.setText(state.titleResId);
            progressBarView.setProgress(progress);

        }
        
    }

    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }
    private static final String TAG = "InitSplash";
}
