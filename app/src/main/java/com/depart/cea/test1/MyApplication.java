package com.depart.cea.test1;

import android.app.Application;

import com.huawei.android.hms.agent.HMSAgent;

/**
 * Created by Administrator on 2018/4/6 0006.
 */

public class MyApplication extends Application{
    private String TAG = "MyApplication";
    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = (MyApplication) getApplicationContext();
        HMSAgent.init(this);
    }
    public static MyApplication getInstance(){
        return instance;
    }

}
