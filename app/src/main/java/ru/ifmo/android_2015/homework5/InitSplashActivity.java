package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InitSplashActivity extends Activity {

    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";
    private static final String TAG = "InitSplash";
    private static final String TITLE_TEXT = "TITLE_TEXT";

    private ProgressBar progressBarView;
    private TextView titleTextView;
    private BroadcastReceiver receiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate happened");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarView.setMax(100);

        if (savedInstanceState != null) {
            Log.d(TAG, "Service has already started");
        }
        else if (!isServiceRunning(DownloadService.class)) {
            /**
             * workaround to avoid queueing more download attempts
             * when the initial one is still running
             *
             * this should NOT be used if there will be
             * more than one unique URL to download
             *
             * restarting activity after the service has finished its job
             * will still cause the file to be downloaded once again
             **/

            Log.d(TAG, "Download started");
            Intent intent = new Intent(this, DownloadService.class);
            intent.setAction(DownloadService.ACTION_DOWNLOAD);
            intent.putExtra(DownloadService.PARAM_URL, CITIES_GZ_URL);
            startService(intent);
        }
        else {
            Log.d(TAG, "Running service found, waiting for it to finish the job");
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateView(intent);
            }
        };

        IntentFilter filter = new IntentFilter(DownloadService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateView(Intent intent) {
        if (intent != null) {
            final int titleId = intent.getIntExtra(DownloadService.PARAM_TITLE, 0);
            final int progress = intent.getIntExtra(DownloadService.PARAM_PROGRESS, 0);
            titleTextView.setText(titleId);
            progressBarView.setProgress(progress);

            Log.d(TAG, "Progress updated: " + progress + "%");
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TITLE_TEXT, titleTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String s = savedInstanceState.getString(TITLE_TEXT);
            titleTextView.setText(s);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy happened");
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
        super.onDestroy();
    }

}