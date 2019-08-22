package br.com.fatec.icpmanager.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.activity.MainActivity;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {

        PreferenceHelper helper = new PreferenceHelper(this);
        if (helper.getNotification().equals("true")) {
            if (message.getData().size() > 0) {
                String uid = message.getData().get("to");
                String title = message.getData().get("title");
                String body = message.getData().get("message");
                String flag = message.getData().get("flag");
                if (uid != null && uid.equals(FirebaseHelper.getCurrentUser().getUid()))
                    sendNotification(title, body);
            } else if (message.getNotification() != null)
                sendNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }
    }

    private void sendNotification(String title, String body) {
        int messageId = (int) (System.currentTimeMillis() / 1000);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("NOTIFICATION", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, messageId,
                intent, PendingIntent.FLAG_ONE_SHOT);

        String channel = getString(R.string.default_notification_channel_id);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_dashboard_white)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel,
                    "CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }

        manager.notify(messageId, notification.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        PreferenceHelper helper = new PreferenceHelper(getBaseContext());
        helper.putMessagingToken(s);
    }
}