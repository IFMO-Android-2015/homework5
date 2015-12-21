package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


/**
 * Created by sofya on 21.12.15.
 */
public class MyService extends IntentService {


    public MyService() {
        super("name");
    }

    public MyService(String name) {
        super(name);
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int downloading;
        String url = intent.getStringExtra("url");
        try {
            InitSplashActivity.downloadFile(getApplicationContext(), new ProgressCallback() {
                @Override
                public void onProgressChanged(int progress) {
                    Intent intentUpdate = new Intent();
                    intentUpdate.setAction("ru.ifmo.android_2015.homework5.UPDATE");
                    intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                    intentUpdate.putExtra("Update", progress);
                    sendBroadcast(intentUpdate);
                }
            }, url);
            downloading = 11;
        } catch (Exception e) {
            downloading = 10;
            Log.w("Here", "Error");
        }

        Intent intentResponse = new Intent();
        intentResponse.setAction("ru.ifmo.android_2015.homework5.RESPONSE");
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra("Out", downloading);
        sendBroadcast(intentResponse);
    }



    public void onDestroy() {
        super.onDestroy();
    }

}

