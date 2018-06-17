package cn.heyanle.musicball.services.data;


import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.heyanle.musicball.services.NotificationService;
import cn.heyanle.musicball.utils.HeLog;

public class CloudMusic extends Base {

    private static final String PACKAGE_NAME = "com.netease.cloudmusic";

    private Resources resources = null;

    ViewGroup notificationRoot;

    int id ;


    public CloudMusic(NotificationService notificationService){
        super(notificationService,PACKAGE_NAME);
        //获取改包资源文件
        try {
            resources = context.getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiver(Bundle bundle) {
        super.receiver(bundle);
        int what = bundle.getInt("What");
        int arg = bundle.getInt("arg1");
        switch (what){
            case NotificationService.ACTIVITY_BALL_SIZE:
                ballSize = arg;
                ballView.getLayoutParams().width = ballSize;
                ballView.getLayoutParams().height = ballSize;
                layoutParams.height = ballSize + backSize;
                layoutParams.width = ballSize + backSize;
                try {
                    windowManager.updateViewLayout(contentView,layoutParams);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case NotificationService.ACTIVITY_BACK_SIZE:
                backSize = arg;
                layoutParams.height = ballSize + backSize;
                layoutParams.width = ballSize + backSize;
                try {
                    windowManager.updateViewLayout(contentView,layoutParams);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case NotificationService.ACTIVITY_BORDER_WIDTH:
                borderWidth = arg;
                ballView.setBorder(context.getColor(android.R.color.black),arg);
                try {
                    windowManager.updateViewLayout(contentView,layoutParams);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, NotificationService notificationService) {
        super.onNotificationPosted(sbn,notificationService);

        Notification notification = sbn.getNotification();

        //如果是系统样式
        if (notification.extras.containsKey(NotificationCompat.EXTRA_MEDIA_SESSION)){


            id = sbn.getId();

            //如果没有显示则显示View
            if (!onShow) showView();

            //获取封面并给
            if (notification.getLargeIcon() != null)
                ballView.setImageDrawable(notification.getLargeIcon().loadDrawable(context));


            //获取按钮Action
            int actionCount = NotificationCompat.getActionCount(notification);
            for (int i = 0; i < actionCount; i++) {
                NotificationCompat.Action action = NotificationCompat.getAction(notification, i);
                if (resources != null) {
                    try {
                        //获取到图标名字来判断是暂停还是播放
                        /*
                        note_btn_loved 我喜欢 开 note_btn_love 我喜欢 关
                        note_btn_pre 上一首
                        note_btn_pause_ms 暂停 和  note_btn_play_ms 播放
                        note_btn_next 下一首
                        note_btn_lyc_mc 歌词 关 和 note_btn_lyced_ms 歌词 开

                         */
                        HeLog.i("Icon",resources.getResourceEntryName(action.getIcon()),this);
                        if (resources.getResourceEntryName(action.getIcon()).equals("note_btn_pause_ms")){
                            isPlaying = true;
                            if (ballView.isTurn == 0) ballView.startTurn();
                            if (ballView.isTurn == 1) ballView.resumeTurn();
                            Toast.makeText(context,"播放",Toast.LENGTH_SHORT).show();
                        }
                        if (resources.getResourceEntryName(action.getIcon()).equals("note_btn_play_ms")){
                            isPlaying = false;
                            if (ballView.isTurn == 2) ballView.pauseTurn();
                            Toast.makeText(context,"暂停",Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        //如果是普通样式
        else if (notification.tickerText != null
                && notification.tickerText.toString().equals("网易云音乐正在播放")){

            id = sbn.getId();

            //如果没有显示则显示View
            if (!onShow) showView();

            //获得ViewRoot
            notificationRoot = (ViewGroup) notification.bigContentView.apply(context
                    , new FrameLayout(context));

            //得到布局文件
            RelativeLayout relativeLayout = (RelativeLayout) notificationRoot.getChildAt(0);

            //遍历布局文件 通过id找相应的view
            for (int i = 0 ; i < relativeLayout.getChildCount() ; i ++){
                View view =  relativeLayout.getChildAt(i);
                HeLog.i("View Id",resources.getResourceEntryName(view.getId()),this);
                String viewId = resources.getResourceEntryName(view.getId());

                //对应id
                switch (viewId){
                    case "notifyAlbumCover"://封面view 直接设置封面
                        ballView.setImageDrawable(((ImageView)view).getDrawable());
                        break;
                    case "playNotificationBtns"://按钮布局
                        //遍历按钮
                        LinearLayout linearLayout = (LinearLayout) view;
                        for (int ii = 0 ; ii < linearLayout.getChildCount() ; ii ++){
                            ImageView imageView = (ImageView) linearLayout.getChildAt(ii);
                            HeLog.i("Button Id",resources.getResourceEntryName(imageView.getId()),this);
                            String buttonId = resources.getResourceEntryName(imageView.getId());
                            switch (buttonId){
                                case "playNotificationStar":
                                    break;
                                case "playNotificationPre":
                                    break;
                                case "playNotificationToggle":
                                    //这里通过拿到View的图片资源Bitmap 判断中间像素点颜色来判断是否播放
                                    Bitmap mp = drawableToBitmap(imageView.getDrawable());
                                    int color = mp.getPixel(mp.getWidth()/2,mp.getHeight()/2);
                                    HeLog.i("Button_Color",color + "",this);
                                    if (color == 0){
                                        isPlaying = true;
                                        Toast.makeText(context,"播放",Toast.LENGTH_SHORT).show();
                                        if (ballView.isTurn == 0) ballView.startTurn();
                                        if (ballView.isTurn == 1) ballView.resumeTurn();
                                    }else{
                                        isPlaying = false;
                                        Toast.makeText(context,"暂停",Toast.LENGTH_SHORT).show();
                                        if (ballView.isTurn == 2) ballView.pauseTurn();
                                    }
                                    break;
                                case "playNotificationNext":
                                    break;
                                case "playNotificationLyric":
                                    break;
                            }
                        }
                        break;
                }
            }


        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationService notificationService) {
        super.onNotificationRemoved(sbn,notificationService);

        if (sbn.getId() == id)
            removeView();

    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

}
