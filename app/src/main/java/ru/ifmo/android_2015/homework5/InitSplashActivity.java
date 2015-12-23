package ru.ifmo.android_2015.homework5;

import android.app.Activity;
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
    private static final String DOWNLOAD_PROGRESS = "downloadProgress";
    private static final String DOWNLOAD_FAILED = "downloadFaield";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    private BroadcastReceiver receiver = null;

    private int downloadProgress;
    private boolean downloadFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        if (savedInstanceState != null) {
            downloadProgress = savedInstanceState.getInt(DOWNLOAD_PROGRESS, 0);
            downloadFailed = savedInstanceState.getBoolean(DOWNLOAD_FAILED, false);
        }

        initUI();
        updateUI();

        //start service
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(DownloadService.URL_EXTRA, CITIES_GZ_URL);
        startService(intent);
        Log.d(TAG, "Service started");
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putBoolean(DOWNLOAD_FAILED, downloadFailed);
        bundle.putInt(DOWNLOAD_PROGRESS, downloadProgress);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //register the broadcast receiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Broadcast received");
                boolean errorFlag = intent.getBooleanExtra(DownloadService.ERROR_FLAG_EXTRA, false);
                boolean successFlag = intent.getBooleanExtra(DownloadService.SUCCESS_FLAG_EXTRA, false);

                if (!errorFlag) {
                    if (successFlag) {
                        downloadProgress = 100;
                    } else {
                        downloadProgress = intent.getIntExtra(DownloadService.PROGRESS_EXTRA, 0);
                    }
                } else {
                    downloadFailed = true;
                }

                updateUI();
            }
        };
        IntentFilter filter = new IntentFilter(DownloadService.REPORT_PROGRESS_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void updateUI() {
        if (downloadFailed) {
            titleTextView.setText(getResources().getString(R.string.error));
        } else {
            if (downloadProgress == 100) {
                titleTextView.setText(getResources().getString(R.string.done));
            } else {
                titleTextView.setText(getResources().getString(R.string.downloading));
                progressBarView.setProgress(downloadProgress);
            }
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        super.onPause();
    }

    private void initUI() {
        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarView.setMax(100);
    }

    private static final String TAG = "InitSplash";
}
