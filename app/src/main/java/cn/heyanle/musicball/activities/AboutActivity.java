package cn.heyanle.musicball.activities;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import cn.heyanle.musicball.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_layout);

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        RelativeLayout relativeLayout = findViewById(R.id.about_layout);
        //设置旋转
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(relativeLayout,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.start();
    }
}
