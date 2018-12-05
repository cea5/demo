package com.depart.cea.test1;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.depart.cea.test1.net.CollectionService;
import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.push.handler.DeleteTokenHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                IgnoringBatteryOptimizations(this);
            }
            startService(new Intent(this, CollectionService.class));
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 5000, PendingIntent.getService(this, 1, new Intent(this, CollectionService.class),PendingIntent.FLAG_CANCEL_CURRENT));
//            }
        }
        //创建通知栏通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "Build.VERSION.SDK_INT: "+Build.VERSION.SDK_INT);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("id", "name", importance);
            channel.setDescription("description");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }else {
            Log.e(TAG, "Build.VERSION.SDK_INT: "+Build.VERSION.SDK_INT);
        }


        HMSAgent.connect(this, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                Log.d(TAG, "HMS connect end:" + rst);
//                deleteToken();
                getToken();
            }
        });

//        HMSAgent.Push.enableReceiveNormalMsg(true, new EnableReceiveNormalMsgHandler() {
//            @Override
//            public void onResult(int rst) {
//            }
//        });

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean IgnoringBatteryOptimizations(Activity activity) {
        String packageName = activity.getPackageName();
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            return true;
        } else {
            try {
                Intent intent = new Intent();
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    // / intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivityForResult(intent, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == 1) {
                IgnoringBatteryOptimizations(this);
            }
        }
    }
    /**
     * 获取token
     */
    private void getToken() {
        Log.d(TAG, "get token: begin");
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rst) {
                Log.d(TAG, "result code: "+rst);
            }
        });
    }

    /**
     * 删除token | delete push token
     */
    private void deleteToken(){
        Log.d(TAG, "deleteToken:begin");
        HMSAgent.Push.deleteToken("907135000", new DeleteTokenHandler() {
            @Override
            public void onResult(int rst) {
                Log.d(TAG, "deleteToken:end code=" + rst);
            }
        });
    }

}
