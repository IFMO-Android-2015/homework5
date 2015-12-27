package ru.ifmo.android_2015.citycam;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import ru.ifmo.android_2015.citycam.model.City;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    private static final String TAG = "CityCamActivity";

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    ImageView camImageView;
    ProgressBar progressView;
    TextView loadStateView, titleView;
    RatingBar ratingBar;

    private WebCamScope scope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        City city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);
        ratingBar    = (RatingBar) findViewById(R.id.rate_bar);
        loadStateView= (TextView) findViewById(R.id.load_state_view);
        titleView    = (TextView) findViewById(R.id.title_view);

        getSupportActionBar().setTitle(city.name);

        if (savedInstanceState != null) {
            scope = (WebCamScope) getLastCustomNonConfigurationInstance();
        }
        if (scope == null) {
            scope = new WebCamScope(this, city);
        } else {
            scope.attachActivity(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WebCamLoadService.MSG);
        LocalBroadcastManager.getInstance(this).registerReceiver(scope, intentFilter);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return scope;
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(scope);
        super.onDestroy();
    }
}