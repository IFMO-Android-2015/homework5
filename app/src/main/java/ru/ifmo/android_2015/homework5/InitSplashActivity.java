package ru.ifmo.android_2015.homework5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Экран, выполняющий инициализацию при первом запуске приложения. В процессе инициализации
 * скачивается файл с данными, нужными для работы приложения. Пока идет инициализация, показывается
 * сплэш-скрин с индикатором прогресса.
 */
public class InitSplashActivity extends FragmentActivity {
    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    private boolean initializationFinished;
    private DownloadProgressReceiver receiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        if (!initializationFinished) {
            receiver = (DownloadProgressReceiver) getLastCustomNonConfigurationInstance();
            if (receiver != null) {
                receiver.setActivity(this);
                receiver.updateView();
            } else {
                receiver  = new DownloadProgressReceiver(this);
                LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(DownloadService.ACTION_DOWNLOAD_PROGRESS_UPDATED));
            }
        }

        if (savedInstanceState == null) {
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        }
    }

    private void onDownloadProgress(Intent initializationServiceBroadcast) {
        DownloadService.DownloadState downloadState = (DownloadService.DownloadState) initializationServiceBroadcast.getSerializableExtra(DownloadService.EXTRA_DOWNLOAD_STATE);
        int progress = initializationServiceBroadcast.getIntExtra(DownloadService.EXTRA_PROGRESS, 0);

        titleTextView.setText(downloadState.titleResId);
        progressBarView.setProgress(progress);

        if (downloadState != DownloadService.DownloadState.DOWNLOADING) {
            initializationFinished = true;
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return receiver;
    }

    public class DownloadProgressReceiver extends BroadcastReceiver {
        private WeakReference<InitSplashActivity> activityRef;
        private Intent lastIntent;
        private long lastIntentIndex;

        public DownloadProgressReceiver(InitSplashActivity activity) {
            setActivity(activity);
            setDebugUnregister(true);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            lastIntent = intent;
            long intentIndex = intent.getLongExtra(DownloadService.EXTRA_INTENT_INDEX, 0);
            if (intentIndex > lastIntentIndex) {
                lastIntentIndex = intentIndex;
                updateView();
            }
        }

        public void updateView() {
            InitSplashActivity activity = this.activityRef.get();
            if (activity != null && lastIntent != null) {
                activity.onDownloadProgress(lastIntent);
            }
        }

        public void setActivity(InitSplashActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }
    }

    private static final String TAG = "InitSplash";
}
