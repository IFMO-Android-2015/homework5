package ru.ifmo.android_2015.homework5;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZeRoGerc on 27.12.15.
 */
public class DownloadService extends IntentService {
    public static final String PROGRESS = "PROGRESS";
    public static final String ACTION_PROGRESS = "ACTION_PROGRESS";
    public static final String ACTION_FINISH = "ACTION_FINISH";
    public static final String ACTION_ERROR = "ACTION_ERROR";
    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";

    private static final String TAG = "SERVICE";

    public DownloadService() {
        super("Download Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String downloadUrl = intent.getStringExtra(DOWNLOAD_URL);
            File destFile = FileUtils.createTempExternalFile(getApplicationContext(), "gz");

            downloadFile(downloadUrl, destFile);
        } catch (Exception e) {
            errorOccurred();
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    private void progressChanged(int progress) {
        Intent toSend = new Intent(ACTION_PROGRESS);
        toSend.putExtra(PROGRESS, progress);
        getApplicationContext().sendBroadcast(toSend);
    }

    private void downloadFinished() {
        Intent toSend = new Intent(ACTION_FINISH);
        getApplicationContext().sendBroadcast(toSend);
    }

    private void errorOccurred() {
        Intent toSend = new Intent(ACTION_ERROR);
        getApplicationContext().sendBroadcast(toSend);
    }

    private void downloadFile(String downloadUrl,
                             File destFile) throws IOException {
        Log.d(TAG, "Start downloading url: " + downloadUrl);
        Log.d(TAG, "Saving to file: " + destFile);

        // Выполняем запрос по указанному урлу. Поскольку мы используем только http:// или https://
        // урлы для скачивания, мы привести результат к HttpURLConnection. В случае урла с другой
        // схемой, будет ошибка.
        HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
        InputStream in = null;
        OutputStream out = null;

        try {

            // Проверяем HTTP код ответа. Ожидаем только ответ 200 (ОК).
            // Остальные коды считаем ошибкой.
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Received HTTP response code: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new FileNotFoundException("Unexpected HTTP response: " + responseCode
                        + ", " + conn.getResponseMessage());
            }

            // Узнаем размер файла, который мы собираемся скачать
            // (приходит в ответе в HTTP заголовке Content-Length)
            int contentLength = conn.getContentLength();
            Log.d(TAG, "Content Length: " + contentLength);

            // Создаем временный буффер для I/O операций размером 128кб
            byte [] buffer = new byte[1024 * 128];

            // Размер полученной порции в байтах
            int receivedBytes;
            // Сколько байт всего получили (и записали).
            int receivedLength = 0;
            // прогресс скачивания от 0 до 100
            int progress = 0;

            // Начинаем читать ответ
            in = conn.getInputStream();
            // И открываем файл для записи
            out = new FileOutputStream(destFile);

            // В цикле читаем данные порциями в буффер, и из буффера пишем в файл.
            // Заканчиваем по признаку конца файла -- in.read(buffer) возвращает -1
            while ((receivedBytes = in.read(buffer)) >= 0) {
                out.write(buffer, 0, receivedBytes);
                receivedLength += receivedBytes;

                if (contentLength > 0) {
                    int newProgress = 100 * receivedLength / contentLength;
                    if (newProgress > progress) {
                        Log.d(TAG, "Downloaded " + newProgress + "% of " + contentLength + " bytes");
                        progressChanged(newProgress);

                    }
                    progress = newProgress;
                }
            }

            if (receivedLength != contentLength) {
                Log.w(TAG, "Received " + receivedLength + " bytes, but expected " + contentLength);
                errorOccurred();
            } else {
                Log.d(TAG, "Received " + receivedLength + " bytes");
                downloadFinished();
            }

        } finally {
            // Закрываем все потоки и соедиениние
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close HTTP input stream: " + e, e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close file: " + e, e);
                }
            }
            conn.disconnect();
        }
    }
}
