package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private String downloading;
    private MyBroadcastReceiver myBroadcastReceiver;
    private MyUpdateBroadcastReceiver myUpdateBroadcastReceiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent.putExtra("url", CITIES_GZ_URL));

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("ru.ifmo.android_2015.homework5.RESPONSE");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        myUpdateBroadcastReceiver = new MyUpdateBroadcastReceiver();
        IntentFilter intentFilter1 = new IntentFilter("ru.ifmo.android_2015.homework5.UPDATE");
        intentFilter1.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myUpdateBroadcastReceiver, intentFilter1);

    }

    private int progress;

    void updateView() {
        if (progress != -1) {
            titleTextView.setText(progress + "%");
        } else {
            titleTextView.setText("Ошибка");
        }
        progressBarView.setProgress(progress);
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra("Out", 0);
            if (result == 11) {
                progress = 100;
            } else {
                progress = -1;
            }
            updateView();
            unregisterReceiver(myBroadcastReceiver);
            unregisterReceiver(myUpdateBroadcastReceiver);
        }
    }

    public class MyUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra("Update", 0);
            publishProgress(update);
        }
    }

    public void publishProgress(Integer... values) {
        if (values.length > 0) {
            progress = values[values.length - 1];
            updateView();
        }
    }

    /**
     * Скачивает список городов во временный файл.
     */


    static void downloadFile(Context context,
                             ProgressCallback progressCallback, String url) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(url, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}
