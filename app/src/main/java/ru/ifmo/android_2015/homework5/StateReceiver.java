package ru.ifmo.android_2015.homework5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StateReceiver extends BroadcastReceiver {
    static final String ACTION = "DOWNLOADING";
    public static final String TAG = "StateReceiver";

    public static final String progress = "progress";
    public static final String state = "state";

    @Override
    public void onReceive(Context context, Intent intent) {}
}