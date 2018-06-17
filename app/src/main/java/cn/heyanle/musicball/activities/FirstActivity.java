package cn.heyanle.musicball.activities;

import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.heyanle.musicball.R;


public class FirstActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonNotification ;
    private Button buttonBall;
    private Button buttonFinish;
    private RelativeLayout relativeLayout;
    private Button buttonLive;

    private boolean isLive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_layout);

        //初始化View
        relativeLayout = findViewById(R.id.first_layout);
        buttonNotification = findViewById(R.id.first_button_notification);
        buttonBall = findViewById(R.id.first_button_ball);
        buttonFinish = findViewById(R.id.first_button_finish);
        buttonLive = findViewById(R.id.first_button_live);

        //设置actionbar阴影为0
        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        if (notificationListenerEnable() && Settings.canDrawOverlays(FirstActivity.this))
            gotoMainActivity();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //如果已经有相应权限，刷新View
        if (notificationListenerEnable()) buttonNotification.setText("已授予");
        if (Settings.canDrawOverlays(FirstActivity.this)) buttonBall.setText("已授予");

        //设置监听
        buttonNotification.setOnClickListener(this);
        buttonBall.setOnClickListener(this);
        buttonFinish.setOnClickListener(this);

        //设置旋转
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(relativeLayout,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //如果已经有相应权限，刷新View
        if (notificationListenerEnable()) buttonNotification.setText("已授予");
        if (Settings.canDrawOverlays(FirstActivity.this)) buttonBall.setText("已授予");
    }

    private void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(localIntent);
        Toast.makeText(context,"请允许MusicBall后台运行和开机自启",Toast.LENGTH_SHORT).show();
    }



    //点击方法
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.first_button_notification:
                if (! notificationListenerEnable())
                    gotoNotificationAccessSetting(FirstActivity.this);
                break;
            case R.id.first_button_ball:
                if (! Settings.canDrawOverlays(FirstActivity.this)){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                }
                break;
            case R.id.first_button_finish:
                if (notificationListenerEnable() && Settings.canDrawOverlays(FirstActivity.this)){
                    if(isLive) gotoMainActivity();
                    else {
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("请允许后台运行")//设置对话框的标题
                                .setMessage("对于MIUI EMUI等ui需要允许后台运行后才能使用 如果你是类原生之类的ui 请无视本提示")//设置对话框的内容
                                //设置对话框的按钮
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                        isLive = true;
                    }
                }

                else Toast.makeText(FirstActivity.this, "请授予MusicBall相关权限", Toast.LENGTH_LONG).show();
                break;
            case R.id.first_button_live:
                getAppDetailSettingIntent(this);
                isLive = true;
        }
    }

    //跳转到MainActivity
    private void gotoMainActivity(){
        Intent intent = new Intent(FirstActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
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
