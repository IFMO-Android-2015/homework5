package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class InitSplashActivity extends Activity {

    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";
    public static boolean Working = false;

    private ProgressBar progressBarView;

    private TextView title;
    private static final String TAG = "InitSplash";
    private BroadcastReceiver receiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "WE CREATE PERFECT EVIL");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        title = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        if(savedInstanceState != null) {
            Log.d(TAG, "NOW WE RETURN TO PUNISH");
        } else {
            if(!Working) {
                Log.d(TAG, "WE BEGIN OUR EVIL PLANS");
                Intent intent = new Intent(this, DownloadService.class);
                intent.setAction(DownloadService.DOWNLOAD);
                intent.putExtra(DownloadService.URL, CITIES_GZ_URL);
                startService(intent);
            } else {
                Log.d(TAG, "EVIL IS WORKING FOR YOU PLEASURE, MY LORD");
            }
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    //здесь происходит апдейт
                    final int titleId = intent.getIntExtra(DownloadService.TITLE, 0);
                    final int progress = intent.getIntExtra(DownloadService.PROGRESS, 0);
                    title.setText(titleId);
                    progressBarView.setProgress(progress);

                    Log.d(TAG, "WORLD WILL BE CONQURED AFTER 100%, BUT NOW WE HAVE : " + progress + "%");
                }
            }
        };


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(DownloadService.BROADCAST));
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return receiver;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "DOOMSDAY");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }
}
