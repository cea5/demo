package com.depart.cea.test1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class KeepLiveActivity extends Activity {
    private static KeepLiveActivity instance;
    private static String TAG = "KeepLiveActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        Log.i(TAG, "onCreate: ");
//        startService(new Intent(this, MyService.class));//设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    /**
     * 开启保活页面
     */
    public static void startKeepAlive() {
        Intent intent = new Intent(MyApplication.getInstance(), KeepLiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MyApplication.getInstance().startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    /**
     * 关闭保活页面
     */
    public static void killKeepAlive() {
        if (instance != null) {
            Log.i(TAG, "instance != null");
            instance.finish();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Instrumentation inst= new Instrumentation();
//                        Log.i(TAG, "run: back");
//                        inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        }else{
            Log.i(TAG, "instance = null");
        }
    }

}
