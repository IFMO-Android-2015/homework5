package ru.ifmo.android_2015.homework5;

import android.app.Activity;
<<<<<<< HEAD
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
=======
import android.content.Context;
>>>>>>> e7f6957552304d58e82ee160eff70f5c12bcab4f
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
<<<<<<< HEAD
    private DownloadState downloadState;
    private  BroadcastReceiver broadcastReceiver;
=======
    // Выполняющийся таск загрузки файла
    private DownloadFileTask downloadTask;
>>>>>>> e7f6957552304d58e82ee160eff70f5c12bcab4f

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

<<<<<<< HEAD
        if (savedInstanceState == null) {
            Log.d(TAG, "startService");
            downloadState = DownloadState.DOWNLOADING;
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        }

        if (downloadState != DownloadState.ERROR && downloadState != DownloadState.DONE) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    downloadState = (DownloadState) intent.getSerializableExtra("downloadState");
                    int progress = intent.getIntExtra("progress", 0);
                    //Log.d(TAG, downloadState.titleResId + " ");
                    if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                        unregisterReceiver(broadcastReceiver);
                    }
                    updateView(downloadState, progress);
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter("HomeworkDownloadService"));

        }
    }

    void updateView(DownloadState state, int progress) {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("downloadState", downloadState);
        outState.putInt("progress", progressBarView.getProgress());
    }


    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        downloadState = (DownloadState) savedInstanceState.getSerializable("downloadState");
        updateView(downloadState, savedInstanceState.getInt("progress"));
=======
        if (savedInstanceState != null) {
            // Пытаемся получить ранее запущенный таск
            downloadTask = (DownloadFileTask) getLastNonConfigurationInstance();
        }
        if (downloadTask == null) {
            // Создаем новый таск, только если не было ранее запущенного таска
            downloadTask = new DownloadFileTask(this);
            downloadTask.execute();
        } else {
            // Передаем в ранее запущенный таск текущий объект Activity
            downloadTask.attachActivity(this);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        // Этот метод вызывается при смене конфигурации, когда текущий объект
        // Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
        // и его можно будет использовать в новом объекте Activity
        return downloadTask;
>>>>>>> e7f6957552304d58e82ee160eff70f5c12bcab4f
    }

    /**
     * Состояние загрузки в DownloadFileTask
     */
    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        // ID строкового ресурса для заголовка окна прогресса
<<<<<<< HEAD
         int titleResId;
=======
        final int titleResId;
>>>>>>> e7f6957552304d58e82ee160eff70f5c12bcab4f

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

<<<<<<< HEAD

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Если все готово(тогда мы отписались от receiver), а мы вертим экран,
        // то нам бы не хотелось падать отписываясь от receiver
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {

=======
    /**
     * Таск, выполняющий скачивание файла в фоновом потоке.
     */
    static class DownloadFileTask extends AsyncTask<Void, Integer, DownloadState>
            implements ProgressCallback {

        // Context приложения (Не Activity!) для доступа к файлам
        private Context appContext;
        // Текущий объект Activity, храним для обновления отображения
        private InitSplashActivity activity;

        // Текущее состояние загрузки
        private DownloadState state = DownloadState.DOWNLOADING;
        // Прогресс загрузки от 0 до 100
        private int progress;

        DownloadFileTask(InitSplashActivity activity) {
            this.appContext = activity.getApplicationContext();
            this.activity = activity;
        }

        /**
         * Этот метод вызывается, когда новый объект Activity подключается к
         * данному таску после смены конфигурации.
         *
         * @param activity новый объект Activity
         */
        void attachActivity(InitSplashActivity activity) {
            this.activity = activity;
            updateView();
        }

        /**
         * Вызываем на UI потоке для обновления отображения прогресса и
         * состояния в текущей активности.
         */
        void updateView() {
            if (activity != null) {
                activity.titleTextView.setText(state.titleResId);
                activity.progressBarView.setProgress(progress);
            }
        }

        /**
         * Вызывается в UI потоке из execute() до начала выполнения таска.
         */
        @Override
        protected void onPreExecute() {
            updateView();
        }

        /**
         * Скачивание файла в фоновом потоке. Возвращает результат:
         *      0 -- если файл успешно скачался
         *      1 -- если произошла ошибка
         */
        @Override
        protected DownloadState doInBackground(Void... ignore) {
            try {
                downloadFile(appContext, this /*progressCallback*/);
                state = DownloadState.DONE;

            } catch (Exception e) {
                Log.e(TAG, "Error downloading file: " + e, e);
                state = DownloadState.ERROR;
            }
            return state;
        }

        // Метод ProgressCallback, вызывается в фоновом потоке из downloadFile
        @Override
        public void onProgressChanged(int progress) {
            publishProgress(progress);
        }

        // Метод AsyncTask, вызывается в UI потоке в результате вызова publishProgress
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values.length > 0) {
                int progress = values[values.length - 1];
                this.progress = progress;
                updateView();
            }
        }

        @Override
        protected void onPostExecute(DownloadState state) {
            // Проверяем код, который вернул doInBackground и показываем текст в зависимости
            // от результата
            this.state = state;
            if (state == DownloadState.DONE) {
                progress = 100;
            }
            updateView();
>>>>>>> e7f6957552304d58e82ee160eff70f5c12bcab4f
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
