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
    private BroadcastReceiver broad;

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

            state = DownloadState.DOWNLOADING;
            startService(new Intent(this, DownloadService.class));

        }

        if ((state != DownloadState.ERROR) ||  (state != DownloadState.DONE)) {
            broad = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    state = (DownloadState) intent.getSerializableExtra("state");

                    if ((state == DownloadState.DONE) || (state == DownloadState.ERROR)) {
                        unregisterReceiver(broad);
                    }

                    updateView(state, intent.getIntExtra("progress", 0));
                }
            };
            registerReceiver(broad, new IntentFilter("downloadFile"));
        }
    }


        protected void onSaveInstanceState(Bundle fileState) {
            fileState.putSerializable("fileState", state);
            fileState.putInt("progress", progressBarView.getProgress());
            super.onSaveInstanceState(fileState);
        }


        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            state = (DownloadState) savedInstanceState.getSerializable("fileState");
            updateView(state, savedInstanceState.getInt("progress"));
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broad);
        }catch (Exception e)
        {

        }
    }




    void updateView(DownloadState filestate, int progress) {
        titleTextView.setText(filestate.titleResId);
        progressBarView.setProgress(progress);
    }



    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return broad;
        // Этот метод вызывается при смене конфигурации, когда текущий объект
        // Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
        // и его можно будет использовать в новом объекте Activity

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
