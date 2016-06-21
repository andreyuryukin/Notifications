package com.example.desktop.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Timer;
//import java.util.TimerTask;

public class NotificationService extends Service {

    public BluetoothAdapter mBluetoothAdapter;
    public BroadcastReceiver mReceiver;
    public BluetoothDevice device;

    public boolean receiverRegistered;
    public static final int ACT_CONNECTED = 1;
    public static final int ACT_DISCONNECTED = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    Log.v("Receiver", "ACTION_ACL_CONNECTED " + device.getName() + "," + device.getAddress() + "\n");
                    sendNotificationBT(device.getName(), ACT_CONNECTED);
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    Log.v("Receiver", "ACTION_ACL_DISCONNECTED " + device.getName() + "," + device.getAddress() + "\n");
                    sendNotificationBT(device.getName(), ACT_DISCONNECTED);
                }
            }
        };

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        receiverRegistered = true;

//        mTimer = new Timer();
//        mTimer.schedule(timerTask, 10000, 60 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        try {
            Log.v("**NotificationService**", "onStartCommand");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startID);
    }

/*    private Timer mTimer;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.v("**NotificationService**", "Service Running");
            sendNotification();
        }
    };*/

    public void onDestroy() {
        try {
//            mTimer.cancel();
//            timerTask.cancel();

            if (receiverRegistered) {
                this.unregisterReceiver(mReceiver);
                receiverRegistered = false;
            }

            Log.v("**NotificationService**", "onDestroy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public void sendNotification() {

        Notification.Builder builder;

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        Context context = getApplicationContext();
        builder = new Notification.Builder(context)
                .setContentTitle("Timer Notification every 1 Minute")
                .setContentText(formattedDate)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }*/

    public void sendNotificationBT(String deviceName, int actionDone) {

        Notification.Builder builder;
        String title = "Bluetooth Device";

        switch (actionDone) {
            case ACT_CONNECTED:
                title = "Bluetooth Device Connected";
                break;
            case ACT_DISCONNECTED:
                title = "Bluetooth Device Disconnected";
                break;
        }


        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String contentText = df.format(c.getTime()) + " " + deviceName;

        Context context = getApplicationContext();
        builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
