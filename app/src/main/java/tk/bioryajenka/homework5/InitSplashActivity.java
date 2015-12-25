package tk.bioryajenka.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Экран, выполняющий инициализацию при первом запуске приложения. В процессе инициализации
 * скачивается файл с данными, нужными для работы приложения. Пока идет инициализация,
 * показывается
 * сплэш-скрин с индикатором прогресса.
 */
public class InitSplashActivity extends Activity {

    // Урл для скачивания файла с данными, нужными для инициализации приложения при первом
    // запуске.
    // GZIP-архив, содержащий список городов в формате JSON.
    public static final String CITIES_GZ_URL = "https://www.dropbox" +
            ".com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";
    public static final String RECEIVE_ACTION = "tk.bioryajenka.homework5.RECEIVE_ACTION";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    private BroadcastReceiver receiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        if (savedInstanceState != null) {
            Log.d(TAG, "sis is not null");
            titleTextView.setText((String) getLastNonConfigurationInstance());
        } else {
            Log.d(TAG, "sis is null");
            titleTextView.setText("0");
            startService(new Intent(this, DownloadService.class));
        }
    }

    public enum DownloadState {
        ERROR(-1), DOWNLOADING(0), FINISHED(1);
        private int num;

        public int getNum() {
            return num;
        }

        public static DownloadState fromNum(int num) {
            for (DownloadState d : values()) {
                if (d.num == num) {
                    return d;
                }
            }
            return null;
        }

        DownloadState(int num) {
            this.num = num;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String res = "Error";

                    int progress = intent.getIntExtra(DownloadService.EXTRA_PROGRESS, 0);
                    int stateNum = intent.getIntExtra(DownloadService.EXTRA_STATE, DownloadState.ERROR.getNum());

                    DownloadState state = DownloadState.fromNum(stateNum);

                    switch (state) {
                        case ERROR:
                            res = "Error";
                            break;
                        case DOWNLOADING:
                            res = "" + progress + "/100";
                            break;
                        case FINISHED:
                            res = "Finished";
                            break;
                    }

                    titleTextView.setText(res);
                }
            };

            IntentFilter intentFilter = new IntentFilter(RECEIVE_ACTION);
            registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        // Этот метод вызывается при смене конфигурации, когда текущий объект
        // Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
        // и его можно будет использовать в новом объекте Activity
        return titleTextView.getText();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private static final String TAG = "InitSplashActivity.java";
}
