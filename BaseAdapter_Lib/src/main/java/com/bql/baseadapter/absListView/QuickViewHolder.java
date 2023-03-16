package com.bql.baseadapter.absListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bql.baseadapter.AbsListViewHelper;

/**
 * <p>便捷操作的ViewHolder</p>
 * Created by Cyarie on 2016/3/30.
 */
public class QuickViewHolder extends BaseViewHolder implements AbsListViewHelper<QuickViewHolder> {

    public QuickViewHolder(Context context, int position, ViewGroup parent,
                           int layoutId) {
        super(context, position, parent, layoutId);
    }

    public QuickViewHolder() {
    }

    @Override
    public QuickViewHolder get(Context context, int position,
                               View convertView, ViewGroup parent, int layoutId) {
        QuickViewHolder holder;
        if (convertView == null) {
            holder = new QuickViewHolder(context, position, parent, layoutId);
        } else {
            holder = (QuickViewHolder) convertView.getTag();
            if (holder.mLayoutId != layoutId) {
                holder = new QuickViewHolder(context, position, parent, layoutId);
            }
            holder.setPosition(position);
        }
        return holder;
    }


    @Override
    public QuickViewHolder setText(int viewId, String value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    @Override
    public QuickViewHolder setImageResource(int viewId, int imgResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imgResId);
        return this;
    }

    @Override
    public QuickViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    @Override
    public QuickViewHolder setBackgroundColorRes(int viewId, int colorRes) {
        View view = getView(viewId);
        view.setBackgroundResource(colorRes);
        return this;
    }

    @Override
    public QuickViewHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    @Override
    public QuickViewHolder setTextColorRes(int viewId, int colorRes) {
        TextView view = getView(viewId);
        view.setTextColor(mContext.getResources().getColor(colorRes));
        return this;
    }

    @Override
    public QuickViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    @Override
    public QuickViewHolder setImageDrawableRes(int viewId, int drawableRes) {
        Drawable drawable = mContext.getResources().getDrawable(drawableRes);
        return setImageDrawable(viewId, drawable);
    }


    @Override
    public QuickViewHolder setImageBitmap(int viewId, Bitmap imgBitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(imgBitmap);
        return this;
    }

    @Override
    public QuickViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    @Override
    public QuickViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    @Override
    public QuickViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    @Override
    public QuickViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    @Override
    public QuickViewHolder setAdapter(int viewId, Adapter adapter) {
        AdapterView view = getView(viewId);
        view.setAdapter(adapter);
        return this;
    }

    @Override
    public QuickViewHolder setAdapter(int viewId, RecyclerView.Adapter adapter) {
        RecyclerView view = getView(viewId);
        view.setAdapter(adapter);
        return this;
    }

    @Override
    public QuickViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    @Override
    public QuickViewHolder linkify(int viewId) {
        TextView view = getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    @Override
    public QuickViewHolder setTypeface(int viewId, Typeface typeface) {
        TextView view = getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    @Override
    public QuickViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    @Override
    public QuickViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    @Override
    public QuickViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    @Override
    public QuickViewHolder setMax(int viewId, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        return this;
    }

    @Override
    public QuickViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    @Override
    public QuickViewHolder setRating(int viewId, float rating, int max) {
        RatingBar view = getView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }
}
