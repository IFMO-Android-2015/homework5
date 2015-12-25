package tk.bioryajenka.homework5;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import tk.bioryajenka.homework5.InitSplashActivity.DownloadState;

import java.io.File;

/**
 * Created by Jackson on 25.12.2015.
 */
public class DownloadService extends Service implements ProgressCallback {
    private Thread thread;

    public static String EXTRA_PROGRESS = "progress";
    public static String EXTRA_STATE = "state";

    private int progress = 0;
    private DownloadState state = DownloadState.ERROR;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null) {
            Log.d(TAG, "start again?");
            startForeground(1, new Notification.Builder(this).
                    setContentTitle("Sure want to stop downloading?!").
                    setSmallIcon(R.mipmap.ic_launcher).build());
        }

        if (thread == null) {
            Log.d(TAG, "starting thread");
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "Starting downloading");
                        File destFile = FileUtils.createTempExternalFile(DownloadService
                                .this, "gz");
                        state = DownloadState.DOWNLOADING;
                        DownloadUtils.downloadFile(InitSplashActivity.CITIES_GZ_URL,
                                destFile, DownloadService.this);
                        state = DownloadState.FINISHED;
                        Log.d(TAG, "fin");
                    } catch (Exception e) {
                        Log.e(TAG, "Error downloading file: " + e, e);
                        state = DownloadState.ERROR;
                    }
                    postResult();
                }
            });
            thread.start();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy service");

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onProgressChanged(int progress) {
        Log.d(TAG, "progress is " + progress);
        this.progress = progress;
        postResult();
    }

    private void postResult() {
        Intent intent = new Intent(InitSplashActivity.RECEIVE_ACTION);
        intent.putExtra(EXTRA_PROGRESS, progress);
        intent.putExtra(EXTRA_STATE, state.getNum());
        sendBroadcast(intent);
    }

    private static final String TAG = "DownloadService.java";
}
