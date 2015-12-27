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
    private static final String TAG = "InitSplash";
    private static final String TITLE_TEXT = "TITLE_TEXT";
    private ProgressBar progressBarView;
    private TextView titleTextView;
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
            Log.d(TAG, "Service is already working");
        } else {
            DownloadService.addDownload(this, CITIES_GZ_URL);
            Log.d(TAG, "Download started");
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Progress updated");
                updateView(intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter(DownloadService.ACTION_BROADCAST);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, intentFilter);
    }

    private void updateView (Intent intent) {
        if (intent != null) {
            final int title = intent.getIntExtra(DownloadService.TITLE_PARAMETER, 0);
            final int progress = intent.getIntExtra(DownloadService.PROGRESS_PARAMETER, 0);
            titleTextView.setText(title);
            progressBarView.setProgress(progress);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String label = savedInstanceState.getString(TITLE_TEXT);
            titleTextView.setText(label);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TITLE_TEXT, titleTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
        super.onDestroy();
    }
}
