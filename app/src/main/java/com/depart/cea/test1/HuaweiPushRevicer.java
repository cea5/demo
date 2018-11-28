package com.depart.cea.test1;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.depart.cea.test1.net.CollectionService;
import com.huawei.hms.support.api.push.PushReceiver;

public class HuaweiPushRevicer extends PushReceiver {
    private String TAG = "HuaweiPushRevicer";

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        Log.i(TAG, "onToken: " + token);
        DataSingleton.getInstance().setToken(token);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        Log.d(TAG, "onPushState: " + pushState);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            //CP可以自己解析消息内容，然后做相应的处理
            String content = new String(msg, "UTF-8");
            Log.i(TAG, "收到PUSH透传消息,消息内容为:" + content);
            Intent it = new Intent(context, CollectionService.class);
            context.startService(it);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            Log.i(TAG, "收到通知栏消息点击事件,notifyId:" + notifyId);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
        }

        String message = extras.getString(BOUND_KEY.pushMsgKey);
        super.onEvent(context, event, extras);
    }
}
