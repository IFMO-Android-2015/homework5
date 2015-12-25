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

    private ProgressBar progressBarView;
    private TextView titleTextView;
    private BroadcastReceiver br;
    private int progress;
    private DownloadState state;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarView.setMax(100);


        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                state = (DownloadState) intent.getSerializableExtra(DownloadService.EXTRA_PARAM_STATE);
                progress = intent.getIntExtra(DownloadService.EXTRA_PARAM_PROGRESS, 0);
                Log.d(TAG, "onReceive: state = " + state + ", progress = " + progress);

                updateView();
            }
        };

        // создаем фильтр для BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(DownloadService.ACTION_BROADCAST);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intentFilter);


        if (savedInstanceState == null) {
            Intent intentDownloadService = new Intent(this, DownloadService.class);
            intentDownloadService.setAction(DownloadService.ACTION_DOWNLOAD);
            intentDownloadService.putExtra(DownloadService.EXTRA_PARAM_URL, CITIES_GZ_URL);
            startService(intentDownloadService);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(br);
    }

    private void updateView() {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        progress = savedInstanceState.getInt("progress");
        state = (DownloadState) savedInstanceState.getSerializable("state");
        updateView();
        Log.d(TAG, "onRestoreInstanceState");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("progress", progress);
        outState.putSerializable("state", state);
        Log.d(TAG, "onSaveInstanceState");
    }


    private static final String TAG = "InitSplash";
}
