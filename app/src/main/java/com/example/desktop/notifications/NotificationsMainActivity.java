package com.example.desktop.notifications;

import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;

public class NotificationsMainActivity extends AppCompatActivity {

    public NotificationManager notificationManager;
    public NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_main);

        mBuilder = new NotificationCompat.Builder(this);
    }

    public void SendNotification(View v){
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setContentTitle("Notification Alert, Click Me!");
        mBuilder.setContentText("Hi, This is Android Notification Detail!");

        int mNotificationId = 1;

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, mBuilder.build());
    }
}
