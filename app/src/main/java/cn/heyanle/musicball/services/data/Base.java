package cn.heyanle.musicball.services.data;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import cn.heyanle.musicball.DataMonitor;
import cn.heyanle.musicball.R;
import cn.heyanle.musicball.services.NotificationService;
import cn.heyanle.musicball.utils.HeLog;
import cn.heyanle.musicball.views.TouchBall;

public abstract class Base {

    String packageName = "com.netease.cloudmusic";

    //View相关变量
    RelativeLayout contentView = null;
    TouchBall ballView = null;
    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    WindowManager windowManager = null;

    //是否正在播放
    boolean isPlaying = false;

    //上下文对象
    Context context;

    //当前View位置坐标占屏幕坐标的百分比
    float pX = 0f;
    float pY = 0f;

    //坐标系长度
    float xPixels = 0;
    float yPixels = 0;

    //点击点坐标占View的相对坐标
    float touchX = 0f;
    float touchY = 0f;

    //View相关属性
    int ballSize ; //封面大小
    int backSize ; //背景白色半透明宽度
    int borderWidth ; //边框宽度
    int alpha ; //透明度

    boolean onShow = false;

    //数据管理者对象
    DataMonitor dataMonitor;

    public Base(NotificationService notificationService,String packageName){
        notificationService.dataMap.put(packageName,this);

        this.packageName = packageName;
        context = notificationService;
        dataMonitor = new DataMonitor(context);

        if (windowManager == null)
            windowManager = notificationService.getWindowManager();

        //读取数据
        ballSize = dataMonitor.getInt(packageName + ":ballSize",200);
        backSize = dataMonitor.getInt(packageName + ":backSize",2);
        borderWidth = dataMonitor.getInt(packageName + ":borderWidth",10);
        alpha = dataMonitor.getInt(packageName + ":alpha",0);
        pX = dataMonitor.getFloat(packageName + ":pX",0);
        pY = dataMonitor.getFloat(packageName + ":pY",0);

        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        xPixels = dm.widthPixels;
        yPixels = dm.heightPixels;

        initView();
    }

    public void onNotificationPosted(StatusBarNotification sbn
            , NotificationService notificationService){
        if (windowManager == null)
            windowManager = notificationService.getWindowManager();
    }

    public void onNotificationRemoved(StatusBarNotification sbn
            , NotificationService notificationService){
        if (windowManager == null)
            windowManager = notificationService.getWindowManager();
    }

    public void onConfigurationChanged() {
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        xPixels = dm.widthPixels;
        yPixels = dm.heightPixels;
        layoutParams.x = (int)((float)dm.widthPixels*pX);
        layoutParams.y = (int)((float)dm.heightPixels*pY);
        if (windowManager != null)
            try {
                windowManager.updateViewLayout(contentView, layoutParams);
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    private void saveXY(int x,int y){
        pX = x/xPixels;
        pY = y/yPixels;
        dataMonitor.setFloat(packageName + ":pX",pX);
        dataMonitor.setFloat(packageName + ":pY",pY);
        dataMonitor.apply();
    }

    void showView(){
        if (windowManager != null && !onShow) {
            windowManager.addView(contentView, layoutParams);
            onShow = true;
            HeLog.i(packageName,"onShow",this);
        }
    }

    public void receiver(Bundle bundle){}

    public void removeView(){
        if (windowManager != null && onShow) {
            windowManager.removeView(contentView);
            onShow = false;
            HeLog.i(packageName,"onRemove",this);
        }
    }

    private void initView(){
        layoutParams.windowAnimations = android.R.style.Animation_Translucent;


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;


        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = ballSize + backSize;
        layoutParams.height = ballSize + backSize;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.x = (int)(xPixels*pX);
        layoutParams.y = (int)(yPixels*pY);

        contentView = new RelativeLayout(context);
        contentView.setBackgroundResource(R.drawable.white);
        contentView.setGravity(Gravity.CENTER);
        //contentView.setAlpha(alpha);

        ballView = new TouchBall(context);
        ballView.setBorder(context.getColor(android.R.color.black),borderWidth);


        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ballSize,ballSize);
        contentView.addView(ballView,params);

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        ballView.getLayoutParams().width -= 10;
                        ballView.getLayoutParams().height -= 10;

                        ballView.stopTurn();

                        touchX = motionEvent.getX();
                        touchY = motionEvent.getY();

                        windowManager.updateViewLayout(contentView,layoutParams);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int[] screenInt = new int[2];
                        int xxx = layoutParams.y;
                        contentView.getLocationOnScreen(screenInt);

                        layoutParams.x = (int)(motionEvent.getRawX() - touchX);
                        layoutParams.y = (int)(motionEvent.getRawY() - touchY - (screenInt[1] - xxx));

                        windowManager.updateViewLayout(contentView,layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        ballView.getLayoutParams().width += 10;
                        ballView.getLayoutParams().height += 10;

                        saveXY(layoutParams.x,layoutParams.y);

                        if (isPlaying)
                            ballView.startTurn();

                        windowManager.updateViewLayout(contentView,layoutParams);

                }
                return false;
            }
        });

    }

}
