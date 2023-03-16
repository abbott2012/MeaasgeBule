package com.bql.pulltorefreshandloadmore.loadmorestyle.indicator;

import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

/**
 * ClassName: BaseIndicatorController <br>
 * Description: Indicator控制器<br>
 * Author: Cyarie <br>
 * Created: 2016/4/15 11:05 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class BaseIndicatorController {

    /**
     * View
     */
    private View mTarget;

    /**
     * 动画集合
     */
    private List<Animator> mAnimators;


    public void setTarget(View target) {
        this.mTarget = target;
    }

    public View getTarget() {
        return mTarget;
    }


    public int getWidth() {
        return mTarget.getWidth();
    }

    public int getHeight() {
        return mTarget.getHeight();
    }

    /**
     * 刷新View
     */
    public void postInvalidate() {
        mTarget.postInvalidate();
    }

    /**
     * 画出indicator
     *
     * @param canvas
     * @param paint
     */
    public abstract void draw(Canvas canvas, Paint paint);

    /**
     * 创建动画集合
     */
    public abstract List<Animator> createAnimation();


    /**
     * 初始化动画集合
     */
    public void initAnimation() {
        mAnimators = createAnimation();
    }

    /**
     * 设置动画的状态
     * make animation to start or end when target
     * view was be Visible or Gone or Invisible.
     * make animation to cancel when target view
     * be onDetachedFromWindow.
     *
     * @param animStatus
     */
    public void setAnimationStatus(AnimStatus animStatus) {
        if (mAnimators == null) {
            return;
        }
        int count = mAnimators.size();
        for (int i = 0; i < count; i++) {
            Animator animator = mAnimators.get(i);
            boolean isRunning = animator.isRunning();
            switch (animStatus) {
                case START:
                    if (!isRunning) {
                        animator.start();
                    }
                    break;
                case END:
                    if (isRunning) {
                        animator.end();
                    }
                    break;
                case CANCEL:
                    if (isRunning) {
                        animator.cancel();
                    }
                    break;
            }
        }
    }


    /**
     * 动画状态枚举  开始  结束  取消
     */
    public enum AnimStatus {
        START, END, CANCEL
    }
}
