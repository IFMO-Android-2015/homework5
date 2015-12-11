package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.Context;
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

    // Урл для скачивания файла с данными, нужными для инициализации приложения при первом запуске.
    // GZIP-архив, содержащий список городов в формате JSON.
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    // Receiver of local broadcast from the DownloadService
    private DownloadReceiver downloadReceiver;

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
            // Пытаемся получить ранее запущенный ресивер
            downloadReceiver = (DownloadReceiver) getLastNonConfigurationInstance();
        }
        if (downloadReceiver == null) {
            // Start receiver and add new task for the service
            downloadReceiver = new DownloadReceiver(this);

            Intent downloadFile = new Intent(this, DownloadService.class);
            downloadFile.putExtra(DownloadService.URL_FIELD, CITIES_GZ_URL);
            startService(downloadFile);
        } else {
            // Attach receiver to the activity
            downloadReceiver.attach(this);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        // Этот метод вызывается при смене конфигурации, когда текущий объект
        // Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
        // и его можно будет использовать в новом объекте Activity
        return downloadReceiver;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Состояние загрузки в DownloadReceiver
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

    static class DownloadReceiver extends AttachableReceiver {
        private DownloadState state;
        private int progress;

        public DownloadReceiver(Context context) {
            super(context, DownloadService.BROADCAST_NAME);
        }

        // Update progress bar and header
        private void updateView(InitSplashActivity activity) {
            activity.titleTextView.setText(state.titleResId);
            activity.progressBarView.setProgress(progress);
        }

        // If we receive message from the Download Service
        @Override
        public void onMessageReceive(Context context, Intent message) {
            InitSplashActivity activity = (InitSplashActivity) context;

            int status = message.getIntExtra(DownloadService.STATUS_FIELD, DownloadService.ERROR);
            String url = message.getStringExtra(DownloadService.URL_FIELD);

            // Is url of file ours?
            if (!url.equals(CITIES_GZ_URL)) {
                return;
            }

            switch (status) {
                case DownloadService.DOWNLOADING:
                    progress = message.getIntExtra(DownloadService.PROGRESS_FIELD, -1);
                    state = DownloadState.DOWNLOADING;
                    break;
                case DownloadService.FINISHED:
                    state = DownloadState.DONE;
                    progress = 100;
                    stop(context);  // Stop receiver
                    activity.downloadReceiver = null; // Delete self
                    break;
                case DownloadService.ERROR:
                    String error = message.getStringExtra(DownloadService.ERROR_FIELD);
                    Log.e(TAG, "Error downloading file: " + error);
                    state = DownloadState.ERROR;
                    stop(context); // Stop receiver
                    activity.downloadReceiver = null; // Delete self
            }
            updateView(activity);
        }
    }

    private static final String TAG = "InitSplash";
}
