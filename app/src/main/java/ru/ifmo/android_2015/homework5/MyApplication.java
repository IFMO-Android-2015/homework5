package ru.ifmo.android_2015.homework5;

import android.app.Application;

/**
 * @author creed
 * @date 13.01.16
 */
public class MyApplication extends Application {

    public static final String TAG = "ServiceDownload";

    public interface ACTIONS {
        String DOWNLOAD = "ru.ifmo.android_2015.homework5.action.DOWNLOAD";
        String PUBLISH_PROGRESS = "ru.ifmo.android_2015.homework5.action.PUBLISH_PROGRESS";
    }
    public interface EXTRA_PARAMS {
        String PROGRESS = "progress";
    }

    /**
     * Состояние загрузки в DownloadFileTask
     */
    public enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        // ID строкового ресурса для заголовка окна прогресса
        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

    private static DownloadState state;
    private static int progress = 0;

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static DownloadState getState() {
        return state;
    }

    public static void setState(DownloadState state) {
        MyApplication.state = state;
    }

    public static int getProgress() {
        return progress;
    }

    public static void setProgress(int progress) {
        MyApplication.progress = progress;
    }
}
