package com.depart.cea.test1;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private String TAG = "MyReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
                Toast.makeText(context, "ACTION_SCREEN_OFF！！！！！！！！！！！", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onReceive: ACTION_SCREEN_OFF");
                KeepLiveActivity.startKeepAlive();
                break;
            case Intent.ACTION_SCREEN_ON:
                Toast.makeText(context, "ACTION_SCREEN_ON！！！！！！！！！！！", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onReceive: ACTION_SCREEN_ON");
                KeepLiveActivity.killKeepAlive();
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                Log.i(TAG, "onReceive: ACTION_BOOT_COMPLETED");
                Toast.makeText(context, "开机自启成功！！！！！！！！！！！", Toast.LENGTH_LONG).show();
                Notification.Builder mBuilder =
                        new Notification.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("My notification")
                                .setContentText("开机自启成功");
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
//                context.startService(new Intent(context, CollectionService.class));
                break;
        }
    }
}
