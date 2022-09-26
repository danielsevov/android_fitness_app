package com.personal.lifttoliveapp.API;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.personal.lifttoliveapp.R;
import com.personal.lifttoliveapp.misc.Helper;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String TAG = "PushNotificationService";
    private int notificationId = 0;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        //now we will have the token
        Task<String> tok = FirebaseMessaging.getInstance().getToken();

        //for now we are displaying the token in the log
        Log.d("MyRefreshedToken", tok.getResult());
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = message.getNotification().getTitle();
        String body = message.getNotification().getBody();

        final String CHANNEL_ID = message.getFrom();
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Heads Up Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Helper.reqCode++, notification.build());

        super.onMessageReceived(message);
    }
}
