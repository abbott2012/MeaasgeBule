package com.bql.pulltorefreshandloadmore.loadmorestyle.indicator;

import android.animation.Animator;
import android.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: LineScalePulseOutRapidIndicator <br>
 * Description: <br>
 * Author: Cyarie <br>
 * Created: 2016/4/15 11:35 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class LineScalePulseOutRapidIndicator extends LineScaleIndicator {

    @Override
    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList<>();
        long[] delays = new long[]{400, 200, 0, 200, 400};
        for (int i = 0; i < 5; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.4f, 1);
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            scaleAnim.start();
            animators.add(scaleAnim);
        }
        return animators;
    }
}
