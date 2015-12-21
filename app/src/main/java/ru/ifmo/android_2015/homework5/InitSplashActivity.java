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

import static ru.ifmo.android_2015.homework5.DownloadState.*;

public class InitSplashActivity extends Activity {

    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    private ProgressBar progressBarView;
    private TextView titleTextView;
    private DownloadState downloadState;
    private int progress;
    private DownloadReceiver receiver;

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
            downloadState = DOWNLOADING;
            progress = 0;
            startService(new Intent(this, DownloadService.class));
        }
        if (downloadState != DONE) {
            receiver = new DownloadReceiver(this);
            registerReceiver(receiver, new IntentFilter(HOMEWORK));
        }
    }

    private static class DownloadReceiver extends BroadcastReceiver {

        private InitSplashActivity activity;

        public DownloadReceiver(InitSplashActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            activity.downloadState = (DownloadState) intent.getSerializableExtra(DOWNLOAD_STATE);
            activity.progress = intent.getIntExtra(PROGRESS, 0);
            if (activity.downloadState != DOWNLOADING) {
                activity.unregisterReceiver(activity.receiver);
            }
            activity.updateView();
        }
    }

    private void updateView() {
        titleTextView.setText(downloadState.titleResId);
        if (downloadState != DOWNLOADING) {
            progressBarView.setVisibility(ProgressBar.INVISIBLE);
        } else {
            progressBarView.setVisibility(ProgressBar.VISIBLE);
            progressBarView.setProgress(progress);
        }
    }

    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        Log.e(TAG, "start downloading");
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        Log.e(TAG, "made file");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        downloadState = (DownloadState) savedInstanceState.getSerializable(DOWNLOAD_STATE);
        progress = savedInstanceState.getInt(PROGRESS);
        updateView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(DOWNLOAD_STATE, downloadState);
        outState.putInt(PROGRESS, progress);
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return receiver;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception ignored) {}
    }

    private static final String TAG = "InitSplash";
}
