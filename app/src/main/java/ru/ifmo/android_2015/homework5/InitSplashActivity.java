package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.Context;
import android.app.PendingIntent;
import android.os.Bundle;
import android.content.Intent;
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
    private static final Integer REQUESTED = 0;
    private static final String DOWNLOAD_STATE = "is_downloading";
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    private boolean downloadStarted = false;

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
            downloadStarted = savedInstanceState.getBoolean(DOWNLOAD_STATE);
        }
        if (!downloadStarted) {
            PendingIntent pendingIntent = createPendingResult(REQUESTED, new Intent(), 0);
            Intent intent = new Intent(getApplicationContext(), FileDownloadService.class).putExtra(FileDownloadService.PENDING_INTENT, pendingIntent);
            startService(intent);
        }
    }


    private void handleDone(Intent data) {
        titleTextView.setText(R.string.done);
        progressBarView.setProgress(data.getIntExtra(FileDownloadService.DOWNLOAD_PROGRESS, 0));
    }

    private void handleError(Intent data) {
        downloadStarted = false;
        titleTextView.setText(R.string.error);
    }

    private void handleProgress(Intent data) {
        downloadStarted = true;
        titleTextView.setText(R.string.downloading);
        progressBarView.setProgress(data.getIntExtra(FileDownloadService.DOWNLOAD_PROGRESS, 0));
    }


    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        if (request == REQUESTED) {
            switch (result) {
                case FileDownloadService.Done:
                    handleDone(intent);
                    break;
                case FileDownloadService.Error:
                    handleError(intent);
                    break;
                case FileDownloadService.InProgress:
                    handleProgress(intent);
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(DOWNLOAD_STATE, downloadStarted);
    }

    /**
     * Скачивает список городов во временный файл.
     */
    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}
