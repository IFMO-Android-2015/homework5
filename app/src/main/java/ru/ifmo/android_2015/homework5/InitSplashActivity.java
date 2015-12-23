package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
    protected static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";
    protected static final String STATE = "STATE", PROGRESS = "PROGRESS",
            BROADCAST_ACTION = "ru.ifmo.android_2015.homework5.BROADCAST_ACTION", SAVED_STATE = "SAVED_STATE";
    protected static final int FINISHED_STATE = 0, DOWNLOADING_STATE = 1, ERROR_STATE = -1, MAX_PROGRESS = 100;
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

        progressBarView.setMax(MAX_PROGRESS);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadState state = DownloadState.ERROR;
                int param = intent.getIntExtra(STATE, 0);
                int progress = intent.getIntExtra(PROGRESS, 0);
                System.out.println("#" + param);
                switch (param) {
                    case FINISHED_STATE:
                        state = DownloadState.DONE;
                        break;
                    case DOWNLOADING_STATE:
                        state = DownloadState.DOWNLOADING;
                        break;
                }
                titleTextView.setText(state.titleResId);
                progressBarView.setProgress(progress);
            }
        };

        IntentFilter filter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(receiver, filter);

        if (savedInstanceState == null) {
            final Intent service = new Intent(this, DownloadService.class);
            startService(service);
        }
    }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_STATE, titleTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            titleTextView.setText(savedInstanceState.getString(SAVED_STATE));
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private static final String TAG = "InitSplash";
}
