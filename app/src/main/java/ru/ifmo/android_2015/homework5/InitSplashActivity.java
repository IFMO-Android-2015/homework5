package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

    private static final String TITLE = "TITLE";
    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
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
            Log.d(TAG, "Service has started");
        }
        else if (!isRunning(Downloader.class)) {
            Log.d(TAG, "Download started");
            Intent intent = new Intent(this, Downloader.class);
            intent.setAction(Downloader.ACTION_DOWNLOAD);
            intent.putExtra(Downloader.PARAM_URL, CITIES_GZ_URL);
            startService(intent);
        }
        else {
            Log.d(TAG, "Waiting for runnig service to finish");
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateView(intent);
            }
        };
        IntentFilter filter = new IntentFilter(Downloader.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void updateView(Intent intent) {
        if (intent == null) {
            return;
        }
        final int titleID = intent.getIntExtra(Downloader.PARAM_TITLE, 0);
        final int progress = intent.getIntExtra(Downloader.PARAM_PROGRESS, 0);
        titleTextView.setText(titleID);
        progressBarView.setProgress(progress);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TITLE, titleTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String s = savedInstanceState.getString(TITLE);
            titleTextView.setText(s);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private boolean isRunning(Class<?> service) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private static final String TAG = "InitSplash";
}
