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

    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    private ProgressBar progressBarView;
    private TextView titleTextView;
    private MyReceiver broadcastReceiver;
    private boolean downloadReady;

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

        if (!downloadReady) {
            broadcastReceiver = new MyReceiver();
            registerReceiver(broadcastReceiver, broadcastReceiver.intentFilter);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return broadcastReceiver;
    }

    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";

    private class MyReceiver extends android.content.BroadcastReceiver {

        private final IntentFilter intentFilter = new IntentFilter("DOWNLOAD");

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadState downloadState = (DownloadState) intent.getSerializableExtra("DOWNLOAD_STATE");
            int prog = intent.getIntExtra("DOWNLOAD_PROGRESS", 0);
            if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                unregisterReceiver(broadcastReceiver);
                downloadReady = true;
            }
            updateProgress(downloadState, prog);
        }

        void updateProgress(DownloadState state, int progress) {
            titleTextView.setText(state.titleResId);
            progressBarView.setProgress(progress);
        }
    }
}
