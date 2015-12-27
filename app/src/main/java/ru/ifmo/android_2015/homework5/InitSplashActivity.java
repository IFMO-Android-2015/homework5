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


public class InitSplashActivity extends Activity {

    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    private ProgressBar progressBarView;
    private DownloadState downloadState;
    private TextView titleTextView;
    private BroadcastReceiver broadcastReceiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);


        if (savedInstanceState == null) {
            downloadState = DownloadState.DOWNLOADING;
            startReceiver();
            Intent intent = new Intent(this, DownloadService.class);
            intent.setAction("DOWNLOAD");
            startService(intent);
        }
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onRestore");
        outState.putSerializable("download_state", downloadState);
        outState.putInt("progress", progressBarView.getProgress());
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestore");
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            downloadState = (DownloadState) savedInstanceState.getSerializable("download_state");
            updateView(downloadState, savedInstanceState.getInt("progress"));
            startReceiver();
        }
    }

    private void startReceiver() {
        if (downloadState == DownloadState.DOWNLOADING ) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    downloadState = (DownloadState) intent.getSerializableExtra("download_state");
                    int progress = intent.getIntExtra("progress", 0);
                    if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                        unregisterReceiver(broadcastReceiver);
                    }
                    updateView(downloadState, progress);
                }
            };

            IntentFilter intFilt = new IntentFilter(String.valueOf(R.string.action));
            registerReceiver(broadcastReceiver, intFilt);
        }
    }

    void updateView(DownloadState state, int progress) {
        if (state.titleResId == R.string.downloading) {
            titleTextView.setText("Загрузка..." + " " + progress + "%");
        } else {
            titleTextView.setText(state.titleResId);
        }
        progressBarView.setProgress(progress);
    }


    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return broadcastReceiver;
    }


    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}