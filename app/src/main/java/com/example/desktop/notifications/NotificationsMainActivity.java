package com.example.desktop.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class NotificationsMainActivity extends AppCompatActivity {

    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_main);

    }

    public void startNotificationService(View view){
        intent = new Intent(this, NotificationService.class);
        startService(intent);
    }

    public void stopNotificationService(View view){
        if (intent != null) {
            stopService(intent);
        }
    }
}
