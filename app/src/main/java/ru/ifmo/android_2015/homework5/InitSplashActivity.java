package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    private static final Integer MY_REQUEST = 0;

    private static final String DOWNLOAD_STATUS = "Download_Status";

    private boolean status = false;

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
            status = savedInstanceState.getBoolean(DOWNLOAD_STATUS);
        }
        if (!status) {
            PendingIntent pi = createPendingResult(MY_REQUEST, new Intent(), 0);
            Intent intent = new Intent(getApplicationContext(), DownloadService.class);
            intent.putExtra(DownloadService.P_INTENT, pi);
            startService(intent);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        if (request == MY_REQUEST) {
            switch (result) {
                case DownloadService.Progress:
                    int progress = intent.getIntExtra(DownloadService.DOWNLOAD_PROGRESS, 0);

                    if (progress == 100) {
                        titleTextView.setText(R.string.done);
                    } else {
                        status = true;
                        titleTextView.setText(R.string.downloading);
                    }
                    progressBarView.setProgress(progress);
                    break;
                case DownloadService.Error:
                    status = false;
                    titleTextView.setText(R.string.error);
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(DOWNLOAD_STATUS, status);
    }


    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}