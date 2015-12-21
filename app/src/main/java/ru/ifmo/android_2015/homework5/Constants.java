package ru.ifmo.android_2015.homework5;

/**
 * @author creed
 * @date 21.12.15
 */
public class Constants {
    public static final String TAG = "InitSplash";

    // Урл для скачивания файла с данными, нужными для инициализации приложения при первом запуске.
    // GZIP-архив, содержащий список городов в формате JSON.
    public static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";
    public static final int PROGRESS_MAX_VALUE = 100;
    public static final boolean NEED_TO_FAIL = true;
    public static final int FAIL_AFTER_X_PERCENTS = 10;

    public interface MESSAGES {
        int PROGRESS = 1;
        int ERROR = 2;
        int DONE = 3;
    }
}
