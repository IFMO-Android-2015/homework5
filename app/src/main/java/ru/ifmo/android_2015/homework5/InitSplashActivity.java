package ru.ifmo.android_2015.homework5;

import android.app.Activity;
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

public class InitSplashActivity extends Activity {
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    private ProgressBar progressBarView;
    private TextView titleTextView;

    private BroadcastReceiver broadcastReceiver;
    private int progress;
    private State state;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarView.setMax(100);


        broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                state = (State) intent.getSerializableExtra(DownloadService.EXTRA_PARAM_STATE);
                progress = intent.getIntExtra(DownloadService.EXTRA_PARAM_PROGRESS, 0);
                updateView();
            }
        };

        IntentFilter intentFilter = new IntentFilter(DownloadService.ACTION_BROADCAST);
        registerReceiver(broadcastReceiver, intentFilter);

        if (savedInstanceState == null) {
            Intent intentDownloadService = new Intent(this, DownloadService.class);
            intentDownloadService.setAction(DownloadService.ACTION_DOWNLOAD);
            intentDownloadService.putExtra(DownloadService.EXTRA_PARAM_URL, CITIES_GZ_URL);
            startService(intentDownloadService);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void updateView() {
        titleTextView.setText(state.titleResId);
        progressBarView.setProgress(progress);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        progress = savedInstanceState.getInt("progress");
        state = (State) savedInstanceState.getSerializable("state");
        updateView();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("progress", progress);
        outState.putSerializable("state", state);
    }
}
