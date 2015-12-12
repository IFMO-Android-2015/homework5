package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownLoad_Service extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private  static  final  String TAG="DownloadService";
    public  static  final String PENDING="Pending";
    public static final String DOWNLOAD="Download";
    public static final int Done=0;
    public static final int Error=-1;
    public static final int InProgress=1;

    public DownLoad_Service() {
        super("DownLoad_Service");
    }

    public DownLoad_Service(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        final PendingIntent pendingIntent=intent.getParcelableExtra(PENDING);
        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    try {
                        pendingIntent.send(
                                getApplicationContext(),
                                (progress == 100) ? DownLoad_Service.Done : DownLoad_Service.InProgress,
                                new Intent().putExtra(DOWNLOAD,
                                        progress
                                )
                        );
                    } catch (PendingIntent.CanceledException e) {
                        Log.e(TAG, "Cant reply!");
                    }
                }
            });
        }
        catch (IOException e)
        {
            try {
                pendingIntent.send(getApplicationContext(),DownLoad_Service.Error,new Intent());
            }
            catch (PendingIntent.CanceledException e1)
            {
                Log.e(TAG,"can't reply!");
            }
        }
    }
}
