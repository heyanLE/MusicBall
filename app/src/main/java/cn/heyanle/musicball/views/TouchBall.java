package cn.heyanle.musicball.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class TouchBall extends FrameLayout {

    //旋转状态 0 -> 停止 ; 1 -> 暂停 ; 2 -> 旋转
    public int isTurn = 0;


    private CircleImageView circleImageView;
    private ObjectAnimator objectAnimator;

    public TouchBall(Context context){
        super(context);
        circleImageView = new CircleImageView(context);

        objectAnimator = ObjectAnimator.ofFloat(circleImageView,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);


        addView(circleImageView);
    }

    public void startTurn(){
        objectAnimator.start();
        isTurn = 2;
    }

    public void stopTurn(){
        objectAnimator.end();
        isTurn = 0;
    }

    public void pauseTurn(){
        objectAnimator.pause();
        isTurn = 1;
    }

    public void resumeTurn(){
        objectAnimator.resume();
        isTurn = 2;
    }

    public void setBorder(int color,int width){
        circleImageView.setBorderWidth(width);
        circleImageView.setBorderColor(color);
    }

    public void setImageDrawable(Drawable drawable){
        circleImageView.setImageDrawable(drawable);
    }

    public void setImageDrawableRes(int drawableRes){
        circleImageView.setImageResource(drawableRes);
    }

}
