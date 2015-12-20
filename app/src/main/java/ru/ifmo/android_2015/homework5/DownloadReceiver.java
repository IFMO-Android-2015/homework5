package ru.ifmo.android_2015.homework5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadReceiver extends BroadcastReceiver {
    private InitSplashActivity activity;
    private TextView titleTextView;
    private ProgressBar progressBarView;
    private int progress = 0;
    private boolean status = true;

    DownloadReceiver(InitSplashActivity activity) {
        this.activity = activity;
        updateProgress();
        IntentFilter filter = new IntentFilter(DownloadService.ACTION);
        LocalBroadcastManager.getInstance(activity).registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        progress = intent.getIntExtra(DownloadService.PROGRESS_TAG, -1);
        updateView();
    }

    // Передаем в ранее запущенный таск текущий объект Activity
    public void attachActivity(InitSplashActivity activity) {
        this.activity = activity;
        updateProgress();
        updateView();
    }

    private void updateProgress() {
        titleTextView = (TextView) activity.findViewById(R.id.title_text);
        progressBarView = (ProgressBar) activity.findViewById(R.id.progress_bar);
    }

    void updateView() {
        if (activity != null) {
            if (progress == -1) {
                progressBarView.setVisibility(View.INVISIBLE);
                titleTextView.setText(R.string.error);
                stopReceiver();
            } else if (progress == 100) {
                progressBarView.setProgress(progress);
                titleTextView.setText(R.string.done);
                stopReceiver();
            } else {
                progressBarView.setProgress(progress);
                titleTextView.setText(R.string.downloading);
            }
        }
    }

    private void stopReceiver() {
        if (status) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(this);
            status = false;
        }
    }
}