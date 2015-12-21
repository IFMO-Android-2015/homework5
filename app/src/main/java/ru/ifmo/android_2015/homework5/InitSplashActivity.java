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
    private DownloadState state;
    // Заголовок
    private TextView titleTextView;
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
            Log.d(TAG, "startService");
            state = DownloadState.DOWNLOADING;
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }

        if (state != DownloadState.ERROR ||state != DownloadState.DONE) {
            br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    int progress = intent.getIntExtra("progress", 0);
                    state = (DownloadState) intent.getSerializableExtra("state");

                    if (state == DownloadState.ERROR || state == DownloadState.DONE) {
                        unregisterReceiver(br);
                    }

                    update(progress, state);
                }
            };
            registerReceiver(br, new IntentFilter("service"));
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = (DownloadState) savedInstanceState.getSerializable("state");
        update(savedInstanceState.getInt("progress"), state);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("state", state);
        outState.putInt("progress", progressBarView.getProgress());
        super.onSaveInstanceState(outState);
    }

    void update(int progress, DownloadState state) {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }



    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return br;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            Log.e(TAG, "Don't destroy");
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