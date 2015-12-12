package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by novik on 11.12.15.
 */
public class ProgressReceiver extends BroadcastReceiver {
    private static final String TAG = ProgressReceiver.class.getSimpleName();

    private InitSplashActivity activity;
    private TextView titleTextView;
    private ProgressBar progressBarView;

    private int progress = 0, status = 0;
    private boolean stoped = false;

    ProgressReceiver(InitSplashActivity activity) {
        this.activity = activity;
        init();

        IntentFilter filter = new IntentFilter(DownloadService.ACTION);
        LocalBroadcastManager.getInstance(activity).registerReceiver(this, filter);
    }

    public void attachActivity(InitSplashActivity activity) {
        this.activity = activity;
        init();
        updateView();
    }

    private void init() {
        titleTextView = (TextView) activity.findViewById(R.id.title_text);
        progressBarView = (ProgressBar) activity.findViewById(R.id.progress_bar);
    }

    private void stop() {
        if (!stoped) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(this);
            stoped = true;
        }
    }

    void updateView() {
        if (activity != null) {
            switch (progress) {
                case 100:
                    progressBarView.setProgress(progress);
                    titleTextView.setText(R.string.done);
                    stop();
                    break;
                case -1:
                    progressBarView.setVisibility(View.INVISIBLE);
                    titleTextView.setText(R.string.error);
                    stop();
                    break;
                default:
                    progressBarView.setProgress(progress);
                    titleTextView.setText(R.string.downloading);
            }
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        status = intent.getIntExtra(DownloadService.STATUS_TAG, -1);
        progress = intent.getIntExtra(DownloadService.PROGRESS_TAG, -1);
        updateView();
    }
}
