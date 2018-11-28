package com.depart.cea.test1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private String TAG = "MyService";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        Notification.Builder localBuilder = new Notification.Builder(this);
        localBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        localBuilder.setAutoCancel(false);
        localBuilder.setTicker("Foreground Service Start");
        localBuilder.setContentTitle("Service");
        localBuilder.setContentText("正在运行...");
        startForeground(1, localBuilder.getNotification());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new MyReceiver(), filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        //这里是定时的,这里设置的是每隔两秒打印一次时间=-=,自己改
//        int anHour = 5 * 1000;
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        Intent i = new Intent(this,MyReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        if (manager != null) {
//            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        }else {
//            Log.e(TAG, "onStartCommand: manager = null");
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLowMemory() {
        Log.e(TAG, "onLowMemory: ");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Log.e(TAG, "onTrimMemory: ");
        super.onTrimMemory(level);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "onTaskRemoved: ");
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        //这里是定时的,这里设置的是每隔两秒打印一次时间=-=,自己改
//        int anHour = 0;
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        Intent i = new Intent(this,MyReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        if (manager != null) {
//            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        }else {
//            Log.e(TAG, "onStartCommand: manager = null");
//        }
        super.onTaskRemoved(rootIntent);
    }
}
