package ru.ifmo.android_2015.homework5;

/**
* Created by Dan) on 23.12.2015.
*/

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.InputStreamReader;
import java.net.URL;

import ru.ifmo.android_2015.homework5.model.City;
import ru.ifmo.android_2015.homework5.webcams.Webcams;

public class DownloadService extends IntentService {
    private static final String NAME = "download-service";
    public static final String INFO = "ru.ifmo.android_2015.homework5.INFO";
    public static final String IMAGE = "ru.ifmo.android_2015.homework5.IMAGE";
    public static final String ERROR = "ru.ifmo.android_2015.homework5.ERROR";
    public static final String DATA = "ru.ifmo.android_2015.homework5.DATA";

    public DownloadService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        City city = intent.getExtras().getParcelable("city");
        String webcamName = null;
        URL previewUrl = null;
        Bitmap bitmap = null;
        JsonReader reader = null;

        // No JSONObjects were harmed in the writing of this code
        try {
            // Download camera info
            Log.d(TAG, "Downloading JSON...");
            URL jsonUrl = Webcams.createNearbyUrl(city.latitude, city.longitude);
            reader = new JsonReader(new InputStreamReader(jsonUrl.openStream()));
            Cam result = new Cam();
            reader.beginObject();
            String status;
            while (reader.hasNext()) {
                String field = reader.nextName();
                if (field.equals("status") && (reader.peek() != JsonToken.NULL)) {
                    status = reader.nextString();
                } else if (field.equals("webcams")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        field = reader.nextName();
                        if (field.equals("webcam")) {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    String camField = reader.nextName();
                                    if (camField.equals("title") && (reader.peek() != JsonToken.NULL)) {
                                        result.title = reader.nextString();
                                    } else if (camField.equals("preview_url") && (reader.peek() != JsonToken.NULL)) {
                                        result.preview_url = reader.nextString();
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            }
                            reader.endArray();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            Log.d(TAG, "JSON read");
            Log.d(TAG, "Camera name: " + result.title);
            Log.d(TAG, "Preview URL: " + result.preview_url);
            previewUrl = new URL(result.preview_url);

            Intent infoIntent = new Intent(INFO);
            infoIntent.putExtra(DATA, result.title);
            LocalBroadcastManager.getInstance(this).sendBroadcast(infoIntent);
            bitmap = BitmapFactory.decodeStream(previewUrl.openStream());
            if (bitmap == null) {
                throw new Exception();
            }
            Intent imageIntent = new Intent(IMAGE);
            imageIntent.putExtra(DATA, bitmap);
            LocalBroadcastManager.getInstance(this).sendBroadcast(imageIntent);
        } catch (Exception ignored) {
            Log.d(TAG, "Download unsuccessful");
            Intent errorIntent = new Intent(ERROR);
            LocalBroadcastManager.getInstance(this).sendBroadcast(errorIntent);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {

                }
            }
        }
    }
    private static final String TAG = "DownloadService";
}