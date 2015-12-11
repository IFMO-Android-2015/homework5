package ru.ifmo.android_2015.homework5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

/**
 * Local broadcast receiver that can attach to an activity <br>
 * Holds last received message
 */
public abstract class AttachableReceiver extends BroadcastReceiver {
    private WeakReference<Context> context;// For not making method detach and avoiding memory leaks
    private Intent lastMessage;
    private boolean stopped;

    public abstract void onMessageReceive(Context context, Intent message);

    /**
     * @param context initial context
     * @param filter  current filter
     */
    public AttachableReceiver(Context context, String filter) {
        super();
        this.context = new WeakReference<>(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(filter);
        lastMessage = null;
        LocalBroadcastManager.getInstance(context).registerReceiver(this, intentFilter);
        stopped = false;
    }

    /**
     * Attaches receiver to the context. If receiver holds message inside, onMessageReceive
     * will be called
     *
     * @param context Current context
     */
    public void attach(Context context) {
        this.context = new WeakReference<>(context);
        if (lastMessage != null) {
            onMessageReceive(context, lastMessage);
            lastMessage = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (stopped) {
            return;
        }
        if (this.context == null) {
            lastMessage = intent;
        } else {
            onMessageReceive(this.context.get(), intent);
        }
    }

    /**
     * Unregisters receiver. Should be called at least once
     */
    public void stop(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        stopped = true;
    }
}
