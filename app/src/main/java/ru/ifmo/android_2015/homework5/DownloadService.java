package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DownloadService extends IntentService implements ProgressCallback {

    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null && action.equals(MyApplication.ACTIONS.DOWNLOAD)) {
                try {
                    Log.e(MyApplication.TAG, "onHandleIntent() - start downloading...");
                    MyApplication.setState(MyApplication.DownloadState.DOWNLOADING);
                    downloadFile(this, this);
                    MyApplication.setState(MyApplication.DownloadState.DONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    MyApplication.setState(MyApplication.DownloadState.ERROR);
                }
                notifyClient(MyApplication.getProgress());
            }
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        MyApplication.setProgress(progress);
        notifyClient(progress);
    }

    /**
     * Скачивает список городов во временный файл.
     */
    private void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private void notifyClient(int progress) {
        Log.e(MyApplication.TAG, "notifyClient() - progress = " + progress);
        Intent intent = new Intent(MyApplication.ACTIONS.PUBLISH_PROGRESS);
        intent.putExtra(MyApplication.EXTRA_PARAMS.PROGRESS, progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
