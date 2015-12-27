package ru.ifmo.android_2015.citycam;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.ifmo.android_2015.citycam.model.City;
import ru.ifmo.android_2015.citycam.webcams.Webcams;

public class WebCamLoadService extends IntentService {

    private static final String TAG = "WebCamLoadService";

    public static final String MSG = "WebCamLoadService.MSG";
    public static final String KEY = "WebCamLoadService.KEY";

    public static final String LOAD_INFO  = WebCamScope.State.LOAD_INFO.name();
    public static final String LOAD_VIEW  = WebCamScope.State.LOAD_VIEW.name();
    public static final String NO_CAMERAS = WebCamScope.State.NO_CAMERAS.name();
    public static final String ERROR      = WebCamScope.State.ERROR.name();
    public static final String FINISHED   = WebCamScope.State.FINISHED.name();


    public static final String WEBCAM = "WebCamLoadService.WEBCAM";

    public static final String NAME = "WebCamLoadService.NAME";

    public WebCamLoadService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        City city = intent.getExtras().getParcelable(CityCamActivity.EXTRA_CITY);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MSG).putExtra(KEY, LOAD_INFO));

        HttpURLConnection connection = null;
        InputStream in = null;
        JsonReader reader;

        String url_str = null, title = null;
        double rate = 0;

        try {
            URL url = Webcams.createNearbyUrl(city.latitude, city.longitude);
            connection = (HttpURLConnection) url.openConnection();
            in = connection.getInputStream();
            reader = new JsonReader(new InputStreamReader(in));

            reader.beginObject();
            while (!reader.nextName().equals("webcams"))
                reader.skipValue();                             //skip until "webcams"
            reader.beginObject();                               //enter to "webcams"
            while (!reader.nextName().equals("webcam"))
                reader.skipValue();                             //skip until first "webcam"
            reader.beginArray();
            if (!reader.hasNext()) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MSG).putExtra(KEY, NO_CAMERAS));
                return ;
            }
            reader.beginObject();                               //enter to "webcam"

            while(reader.hasNext()) {
                switch(reader.nextName()) {
                    case "preview_url" :
                        url_str = reader.nextString();
                        break;
                    case "title" :
                        title  = reader.nextString();
                        break;
                    case "rating_avg" :
                        rate = reader.nextDouble();
                        break;
                    default:
                        reader.skipValue();
                }
            }

            reader.close();

        } catch (IOException e) {
            Log.e(TAG, "Error while loading WebCamera information", e);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MSG).putExtra(KEY, ERROR));
        } finally {
            // closing files if it wasn't
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.d(TAG,"Can't close " + in.toString(), e);
            }
            if (connection != null)
                connection.disconnect();
        }

        if( url_str == null)
            return;

        Bitmap bitmap = null;

        try {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MSG).putExtra(KEY, LOAD_VIEW));
            connection = (HttpURLConnection) new URL(url_str).openConnection();
            in = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MSG).putExtra(KEY, ERROR));
            Log.e(TAG, "Error while loading Bitmap camera view", e);
        } finally {
            // closing files if it wasn't
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.d(TAG ,"Can't close " + in.toString(), e);
            }
            if (connection != null)
                connection.disconnect();
        }

        if(bitmap == null) {
            return ;
        }
        WebCam webCam = new WebCam(bitmap, rate, title);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MSG).putExtra(KEY, FINISHED).putExtra(WEBCAM, webCam));
    }
}
