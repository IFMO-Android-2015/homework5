package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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

    public final static String BROADCAST_ACTION = "ru.ifmo.android_2015.homework5";

    public final static String DOWNLOAD_STATE = "download_state";
    public final static String PROGRESS = "progress";

    // Индикатор прогресса
    protected ProgressBar progressBarView;
    // Заголовок
    protected TextView titleTextView;

    private boolean isFinished;
    private BroadcastReceiver br;



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
            Intent intent = new Intent(this, DownloadFileService.class);
            startService(intent);
        }

        if (!isFinished) {
            br = new BroadcastReceiver();
            registerReceiver(br, br.intentFilter);
        }




    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        // Этот метод вызывается при смене конфигурации, когда текущий объект
        // Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
        // и его можно будет использовать в новом объекте Activity
        return br;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(br);
        } catch (Exception e) { }

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
     * Таск, выполняющий скачивание файла в фоновом потоке.
     */

    private class BroadcastReceiver extends android.content.BroadcastReceiver {





        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadState downloadState = (DownloadState) intent.getSerializableExtra(DOWNLOAD_STATE);
            int progress = intent.getIntExtra(PROGRESS, 0);
            if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                unregisterReceiver(br);
                isFinished = true;
            }
            updateView(downloadState, progress);
        }

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

        void updateView(DownloadState state, int progress) {


                titleTextView.setText(state.titleResId);
                progressBarView.setProgress(progress);

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
