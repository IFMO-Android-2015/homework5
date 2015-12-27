package ru.ifmo.android_2015.citycam;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import ru.ifmo.android_2015.citycam.model.City;


public class WebCamScope extends BroadcastReceiver {

    enum State {
        STARTED("Начало загрузки"),
        LOAD_INFO("Загрузка инофрмации о камере..."),
        LOAD_VIEW("Загрузка изображения с камеры..."),
        NO_CAMERAS("Нет камер по-близости."),
        ERROR("Ошибка загрузки."),
        FINISHED("Загрузка завершена.");

        private String info;

        public String getInfo() {
            return info;
        }

        State(String info) {
            this.info = info;
        }
    }

    CityCamActivity activity;

    WebCam cam;

    State state;

    void updateView() {
        switch (state) {
            case STARTED :
            case LOAD_INFO :
            case LOAD_VIEW :
                activity.progressView.setVisibility(View.VISIBLE);
                break;
            case ERROR :
            case NO_CAMERAS :
                activity.progressView.setVisibility(View.INVISIBLE);
                break;
            case FINISHED :
                activity.progressView.setVisibility(View.INVISIBLE);
                activity.ratingBar.setVisibility(View.VISIBLE);
                activity.camImageView.setVisibility(View.VISIBLE);
                activity.titleView.setVisibility(View.VISIBLE);

                activity.ratingBar.setRating((float) cam.getRate());
                activity.titleView.setText(cam.getTitle());
                activity.camImageView.setImageBitmap(cam.getBitmap());
        }
    }

    void updateState(State state) {
        activity.loadStateView.setText(state.getInfo());
        this.state = state;

        updateView();
    }

    public void attachActivity(CityCamActivity activity) {
        this.activity = activity;
        updateView();
    }

    public WebCamScope(CityCamActivity activity, City city) {
        this.activity = activity;
        updateState(State.STARTED);
        activity.startService(new Intent(activity, WebCamLoadService.class).putExtra(CityCamActivity.EXTRA_CITY, city));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        State state = State.valueOf(intent.getStringExtra(WebCamLoadService.KEY));

        if (state == State.FINISHED) {
            cam = intent.getParcelableExtra(WebCamLoadService.WEBCAM);
        }

        updateState(state);
    }
}