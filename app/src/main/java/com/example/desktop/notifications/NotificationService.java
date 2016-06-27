package com.example.desktop.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    public BluetoothAdapter mBluetoothAdapter;
    public BroadcastReceiver mReceiver;
    public BluetoothDevice device;
    public Notification notification;
    public NotificationManager notificationManager;
    public Context context;
    public Notification.Builder builder;
    public MediaRecorder mediaRecorder;
    public File file;

    public boolean receiverRegistered;
    public static final int ACT_CONNECTED = 1;
    public static final int ACT_DISCONNECTED = 2;
    public int notificationNumber;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("onCreate", "Start");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.v("Receiver", action);

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    Log.v("Receiver", "ACTION_ACL_CONNECTED " + device.getName() + "," + device.getAddress() + "\n");
                    sendNotificationBT(device.getName(), ACT_CONNECTED);
                    startRecording();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    Log.v("Receiver", "ACTION_ACL_DISCONNECTED " + device.getName() + "," + device.getAddress() + "\n");
                    sendNotificationBT(device.getName(), ACT_DISCONNECTED);
                    startRecording();
//                } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
//                    startRecording();
                }
            }
        };

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter filter3 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        IntentFilter filter4 = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);

        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);
        this.registerReceiver(mReceiver, filter4);

        receiverRegistered = true;

        Log.v("onCreate", "Registering filters");

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

    public void onDestroy() {
        try {
            if (receiverRegistered) {
                this.unregisterReceiver(mReceiver);
                receiverRegistered = false;
            }

            Log.v("**NotificationService**", "onDestroy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(String action, Integer actionCode, File file) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        context = getApplicationContext();

        if (actionCode == 0) {
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file://" + file.getAbsolutePath());
            notificationIntent.setDataAndType(uri, "audio/*");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder = new Notification.Builder(context)
                    .setContentTitle(formattedDate)
                    .setContentText(file.getAbsolutePath())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent);
        } else {
            builder = new Notification.Builder(context)
                    .setContentTitle(formattedDate)
                    .setContentText(action)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification);
        }

        notification = builder.build();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationNumber = generateRandom();
        notificationManager.notify(notificationNumber, notification);
    }

    public void sendNotificationBT(String deviceName, int actionDone) {

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

        context = getApplicationContext();
        builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification);

        notification = builder.build();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationNumber = generateRandom();
        notificationManager.notify(notificationNumber, notification);
    }

    public void startRecording() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                Log.v("startRecording", "Stopping voice recording");
                sendNotification("Stopping voice recording ...", 0, file);
            }
        }, 10000);

        try {
            file = new File(Environment.getExternalStorageDirectory(),
                    "" + new Random().nextInt(50) + ".3gp");

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder
                    .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder
                    .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(file.getAbsolutePath());

            Log.v("startRecording", "Starting voice recording");

            mediaRecorder.prepare();
            mediaRecorder.start();
            sendNotification("Starting voice recording ...", 1, file);

        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}
