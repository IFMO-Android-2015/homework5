package ru.ifmo.android_2015.homework5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import ru.ifmo.android_2015.homework5.model.City;
import ru.ifmo.android_2015.homework5.webcams.Webcams;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {
    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";
    private City city;
    private ImageView camImageView;
    private ProgressBar progressView;
    private Cam thisCam = null;
    private Bitmap camImg = null;
    private TextView textView;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);
        textView = (TextView)findViewById(R.id.textViewCamName);

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);
        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.

        if (savedInstanceState == null) {
            camImageView.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.VISIBLE);
            startService(new Intent(this, DownloadService.class).putExtra("city", city));
        }

        if (camImg != null) {
            camImageView.setImageBitmap(camImg);
            progressView.setVisibility(View.INVISIBLE);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case DownloadService.INFO:
                        textView.setText(intent.getStringExtra(DownloadService.DATA));
                        break;
                    case DownloadService.IMAGE:
                        progressView.setVisibility(View.INVISIBLE);
                        camImageView.setVisibility(View.VISIBLE);
                        camImg = intent.getParcelableExtra(DownloadService.DATA);
                        camImageView.setImageBitmap(camImg);
                        break;

                    case DownloadService.ERROR:
                        textView.setText(R.string.download_error);
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.INFO);
        intentFilter.addAction(DownloadService.IMAGE);
        intentFilter.addAction(DownloadService.ERROR);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("img", camImg);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        camImg = savedInstanceState.getParcelable("img");
        if (camImg != null) {
            camImageView.setImageBitmap(camImg);
            camImageView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);
        } else {
            camImageView.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.VISIBLE);
            startService(new Intent(this, DownloadService.class).putExtra("city", city));
        }
    }

    private static final String TAG = "CityCam";
}
