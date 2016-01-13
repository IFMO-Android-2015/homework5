package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Экран, выполняющий инициализацию при первом запуске приложения. В процессе инициализации
 * скачивается файл с данными, нужными для работы приложения. Пока идет инициализация, показывается
 * сплэш-скрин с индикатором прогресса.
 */
public class InitSplashActivity extends Activity {

    private static int PROGRESS_MAX = 100;

    private ProgressBar progressBarView;
    private TextView titleTextView;

    private BroadcastReceiver receiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarView.setMax(PROGRESS_MAX);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action = intent.getAction();
                    if (action.equals(MyApplication.ACTIONS.PUBLISH_PROGRESS)) {
                        int progress = intent.getIntExtra(MyApplication.EXTRA_PARAMS.PROGRESS, -1);
                        updateView(MyApplication.getState(), progress);
                    }
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(MyApplication.ACTIONS.PUBLISH_PROGRESS);

        if (MyApplication.getState() == null) {
            Intent intent = new Intent(this, DownloadService.class);
            intent.setAction(MyApplication.ACTIONS.DOWNLOAD);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        updateView(MyApplication.getState(), MyApplication.getProgress());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    /**
     * Вызываем на UI потоке для обновления отображения прогресса и
     * состояния в текущей активности.
     */
    void updateView(MyApplication.DownloadState state, int progress) {
        if (state != null) {
            titleTextView.setText(state.titleResId);
            if (progress != -1) {
                progressBarView.setProgress(progress);
            }
        }
    }
}
