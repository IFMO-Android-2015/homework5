package ru.ifmo.android_2015.homework5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * �����, ����������� ������������� ��� ������ ������� ����������. � �������� �������������
 * ����������� ���� � �������, ������� ��� ������ ����������. ���� ���� �������������, ������������
 * �����-����� � ����������� ���������.
 */
public class InitSplashActivity extends Activity {

    // ��� ��� ���������� ����� � �������, ������� ��� ������������� ���������� ��� ������ �������.
    // GZIP-�����, ���������� ������ ������� � ������� JSON.
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";
    // ��������� ���������
    private ProgressBar progressBarView;
    // ���������
    private TextView titleTextView;
    private DownloadState downloadState;
    private BroadcastReceiver broadcastReceiver;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        progressBarView.setMax(100);

        if (savedInstanceState == null) {
            downloadState = DownloadState.DOWNLOADING;
            startService(new Intent(this, DownloadService.class));
        }
        if (downloadState == DownloadState.DOWNLOADING) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    downloadState = (DownloadState) intent.getSerializableExtra("state");
                    if (downloadState != DownloadState.DOWNLOADING) {
                        unregisterReceiver(broadcastReceiver);
                    }
                    update(intent.getIntExtra("progress", 0), downloadState);
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter("service"));
        }
    }

    void update(int progress, DownloadState downloadState) {
        titleTextView.setText(downloadState.titleResId);
        progressBarView.setProgress(progress);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("state", downloadState);
        outState.putInt("progress", progressBarView.getProgress());
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        // ���� ����� ���������� ��� ����� ������������, ����� ������� ������
        // Activity ������������. ������, ������� �� ������, �� ����� ���������,
        // � ��� ����� ����� ������������ � ����� ������� Activity
        return broadcastReceiver;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * ��������� �������� � DownloadFileTask
     */
    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        // ID ���������� ������� ��� ��������� ���� ���������
        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }


    /**
     * ��������� ������ ������� �� ��������� ����.
     */
    static void downloadFile(Context context,
                             ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(context, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}