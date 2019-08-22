package br.com.fatec.icpmanager.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notification_id = intent.getIntExtra("NOTIFICATION_ID", -1);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notification_id);
    }
}