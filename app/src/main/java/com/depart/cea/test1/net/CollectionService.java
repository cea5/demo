package com.depart.cea.test1.net;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.depart.cea.test1.MyReceiver;
import com.depart.cea.test1.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by zhongpeng on 18/3/25.
 */
public class CollectionService extends Service {
    private static String TAG = "CollectionService";

    private GpsLocationListener gListener; // GPS信息监听器
    private int _retSpan = 0;
    private GpsPosition _lastPos; // 最后的定位消息

    public static LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private GpsPosition pos;
//    private PowerManager.WakeLock wakeLock;

    private int num = 0;

    @Override
    public void onCreate() {
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("My notification")
                        .setContentText("定位中...");
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;

        startForeground(1, notification);

        mLocationClient = new LocationClient(getApplication());
        mLocationClient.registerLocationListener(myListener);
//        mLocationClient.enableLocInForeground(1, new Notification());

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setPriority(LocationClientOption.GpsFirst);
        option.setOpenGps(true);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
//        option.disableCache(true);
        option.setScanSpan(60 * 1000);
        mLocationClient.setLocOption(option);


        if (mLocationClient.isStarted()) {
            mLocationClient.restart();
        } else {
            mLocationClient.start();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new MyReceiver(), filter);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            JobScheduler mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), MyJobService.class.getName()));
//            builder.setPeriodic(60 * 1000); //每隔60秒运行一次
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//            builder.setRequiresCharging(false); //是否在充电时执行
//            builder.setPersisted(true); //设置设备重启后，是否重新执行任务
//            builder.setRequiresDeviceIdle(false);//是否在空闲时执行
//            if (mJobScheduler.schedule(builder.build()) <= 0) {
//                Toast.makeText(this, "JobScheduler启动失败", Toast.LENGTH_SHORT).show();
//            }
//        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "CollectionService启动", Toast.LENGTH_SHORT).show();
        return Service.START_REDELIVER_INTENT;
    }


//    private void CheckPosition(Intent intent) {
//        // 判断是否要启动定位
//        int newRetSpan = intent.getExtras().getInt("retspan", 60);// 300 设置的时间
//        if (newRetSpan == 0) {
//            stopPositionListener();
//        } else {
//            if (_retSpan != newRetSpan) {
//                // 开启定位失败,不定位了
//                _retSpan = newRetSpan; // 将传过来的值设为初值
//                if (!startPositionListener(_retSpan)) {
//                    _retSpan = 0;
//                }
//            }
//        }
//    }

    private void CheckSendPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<GpsPosition> posList = new ArrayList<GpsPosition>();
        DBHelper dbh = new DBHelper(getApplication());
        dbh.PositionGetAll(posList);
        dbh.CleanUp();

        String simno = "18058411474";

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephonyManager.getSubscriberId();
        String imei = telephonyManager.getDeviceId();

        if (simno == null || simno.equals("")) {
            return;
        }

//        RingtoneManagerUtil.playRing(CollectionService.this);

        for (GpsPosition position : posList) {
            OkHttpUtils
                    .post()
                    .url("http://183.129.255.154:8085/ssm1/" + "position/PositionInsert")
                    .addParams("simno", simno)
                    .addParams("imsi", imsi)
                    .addParams("imei", imei)
                    .addParams("starnum", position.getStarnum() + "")
                    .addParams("gtime", position.getGtime() + "")
                    .addParams("x", position.getX() + "")
                    .addParams("y", position.getY() + "")
                    .addParams("speed", position.getSpeed() + "")
                    .addParams("radius", position.getRadius() + "")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onBefore(Request request, int id) {
                            super.onBefore(request, id);
                        }

                        @Override
                        public void onAfter(int id) {
                            super.onAfter(id);
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(CollectionService.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            parseSendInfo(response);
                        }
                    });
        }
    }

    private void parseSendInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("code").equals("0")) {
                String gtime = jsonObject.getString("msg");
                if (!gtime.equals("null")) {
                    Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "parseSendInfo: 发送成功");
                    DBHelper dbh = new DBHelper(getApplicationContext());
                    dbh.PositionDelete(Long.parseLong(gtime));
                    dbh.CleanUp();

//                    Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
//                    vibrator.vibrate(4000);
//                    releaseWakeLock();
//                  RingtoneManagerUtil.stopRing();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 启动定位
//    private boolean startPositionListener(int span) {
//        boolean success = false;
//
//        int retSpan = span / 2;
//        if (retSpan == 0) {
//            return false;
//        } else if (retSpan < 30) {
//            retSpan = 30;
//        }
//
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        gListener = new GpsLocationListener();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        }
//        locationManager.
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, retSpan * 1000, 0, gListener);
//        success = true;
//
//        return success;
//    }

    // 停止定位
//    private void stopPositionListener() {
//        if (gListener != null) {
//            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            }
//            locationManager.removeUpdates(gListener);
//            gListener = null;
//        }
//    }

    // GPS监听器
    private class GpsLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                int longitude = (int) (location.getLongitude() * 1000000);
                int latitude = (int) (location.getLatitude() * 1000000);

                if (longitude < 74000000 || longitude > 136000000
                        || latitude < 17000000 || latitude > 54000000) {
                    return;
                }

                if (_lastPos != null) {
                    if (_lastPos.getGtime() == pos.getGtime()) {
                        return;
                    }
                }

                GpsPosition pos = new GpsPosition();
                pos.setStarnum(5);
                pos.setGtime(location.getTime());
                pos.setX((int) (location.getLongitude() * 1000000));
                pos.setY((int) (location.getLatitude() * 1000000));
                pos.setSpeed(location.getSpeed() * 3.6 * 100);
                pos.setRadius(location.getAccuracy());

                if (location.getAccuracy() > 500) {
                    return;
                }

                DBHelper dbh = new DBHelper(CollectionService.this);

                List<GpsPosition> posList = new ArrayList<GpsPosition>();
                dbh.PositionGetAll(posList);

                if (posList.size() > 30) {
                    for (int j = 0; j < posList.size() - 30; j++) {
                        dbh.PositionDelete(posList.get(j).getGtime());
                    }
                }

                dbh.PositionAdd(pos);
                dbh.CleanUp();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            num++;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);
//          CheckPosition(intent);
            Log.i(TAG, "onReceiveLocation: " + num);
            Toast.makeText(CollectionService.this, num + "", Toast.LENGTH_SHORT).show();


//            if (num == 3) {
//                if (isNetworkConnected(CollectionService.this)) {
////                    acquireWakeLock();
//                    CheckSendPosition();
////                    releaseWakeLock();
//                }
//                num = 0;
//            }

            double longString = location.getLongitude();
            double latString = location.getLatitude();

            QCPoint qcPoint = new QCPoint(longString, latString);
            QCPoint pt = MapUtil.decode(qcPoint);

            int star = 2;
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                star = 5;
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                String sources = location.getNetworkLocationType();
                if (sources.equals("wf")) {
                    star = 4;
                } else if (sources.equals("cl")) {

                }
            }

            pos = new GpsPosition();
            pos.setStarnum(star);
            pos.setX((int) (pt.getIntX()));
            pos.setY((int) (pt.getIntY()));
            pos.setSpeed((int) (location.getSpeed() * 100));
            pos.setRadius(Double.parseDouble(String.format("%.2f", location.getRadius())));

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = dateFormat.parse(location.getTime());
                pos.setGtime(date.getTime());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                pos = null;
                e.printStackTrace();
            }

            double longitude = pt.getIntX() / 1000000.0;
            double latitude = pt.getIntY() / 1000000.0;
            if (longitude < 74.0 || longitude > 136.0 || latitude < 17.0
                    || latitude > 54.0) {
                pos = null;
                return;
            }

            if (_lastPos != null) {
                if (_lastPos.getGtime() == pos.getGtime()) {
                    pos.setGtime(new Date().getTime());
                }
            }

            _lastPos = pos;

            DBHelper dbh = new DBHelper(CollectionService.this);

            List<GpsPosition> posList = new ArrayList<GpsPosition>();
            dbh.PositionGetAll(posList);

            if (posList.size() > 20) { // 如果是3的话 数据库保留4条数据
                for (int j = 0; j < posList.size() - 20; j++) {
                    dbh.PositionDelete(posList.get(j).getGtime());
                }
            }
            dbh.PositionAdd(pos);
            dbh.CleanUp();
            CheckSendPosition();
        }

    }

    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     */
//    private void acquireWakeLock() {
//        if (null == wakeLock) {
//            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getCanonicalName());
//            if (null != wakeLock) {
//                wakeLock.acquire();
//                wakeLock.setReferenceCounted(false);
//            }
//        }
//    }

    // 释放设备电源锁
//    private void releaseWakeLock() {
//        if (null != wakeLock && wakeLock.isHeld()) {
//            wakeLock.release();
//            wakeLock = null;
//        }
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
//        wakeLock.release();
    }

}
