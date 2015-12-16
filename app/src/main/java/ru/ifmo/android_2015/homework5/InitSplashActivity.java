package ru.ifmo.android_2015.homework5;
import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;
    // Выполняющийся таск загрузки файла
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    public final static String STATUS = "status", PROGRESS = "progress",  BROADCAST_ACTION = "ru.ifmo.android_2015.homework5";
    public final static int DONE = 1, DOWNLOADING = 2, ERROR = 0;
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);



        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int currState = intent.getIntExtra(STATUS,0);

                switch (currState) {
                    case DONE: progressBarView.setProgress(100);
                        titleTextView.setText("DONE");
                        break;
                    case DOWNLOADING: progressBarView.setProgress(intent.getIntExtra(PROGRESS,0));
                        titleTextView.setText("DOWNLOADING...");
                        break;
                    default:
                        titleTextView.setText(R.string.error);
                }
            }
        };
        filter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(receiver, filter);
        if (savedInstanceState == null) {
            Intent buf =  new Intent(this,DownloadService.class);

            startService(buf);
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

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    private static final String TAG = "InitSplash";
}
