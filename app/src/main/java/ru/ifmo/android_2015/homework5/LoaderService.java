package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import ru.ifmo.android_2015.homework5.utils.DownloadUtils;
import ru.ifmo.android_2015.homework5.utils.FileUtils;
import ru.ifmo.android_2015.homework5.utils.ProgressCallback;

/**
 * @author creed
 * @date 21.12.15
 */
public class LoaderService extends IntentService implements ProgressCallback {

    public static final String TAG = LoaderService.class.getSimpleName();

    private Messenger messenger = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public LoaderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(Constants.TAG, TAG+":onHandleIntent() - start loading...");

        initMessenger(intent);

        try {
            downloadFile();
            notifyClient(Message.obtain(null, Constants.MESSAGES.DONE));
        } catch (IOException e) {
            Log.e(Constants.TAG, TAG+":onHandleIntent() - error downloading file: " + e.getMessage());
            notifyClient(Message.obtain(null, Constants.MESSAGES.ERROR));
        }

        /* release link to the messenger */
        messenger = null;
    }

    @Override
    public void onProgressChanged(int progress) {
        Log.i(Constants.TAG, TAG + ":onProgressChanged() - progress="+progress);
        notifyClient(Message.obtain(null, Constants.MESSAGES.PROGRESS, progress));
    }

    private void initMessenger(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            messenger = (Messenger) bundle.get("messenger");
        }
    }

    private void notifyClient(Message message) {
        if (messenger == null) {
            Log.e(Constants.TAG, TAG+":notifyClient() - messenger null");
            return;
        }
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            Log.e(Constants.TAG, TAG+":notifyClient() - error while sending");
        }
    }

    /**
     * Скачивает список городов во временный файл.
     */
    private void downloadFile() throws IOException {
        File destFile = FileUtils.createTempExternalFile(this, "gz");
        DownloadUtils.downloadFile(Constants.CITIES_GZ_URL, destFile, this);
    }
}
