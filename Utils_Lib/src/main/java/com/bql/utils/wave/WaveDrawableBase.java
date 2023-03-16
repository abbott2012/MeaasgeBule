package com.bql.utils.wave;

import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

/**
 *  Created by Cyarie on 2016/1/18.
 */
public abstract class WaveDrawableBase extends Drawable {
    public interface OnWaveDrawableListener {
        void onWaveDrawableAnimatorStart();

        void onWaveDrawableAnimatorEnd();
    }

    public abstract void setOnWaveDrawableListener(OnWaveDrawableListener onWaveDrawableListener);

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
