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

    private static final String PROGRESS_STATE = "PROGRESS_STATE";
    private static final String DOWNLOAD_STATE = "DOWNLOAD_STATE";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    private DownloadState state;
    private int progress;

    private BroadcastReceiver receiver = null;


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

    private void updateView() {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }

    void initBroadcast() {
        state = DownloadState.DOWNLOADING;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(DownloadService.ACTION_PROGRESS)) {
                    progress = intent.getIntExtra(DownloadService.PROGRESS, 0);
                    updateView();
                }

                if (action.equals(DownloadService.ACTION_FINISH)) {
                    progress = 100;
                    state = DownloadState.DONE;
                    updateView();
                    unregisterReceiver(receiver);
                }

                if (action.equals(DownloadService.ACTION_ERROR)) {
                    progress = 0;
                    state = DownloadState.ERROR;
                    updateView();
                    unregisterReceiver(receiver);
                }

            }
        };

        IntentFilter progressFilter = new IntentFilter(DownloadService.ACTION_PROGRESS);
        registerReceiver(receiver, progressFilter);
        IntentFilter finishFilter = new IntentFilter(DownloadService.ACTION_FINISH);
        registerReceiver(receiver, finishFilter);
        IntentFilter errorFilter = new IntentFilter(DownloadService.ACTION_ERROR);
        registerReceiver(receiver, errorFilter);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        initBroadcast();

        if (savedInstanceState != null) {
            progress = savedInstanceState.getInt(PROGRESS_STATE, 0);
            state = ((DownloadState) savedInstanceState.getSerializable(DOWNLOAD_STATE));
            updateView();
        } else {
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_URL, CITIES_GZ_URL);
            startService(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PROGRESS_STATE, progress);
        outState.putSerializable(DOWNLOAD_STATE, state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "receiver is not registred");
            }
        }
    }

    private static final String TAG = "InitSplash";
}
