package com.example.desktop.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("**BootReciever**", "Service Stops");
        context.startService(new Intent(context, NotificationService.class));
    }
}
