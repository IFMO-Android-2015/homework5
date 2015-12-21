package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.Intent;
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

    public static final String TAG = InitSplashActivity.class.getSimpleName();

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.TAG, TAG+":onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarView.setMax(Constants.PROGRESS_MAX_VALUE);

        // when attaching wrapped by produce memory-leak successfully caught by LeakCanary (demo)
        /*if (LoaderState.getInstance().state == null) {
            LoaderState.getInstance().attachActivity(this);
        }*/

        LoaderState.getInstance().attachActivity(this);

        if (LoaderState.getInstance().state == null) {
            LoaderState.getInstance().state = LoaderState.DownloadState.DOWNLOADING;
            Intent intent = new Intent(this, LoaderService.class);
            intent.putExtra("messenger", LoaderState.getInstance().messenger);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constants.TAG, TAG+":onDestroy()");
        LoaderState.getInstance().detachActivity();
    }

    /**
     * Вызываем на UI потоке для обновления отображения прогресса и
     * состояния в текущей активности.
     */
    public void updateView() {
        if (LoaderState.getInstance().state == null) {
            return;
        }
        titleTextView.setText(LoaderState.getInstance().state.titleResId);
        progressBarView.setProgress(LoaderState.getInstance().progress);
    }
}
