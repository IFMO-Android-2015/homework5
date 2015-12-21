package ru.ifmo.android_2015.homework5;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * @author creed
 * @date 21.12.15
 */
public class LoaderState {

    private InitSplashActivity initSplashActivity;
    public int progress = 0;
    public DownloadState state = null;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGES.PROGRESS:
                    state = DownloadState.DOWNLOADING;
                    progress = (int) msg.obj;
                    if (initSplashActivity != null) {
                        initSplashActivity.updateView();
                    }
                    break;
                case Constants.MESSAGES.DONE:
                    state = DownloadState.DONE;
                    progress = 100;
                    if (initSplashActivity != null) {
                        initSplashActivity.updateView();
                    }
                    break;
                case Constants.MESSAGES.ERROR:
                    state = DownloadState.ERROR;
                    if (initSplashActivity != null) {
                        initSplashActivity.updateView();
                    }
                    break;
            }
            return true;
        }
    });
    public Messenger messenger = new Messenger(handler);

    private static LoaderState instance = null;

    public LoaderState() {}

    public static LoaderState getInstance() {
        if (instance == null) {
            instance = new LoaderState();
        }
        return instance;
    }

    public void attachActivity(InitSplashActivity activity) {
        Log.d(Constants.TAG, "LoaderState:attachActivity()");
        initSplashActivity = activity;
    }

    public void detachActivity() {
        Log.d(Constants.TAG, "LoaderState:detachActivity()");
        initSplashActivity = null;
    }

    /**
     * Состояние загрузки
     */
    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        // ID строкового ресурса для заголовка окна прогресса
        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

}
