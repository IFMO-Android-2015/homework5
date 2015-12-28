package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by ruslanthakohov on 23/12/15.
 */
public class DownloadService extends IntentService implements ProgressCallback {
    public static final String URL_EXTRA = "url";
    public static final String ERROR_FLAG_EXTRA = "errorFlag";
    public static final String SUCCESS_FLAG_EXTRA = "successFlag";
    public static final String PROGRESS_EXTRA = "progress";
    public static final String REPORT_PROGRESS_ACTION = "reportProgress";

    public static final String SERVICE_NAME = DownloadService.class.getSimpleName();

    public DownloadService() {
        super(SERVICE_NAME);

        Log.d(SERVICE_NAME, "Service created");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(SERVICE_NAME, "Task received");

        String downloadURL = workIntent.getStringExtra(URL_EXTRA);

        Intent resultIntent = new Intent(REPORT_PROGRESS_ACTION);
        try {
            File destFile = FileUtils.createTempExternalFile(this, "gz");
            DownloadUtils.downloadFile(downloadURL, destFile, this);
            resultIntent.putExtra(SUCCESS_FLAG_EXTRA, true);
        } catch (IOException e) {
            Log.e(SERVICE_NAME, "Failed to download file: " + e.toString());
            resultIntent.putExtra(ERROR_FLAG_EXTRA, true);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onProgressChanged(int downloadProgress) {
        Log.d(SERVICE_NAME, "Progress: " + String.valueOf(downloadProgress));

        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(REPORT_PROGRESS_ACTION)
                        .putExtra(PROGRESS_EXTRA, downloadProgress));
    }
}
