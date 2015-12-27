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

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;
    BroadcastReceiver receiver;

    int progress = 0;
    int state = 0;

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
            Log.d(TAG, "instance state == null");
            Intent intent = new Intent(this, DownloadFileService.class);
            intent.setAction(DownloadFileService.ACTION_DOWNLOAD);
            intent.putExtra(DownloadFileService.EXTRA_URL, CITIES_GZ_URL);
            startService(intent);
            Log.d(TAG,"download started");
        } else {
            Log.d(TAG, "instance state != null");
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                state = intent.getIntExtra(DownloadFileService.EXTRA_STATE, 0);
                progress = intent.getIntExtra(DownloadFileService.EXTRA_PROGRESS, 0);
                updateView(state, progress);
            }
        };


        IntentFilter filter = new IntentFilter(DownloadFileService.BROADCAST_PROGRESS);
        registerReceiver(receiver, filter);
    }

    private void updateView(int state, int progress) {
        if (state == 1) {
            titleTextView.setText(R.string.done);
        } else if (state == 2) {
            Log.d(TAG, "error displayed");
            titleTextView.setText(R.string.error);
        } else {
            titleTextView.setText(R.string.downloading);
            progressBarView.setProgress(progress);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState.getInt(STATE);
            progress = savedInstanceState.getInt(PROGRESS);
            updateView(state, progress);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(PROGRESS, progress);
        outState.putInt(STATE, state);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private static final String TAG = "InitSplash";
    private static final String PROGRESS = "PROGRESS";
    private static final String STATE = "STATE";
}
