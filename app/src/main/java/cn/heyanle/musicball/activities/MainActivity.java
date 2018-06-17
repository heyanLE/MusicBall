package cn.heyanle.musicball.activities;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import cn.heyanle.musicball.DataMonitor;
import cn.heyanle.musicball.R;
import cn.heyanle.musicball.services.NotificationService;
import cn.heyanle.musicball.views.TouchBall;
import cn.heyanle.musicball.utils.HeLog;


public class MainActivity extends AppCompatActivity {

    //View相关变量
    RelativeLayout contentView = null;
    TouchBall ballView = null;
    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    WindowManager windowManager = null;

    private Switch aSwitch ;
    private SeekBar seekBarViewSize;
    private SeekBar seekBarBorderSize;
    private SeekBar seekBarBackSize;
    private LinearLayout linearLayout;
    private Button buttonLive;

    private int borderWidth = 10;
    private int backSize = 10;
    private int ballSize = 200;
    private int ifOpen = 0;

    private DataMonitor dataMonitor;

    String packageName = "com.netease.cloudmusic";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_layout);

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        dataMonitor = new DataMonitor(this);

        aSwitch = findViewById(R.id.main_switch);
        seekBarBackSize = findViewById(R.id.main_seekbar_backsize);
        seekBarBorderSize = findViewById(R.id.main_seekbar_bordersize);
        seekBarViewSize = findViewById(R.id.main_seekbar_viewsize);
        linearLayout = findViewById(R.id.main_linear);
        buttonLive = findViewById(R.id.main_button_live);

        ballSize = dataMonitor.getInt(packageName + ":ballSize",200);
        backSize = dataMonitor.getInt(packageName + ":backSize",2);
        borderWidth = dataMonitor.getInt(packageName + ":borderWidth",10);

        seekBarViewSize.setMax(250);
        seekBarViewSize.setProgress(ballSize);
        seekBarViewSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ballSize = i + 50;
                ballView.getLayoutParams().height = ballSize;
                ballView.getLayoutParams().width = ballSize;
                layoutParams.height = ballSize + backSize;
                layoutParams.width = ballSize + backSize;
                windowManager.updateViewLayout(contentView,layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                save();
                sentMsg(packageName, NotificationService.ACTIVITY_BALL_SIZE,ballSize);
            }
        });

        seekBarBorderSize.setMax(50);
        seekBarBorderSize.setProgress(borderWidth);
        seekBarBorderSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                borderWidth = i;
                ballView.setBorder(getColor(android.R.color.black),i);
                windowManager.updateViewLayout(contentView,layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                save();
                sentMsg(packageName, NotificationService.ACTIVITY_BORDER_WIDTH,borderWidth);
            }
        });

        seekBarBackSize.setMax(50);
        seekBarBackSize.setProgress(backSize);
        seekBarBackSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                backSize = i;
                layoutParams.height = ballSize + backSize;
                layoutParams.width = ballSize + backSize;
                windowManager.updateViewLayout(contentView,layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                save();
                sentMsg(packageName, NotificationService.ACTIVITY_BACK_SIZE,backSize);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (ifOpen == 1) {
                    return;
                }
                if (b){
                    Toast.makeText(MainActivity.this, "请授予通知使用权", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "请取消通知使用权", Toast.LENGTH_LONG).show();
                }
                gotoNotificationAccessSetting(MainActivity.this);
            }
        });

        initView();

        buttonLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppDetailSettingIntent(MainActivity.this);
            }
        });

        findViewById(R.id.main_text_pro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("MusicBall Pro")//设置对话框的标题
                        .setMessage("音乐控制！更多音乐软件的支持！频谱显示！尽在MusicBall Pro！")//设置对话框的内容
                        //设置对话框的按钮
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        findViewById(R.id.main_relative_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (notificationListenerEnable()){
            ifOpen = 1;
            aSwitch.setChecked(true);
            ifOpen = 0;
            linearLayout.setVisibility(View.VISIBLE);
            showView();
        }else{
            ifOpen = 1;
            aSwitch.setChecked(false);
            ifOpen = 0;
            linearLayout.setVisibility(View.GONE);
            removeView();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if (notificationListenerEnable()){
            ifOpen = 1;
            aSwitch.setChecked(true);
            ifOpen = 0;
            linearLayout.setVisibility(View.VISIBLE);
            showView();
            HeLog.i("MainActivity onStart",this);
        }else{
            ifOpen = 1;
            aSwitch.setChecked(false);
            ifOpen = 0;
            linearLayout.setVisibility(View.GONE);
            removeView();
        }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        removeView();
    }


    private void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(localIntent);
        Toast.makeText(context,"请允许MusicBall后台运行和开机自启",Toast.LENGTH_SHORT).show();
    }

    private void sentMsg(String which, int what, int arg1){
        Intent intent = new Intent(NotificationService.BROADCAST_URI);
        Bundle bundle = new Bundle();
        bundle.putInt("What",what);
        bundle.putInt("arg1",arg1);
        bundle.putString("Which",which);
        intent.putExtra("Data",bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void removeView(){
        if (windowManager != null && contentView != null){
            try {
                windowManager.removeView(contentView);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void showView(){
        try {
            windowManager.addView(contentView,layoutParams);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initView(){

        if (windowManager == null)
            windowManager = (WindowManager) getApplication().getSystemService(Application.WINDOW_SERVICE);

        layoutParams.windowAnimations = android.R.style.Animation_Translucent;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = ballSize + backSize;
        layoutParams.height = ballSize + backSize;
        layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        layoutParams.x = (int)(50);
        layoutParams.y = (int)(50);

        contentView = new RelativeLayout(this);
        contentView.setBackgroundResource(R.drawable.white);
        contentView.setGravity(Gravity.CENTER);
        //contentView.setAlpha(alpha);

        ballView = new TouchBall(this);
        ballView.setBorder(getColor(android.R.color.black),borderWidth);
        ballView.setImageDrawableRes(R.mipmap.ic_head);

        ballView.startTurn();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ballSize,ballSize);
        contentView.addView(ballView,params);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "这是预览悬浮球", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void save(){
        dataMonitor.setInt(packageName + ":ballSize",ballSize);
        dataMonitor.setInt(packageName + ":backSize",backSize);
        dataMonitor.setInt(packageName + ":borderWidth",borderWidth);
        dataMonitor.apply();
    }

    //检测是否拥有通知使用权
    private boolean notificationListenerEnable() {
        boolean enable = false;
        String packageName = getPackageName();
        String flat= Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        if (flat != null) {
            enable= flat.contains(packageName);
        }
        return enable;
    }

    //跳转到授予通知使用权页面
    private void gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch(ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings","com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
