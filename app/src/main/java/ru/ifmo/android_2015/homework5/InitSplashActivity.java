package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
    //Обработчик прогресса загрузки файла
    private BroadcastReceiver receiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        if (savedInstanceState != null) {
            //Сервис уже загружает или загрузил картинку
            Log.d(TAG, "Saved instance state not null");
        } else {
            DownloadFileService.queueDownload(this, CITIES_GZ_URL);
            Log.d(TAG, "Started download");
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Progress updated");
                updateView(intent);
            }
        };

        IntentFilter filter = new IntentFilter(DownloadFileService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, filter);
    }

    private void updateView(Intent intent) {
        if (intent != null) {
            final int titleId = intent.getIntExtra(DownloadFileService.PARAM_TITLE, 0);
            final int progress = intent.getIntExtra(DownloadFileService.PARAM_PROGRESS, 0);
            titleTextView.setText(titleId);
            progressBarView.setProgress(progress);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String s = savedInstanceState.getString(LABEL_TEXT);
            titleTextView.setText(s);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //TextView не сохраняет свой текст
        outState.putString(LABEL_TEXT, titleTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
        super.onDestroy();
    }

    private static final String TAG = "InitSplash";
    private static final String LABEL_TEXT = "LABEL_TEXT";
}
