package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private static final Integer REQUEST_CODE = 0;
    private static final String DOWNLOADING_STATE = "is_downloading";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    private boolean isDownloading = false;

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
            isDownloading = savedInstanceState.getBoolean(DOWNLOADING_STATE);
        }

        if (!isDownloading) {
            PendingIntent pendingIntent = createPendingResult(REQUEST_CODE, new Intent(), 0);
            Intent intent = new Intent(getApplicationContext(), DownloadFileService.class)
                    .putExtra(DownloadFileService.PENDING_INTENT, pendingIntent);
            startService(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DOWNLOADING_STATE, isDownloading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case DownloadFileService.DONE:
                    handleDone(data);
                    break;
                case DownloadFileService.DOWNLOADING:
                    handleDownloading(data);
                    break;
                case DownloadFileService.ERROR:
                    handleError(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleDownloading(Intent data) {
        isDownloading = true;
        titleTextView.setText(R.string.downloading);
        progressBarView.setProgress(data.getIntExtra(DownloadFileService.PROGRESS, 0));
    }

    private void handleDone(Intent data) {
        titleTextView.setText(R.string.done);
        progressBarView.setProgress(data.getIntExtra(DownloadFileService.PROGRESS, 0));
        isDownloading = false;
    }

    private void handleError(Intent data) {
        isDownloading = false;
        titleTextView.setText(R.string.error);
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
