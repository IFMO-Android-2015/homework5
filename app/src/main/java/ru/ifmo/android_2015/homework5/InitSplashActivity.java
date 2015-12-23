package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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
    static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;


    private DownloadStateReceiver receiver = null;
    static final int maxProgress = 100;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(maxProgress);

        if (savedInstanceState == null) {
            Intent intent = new Intent(this, DownloadFileService.class);
            Log.d(TAG, "starting service");
            startService(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (receiver == null) {
            receiver = new DownloadStateReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int prog = intent.getIntExtra(DownloadStateReceiver.percentage, 0);
                    DownloadState state = DownloadState.valueOf(intent.getStringExtra(DownloadStateReceiver.state));
                    if (progressBarView.getProgress() != prog) {
                        Log.d(TAG, "progress updated: " + prog + "%");
                    }
                    titleTextView.setText(state.titleResId);
                    progressBarView.setProgress(prog);
                }
            };
            Log.d(TAG, "receiver created");
        }

        IntentFilter filter = new IntentFilter(DownloadStateReceiver.ACTION);
        Log.d(TAG, "registering receiver");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver != null) {
            Log.d(TAG, "unregistering receiver");
            unregisterReceiver(receiver);
            receiver = null;
        }
    }


    private static final String TAG = "InitSplash";
}
