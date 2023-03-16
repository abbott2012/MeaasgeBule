package com.bql.pulltorefreshandloadmore.loadmorestyle.indicator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: SemiCircleSpinIndicator <br>
 * Description: <br>
 * Author: Cyarie <br>
 * Created: 2016/4/15 11:38 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class SemiCircleSpinIndicator extends BaseIndicatorController{

    @Override
    public void draw(Canvas canvas, Paint paint) {
        RectF rectF=new RectF(0,0,getWidth(),getHeight());
        canvas.drawArc(rectF,-60,120,false,paint);
    }

    @Override
    public List<Animator> createAnimation() {
        List<Animator> animators=new ArrayList<>();
        ObjectAnimator rotateAnim=ObjectAnimator.ofFloat(getTarget(),"rotation",0,180,360);
        rotateAnim.setDuration(600);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.start();
        animators.add(rotateAnim);
        return animators;
    }
}
