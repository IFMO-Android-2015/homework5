package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    // Выполняющийся таск загрузки файла


    public static InitSplashActivity activity;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        startService(new Intent(getApplicationContext(), DownloadIntentService.class));

    }




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



    public static class DownloadIntentService extends IntentService {

        DownloadState state = DownloadState.DOWNLOADING;

        public DownloadIntentService() {
            super("download_service");

        }

        @Override
        protected void onHandleIntent(Intent intent) {
            try {
                Log.e("status: ", "started downloading: ");
                class ProgressRunnable implements Runnable {

                    int progress;

                    public ProgressRunnable(int progress) {
                        this.progress = progress;
                    }

                    public void run() {
                        activity.progressBarView.setProgress(progress);
                        activity.titleTextView.setText(state.titleResId);
                    }
                }
                class MyProgressCallback implements ProgressCallback {
                    public void onProgressChanged(int progress) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new ProgressRunnable(progress));
                    }
                }
               downloadFile(activity.getApplicationContext(), new MyProgressCallback());
                state = DownloadState.DONE;
            } catch (Exception e) {
                state = DownloadState.ERROR;
            } finally {
                stopSelf();
            }

        }
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
