package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
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
    private static final String DOWNLOADING_STATE = "State";
    private static final int REQUEST = 0;
    public static final int FLAGS = 0;
    private boolean startStatus = false;
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

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
            // Пытаемся получить ранее запущенный таск
            startStatus = savedInstanceState.getBoolean(DOWNLOADING_STATE);
        }
        if (!startStatus) {
            PendingIntent pendingIntent = createPendingResult(REQUEST, new Intent(), FLAGS);
            Intent intent = new Intent(getApplicationContext(), DownloadService.class);
            intent.putExtra(DownloadService.PENDING, pendingIntent);
            startService(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST) {
            switch (resultCode) {
                case DownloadService.Done:
                    titleTextView.setText(R.string.done);
                    progressBarView.setProgress(data.getIntExtra(DownloadService.DOWNLOADING_PROGRESS, FLAGS));
                    break;
                case DownloadService.Downloading:
                    startStatus = true;
                    titleTextView.setText(R.string.downloading);
                    progressBarView.setProgress(data.getIntExtra(DownloadService.DOWNLOADING_PROGRESS, FLAGS));
                    break;
                case DownloadService.Error:
                    startStatus = false;
                    titleTextView.setText(R.string.error);
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(DOWNLOADING_STATE, startStatus);
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
