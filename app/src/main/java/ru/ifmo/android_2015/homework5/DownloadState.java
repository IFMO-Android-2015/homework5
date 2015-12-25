package ru.ifmo.android_2015.homework5;

/**
 * Created by Anstanasia on 25.12.2015.
 */

enum DownloadState {
    DOWNLOADING(R.string.downloading),
    DONE(R.string.done),
    ERROR(R.string.error);

    // ID строкового ресурса для заголовка окна прогресса
    final int titleResId;

    DownloadState(int titleResId) {
        this.titleResId = titleResId;
    }
}