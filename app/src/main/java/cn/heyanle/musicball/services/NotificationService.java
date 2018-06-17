package cn.heyanle.musicball.services;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import cn.heyanle.musicball.R;
import cn.heyanle.musicball.services.data.Base;
import cn.heyanle.musicball.services.data.CloudMusic;

public class NotificationService extends NotificationListenerService {

    private WindowManager windowManager = null;

    public static final String BROADCAST_URI = "cn.heyanle.musicball.broadcast";

    public static final int ACTIVITY_BALL_SIZE = 2;
    public static final int ACTIVITY_BORDER_WIDTH = 3;
    public static final int ACTIVITY_BACK_SIZE = 4;

    MyBroadcastReceive broadcastReceive;


    public Map<String,Base> dataMap = new HashMap<>();

    class MyBroadcastReceive extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Bundle bundle = arg1.getBundleExtra("Data");
            if (bundle == null)return;
            String pack = bundle.getString("Which");
            dataMap.get(pack).receiver(bundle);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getApplication().getSystemService(Application.WINDOW_SERVICE);

        new CloudMusic(this);

        broadcastReceive = new MyBroadcastReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_URI);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceive,filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceive);

        for (String key : dataMap.keySet()){

            dataMap.get(key).removeView();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (HashMap.Entry<String,Base> entry:dataMap.entrySet()){
            entry.getValue().onConfigurationChanged();
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        //如果WindowManager为Null 赋值
        if (windowManager == null)
            windowManager = (WindowManager) getApplication().getSystemService(Application.WINDOW_SERVICE);

        //通过包名识别 调用相应模块
        String packageName = sbn.getPackageName();

        for (String key : dataMap.keySet()){

            if (packageName.equals(key))
                dataMap.get(key).onNotificationPosted(sbn,this);

        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (windowManager == null)
            windowManager = (WindowManager) getApplication().getSystemService(Application.WINDOW_SERVICE);

        //通过包名识别 调用相应模块
        String packageName = sbn.getPackageName();

        for (String key : dataMap.keySet()){

            if (packageName.equals(key))
                dataMap.get(key).onNotificationRemoved(sbn,this);

        }
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }
}
