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

    public static final String EXTRA_PROGRESS = "progress";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_FILTER = "service";

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
            Log.d(TAG, "Start service");
            downloadState = DownloadState.DOWNLOADING;
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        }
        if (downloadState != DownloadState.DONE) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    downloadState = (DownloadState) intent.getSerializableExtra(EXTRA_STATE);
                    int progress = intent.getIntExtra(EXTRA_PROGRESS, 0);
                    if (downloadState == DownloadState.DONE || downloadState == DownloadState.ERROR) {
                        unregisterReceiver(broadcastReceiver);
                    }
                    updateView(downloadState, progress);
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter(EXTRA_FILTER));
        }
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        downloadState = (DownloadState) savedInstanceState.getSerializable(EXTRA_STATE);
        updateView(downloadState, savedInstanceState.getInt(EXTRA_PROGRESS));
    }

    void updateView(DownloadState state, int progress) {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_STATE, downloadState);
        outState.putInt(EXTRA_PROGRESS, progressBarView.getProgress());
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")

    public Object onRetainNonConfigurationInstance() {
        return broadcastReceiver;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Failed to destroy");
        }
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