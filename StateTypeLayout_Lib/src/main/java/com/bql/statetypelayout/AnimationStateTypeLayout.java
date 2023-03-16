package com.bql.statetypelayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhilianbao.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: AnimationStateTypeLayout <br>
 * Description: <br>
 * Author: Cyarie <br>
 * Created: 2016/7/1 09:12 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class AnimationStateTypeLayout extends FrameLayout {

    private static final String TAG_LOADING = "StateTypeLayout.TAG_LOADING";
    private static final String TAG_EMPTY = "StateTypeLayout.TAG_EMPTY";
    private static final String TAG_ERROR = "StateTypeLayout.TAG_ERROR";

    private final String CONTENT = "type_content";
    private final String LOADING = "type_loading";
    private final String EMPTY = "type_empty";
    private final String ERROR = "type_error";

    private LayoutInflater inflater;
    private View view;
    private LayoutParams layoutParams;
    private Drawable currentBackground;

    private View contentView;

    /*Loading View*/
    private LinearLayout loadingStateLinearLayout;
    //    private MaterialProgressBar loadingStateProgressBar;
    private ImageView mLoadingView;
    private AnimationDrawable loadingDrawable;
    private int loadingDrawableResId;
    //    private TextView loadingStateContentTextView;
    private MarqueeView loadingTextView;

    /*Empty View*/
    private LinearLayout emptyStateLinearLayout;
    private ImageView emptyStateImageView;
    private TextView emptyStateTitleTextView;
    private TextView emptyStateContentTextView;
    private TextView emptyStateButton;

    /*Error View*/
    private LinearLayout errorStateLinearLayout;
    private ImageView errorStateImageView;
    private TextView errorStateTitleTextView;
    private TextView errorStateContentTextView;
    private TextView errorStateButtonLeft;
    private TextView errorStateButtonRight;

    /*Loading State Style*/
    private int loadingStateBackgroundColor;
    //    private int loadingStateContentTextColor;
    //    private int loadingStateContentTextSize;
    //    private int loadingProgressBarColor;

    /*Empty State Style*/
    //    private int emptyStateImageWidth;
    //    private int emptyStateImageHeight;
    private int emptyStateTitleTextSize;
    private int emptyStateContentTextSize;
    private int emptyStateTitleTextColor;
    private int emptyStateContentTextColor;
    private int emptyStateBackgroundColor;
    private int emptyStateButtonTextColor;

    /*Error State Style*/
    //    private int errorStateImageWidth;
    //    private int errorStateImageHeight;
    private int errorStateTitleTextSize;
    private int errorStateContentTextSize;
    private int errorStateTitleTextColor;
    private int errorStateContentTextColor;
    private int errorStateButtonTextColor;
    private int errorStateBackgroundColor;

    private String state = CONTENT;

    private boolean isShowAnim;//是否显示动画

    private Context mContext;

    public AnimationStateTypeLayout(Context context) {
        super(context);
        mContext = context;
    }

    public AnimationStateTypeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public AnimationStateTypeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }


    /**
     * 初始化 attrs
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StateTypeLayout);

        loadingStateBackgroundColor =
                typedArray.getColor(R.styleable.StateTypeLayout_loadingBackgroundColor, Color.TRANSPARENT);

        //        loadingStateContentTextSize = typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_loadingTextSize, 12);
        //
        //        loadingStateContentTextColor = typedArray.getColor(R.styleable.StateTypeLayout_loadingTextColor, Color.parseColor("#999999"));

        //        loadingProgressBarColor = typedArray.getColor(R.styleable.StateTypeLayout_loadingProgressBarColor, Color.parseColor("#f07448"));
        loadingDrawableResId = typedArray.getResourceId(R.styleable.StateTypeLayout_loadingDrawable, R.drawable.loading);
        //Empty state attrs
        //        emptyStateImageWidth =
        //                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_emptyImageWidth, 308);
        //
        //        emptyStateImageHeight =
        //                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_emptyImageHeight, 308);

        emptyStateTitleTextSize =
                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_emptyTitleTextSize, 15);

        emptyStateContentTextSize =
                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_emptyContentTextSize, 13);

        emptyStateTitleTextColor =
                typedArray.getColor(R.styleable.StateTypeLayout_emptyTitleTextColor, Color.BLACK);

        emptyStateContentTextColor =
                typedArray.getColor(R.styleable.StateTypeLayout_emptyContentTextColor, Color.parseColor("#999999"));

        emptyStateBackgroundColor =
                typedArray.getColor(R.styleable.StateTypeLayout_emptyBackgroundColor, Color.TRANSPARENT);
        emptyStateButtonTextColor =
                typedArray.getColor(R.styleable.StateTypeLayout_emptyButtonTextColor, Color.parseColor("#f91c4c"));
        //Error state attrs
        //        errorStateImageWidth =
        //                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_errorImageWidth, 308);
        //
        //        errorStateImageHeight =
        //                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_errorImageHeight, 308);

        errorStateTitleTextSize =
                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_errorTitleTextSize, 15);

        errorStateContentTextSize =
                typedArray.getDimensionPixelSize(R.styleable.StateTypeLayout_errorContentTextSize, 13);

        errorStateTitleTextColor =
                typedArray.getColor(R.styleable.StateTypeLayout_errorTitleTextColor, Color.BLACK);

        errorStateContentTextColor =
                typedArray.getColor(R.styleable.StateTypeLayout_errorContentTextColor, Color.parseColor("#999999"));

        errorStateButtonTextColor =
                typedArray.getColor(R.styleable.StateTypeLayout_errorButtonTextColor, Color.parseColor("#f91c4c"));

        errorStateBackgroundColor =
                typedArray.getColor(R.styleable.StateTypeLayout_errorBackgroundColor, Color.TRANSPARENT);

        isShowAnim = typedArray.getBoolean(R.styleable.StateTypeLayout_isShowAnim, false);

        typedArray.recycle();

        currentBackground = this.getBackground();
        setLoadingView
                ();
        setEmptyView();
        setErrorView();

        hideEmptyView();
        hideErrorView();
        hideLoadingView();
        hideContentView();
    }


    /**
     * 添加内容视图
     *
     * @param child  内容View 不能为空
     * @param index  位置
     * @param params 参数
     */
    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child.getTag() == null || (!child.getTag().equals(TAG_LOADING) &&
                !child.getTag().equals(TAG_EMPTY) && !child.getTag().equals(TAG_ERROR))) {
            if (contentView != null) {
                throw new IllegalStateException("AnimationStateTypeLayout ContentView can host one direct child");
            }
            contentView = child;
        }
    }

    /**
     * Hide all other states and show content
     * 显示内容视图 隐藏其他状态的视图
     */
    public void showContent() {
        switchState(CONTENT, null, null, null, null, null, null, null);
    }


    /**
     * Hide content and show the progress bar
     * 显示加载视图 隐藏内容
     */
    public void showLoading(/*String loadingTextContent*/) {
        switchState(LOADING, null, null, null, null, null, null, null);
    }

    /**
     * Show empty view when there are not data to show
     * <p/>
     * 当没有数据展示的时候 显示空的视图
     *
     * @param emptyImageDrawable Drawable to show 图片
     * @param emptyTextTitle     Title of the empty view to show 标题文字
     * @param emptyTextContent   Content of the empty view to show 内容文字
     */
    public void showEmpty(Drawable emptyImageDrawable, String emptyTextTitle, String emptyTextContent, String emptyButtonText, View.OnClickListener onClickListener) {
        switchState(EMPTY, emptyImageDrawable, emptyTextTitle, emptyTextContent, emptyButtonText, null, onClickListener, null);
    }


    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     * 当出错的时候显示带按钮的错误视图 提示用户操作
     *
     * @param errorImageDrawable   Drawable to show 显示的图片
     * @param errorTextTitle       Title of the error view to show 错误视图的标题文字
     * @param errorTextContent     Content of the error view to show 错误视图的内容文字
     * @param errorLeftButtonText  Text on the error left view button to show  错误视图的左边按钮文字
     * @param onLeftClickListener  Listener of the error left view button 错误视图左边按钮的监听
     * @param errorRightButtonText Text on the error left view button to show  错误视图的右边按钮文字
     * @param onRightClickListener Listener of the error left view button 错误视图右边按钮的监听
     */
    public void showError(Drawable errorImageDrawable, String errorTextTitle, String errorTextContent, String errorLeftButtonText, String errorRightButtonText, View.OnClickListener onLeftClickListener, View.OnClickListener onRightClickListener) {
        switchState(ERROR, errorImageDrawable, errorTextTitle, errorTextContent, errorLeftButtonText, errorRightButtonText, onLeftClickListener, onRightClickListener);
    }


    /**
     * Get which state is set
     * 获得当前的显示状态
     *
     * @return State
     */
    public String getState() {
        return state;
    }

    /**
     * Check if content is shown
     * 内容视图是否显示
     *
     * @return boolean
     */
    public boolean isContent() {
        return state.equals(CONTENT);
    }

    /**
     * Check if loading state is shown
     * 加载视图是否显示
     *
     * @return boolean
     */
    public boolean isLoading() {
        return state.equals(LOADING);
    }

    /**
     * Check if empty state is shown
     * 空视图是否显示
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return state.equals(EMPTY);
    }

    /**
     * Check if error state is shown
     * 错误视图是否显示
     *
     * @return boolean
     */
    public boolean isError() {
        return state.equals(ERROR);
    }

    /**
     * 切换视图显示
     *
     * @param state                状态
     * @param drawable             图片
     * @param errorText            标题文字
     * @param errorTextContent     内容文字
     * @param errorLeftButtonText  左边按钮文字
     * @param errorRightButtonText 右边按钮文字
     * @param onLeftClickListener  左边按钮点击监听
     * @param onRightClickListener 右边按钮点击监听
     */
    private void switchState(String state, Drawable drawable, String errorText, String errorTextContent,
                             String errorLeftButtonText, String errorRightButtonText, View.OnClickListener onLeftClickListener, View.OnClickListener onRightClickListener) {
        this.state = state;

        switch (state) {

            case CONTENT:
                //Hide all state views to display content
                //隐藏其他状态视图 显示内容视图
                hideEmptyView();
                hideErrorView();
                if (contentView.getVisibility() == VISIBLE) {
                    hideLoadingView();
                } else {
                    showContent(loadingStateLinearLayout, contentView, isShowAnim);
                }

                break;

            //显示加载视图
            case LOADING:
                hideEmptyView();
                hideErrorView();
                hideContentView();
                showLoadingView();

                //                if (errorText != null) {
                //
                //                }
                //                if (errorText == null) {
                //                    loadingStateContentTextView.setVisibility(View.GONE);
                //                } else {

                //                }

                break;
            //显示空视图
            case EMPTY:
                hideErrorView();
                hideContentView();
                showEmptyErrorView(loadingStateLinearLayout, emptyStateLinearLayout, isShowAnim);
                if (drawable == null) {
                    emptyStateImageView.setVisibility(View.GONE);
                } else {
                    emptyStateImageView.setImageDrawable(drawable);
                }
                if (errorText == null) {
                    emptyStateTitleTextView.setVisibility(View.GONE);
                } else {
                    emptyStateTitleTextView.setText(errorText);
                }
                if (errorTextContent == null) {
                    emptyStateContentTextView.setVisibility(View.GONE);
                } else {
                    emptyStateContentTextView.setText(errorTextContent);
                }
                if (errorLeftButtonText == null) {
                    emptyStateButton.setVisibility(View.GONE);
                } else {
                    emptyStateButton.setText(errorLeftButtonText);
                }
                emptyStateButton.setOnClickListener(onLeftClickListener);
                break;

            //显示错误视图
            case ERROR:
                hideContentView();
                hideEmptyView();
                showEmptyErrorView(loadingStateLinearLayout, errorStateLinearLayout, isShowAnim);
                if (drawable == null) {
                    errorStateImageView.setVisibility(View.GONE);
                } else {
                    errorStateImageView.setImageDrawable(drawable);
                }
                if (errorText == null) {
                    errorStateTitleTextView.setVisibility(View.GONE);
                } else {
                    errorStateTitleTextView.setText(errorText);
                }
                if (errorTextContent == null) {
                    errorStateContentTextView.setVisibility(View.GONE);
                } else {
                    errorStateContentTextView.setText(errorTextContent);
                }
                if (errorLeftButtonText == null) {
                    errorStateButtonLeft.setVisibility(View.GONE);
                } else {
                    errorStateButtonLeft.setText(errorLeftButtonText);
                }
                errorStateButtonLeft.setOnClickListener(onLeftClickListener);

                if (errorRightButtonText == null) {
                    errorStateButtonRight.setVisibility(View.GONE);
                } else {
                    errorStateButtonRight.setText(errorRightButtonText);
                }
                errorStateButtonRight.setOnClickListener(onRightClickListener);
                break;
        }
    }


    /**
     * 设置加载视图
     */
    public void setLoadingView() {
        if (loadingStateLinearLayout == null) {
            view = inflater.inflate(R.layout.state_loading_view, null);
            if (view == null) {
                throw new NullPointerException(
                        "Loading view is null! Have you specified a loading view in your layout xml file?");
            }
            loadingStateLinearLayout = (LinearLayout) view.findViewById(R.id.loadingViewLinearLayout);
            loadingStateLinearLayout.setTag(TAG_LOADING);

            //            loadingStateProgressBar = (MaterialProgressBar) view.findViewById(loadingStateProgressBar);
            mLoadingView = (ImageView) view.findViewById(R.id.iv_loading);
            mLoadingView.setBackgroundResource(loadingDrawableResId);
            loadingDrawable = (AnimationDrawable) mLoadingView.getBackground();
            loadingDrawable.mutate();
            loadingDrawable.start();
            //            loadingStateContentTextView = (TextView) view.findViewById(loadingStateContentTextView);
            loadingTextView = (MarqueeView) view.findViewById(R.id.loading_textView);
            List<String> loadingList = new ArrayList<>();
            loadingList.add(mContext.getString(R.string.loading_1));
            loadingList.add(mContext.getString(R.string.loading_2));
            loadingList.add(mContext.getString(R.string.loading_3));
            loadingList.add(mContext.getString(R.string.loading_4));
            loadingTextView.setNotices(loadingList);
            //设置进度条颜色
            //            loadingStateProgressBar.setBarColor(loadingProgressBarColor);

            //设置加载视图progressBar宽高
            //            loadingStateProgressBar.getLayoutParams().width = loadingStateProgressBarWidth;
            //            loadingStateProgressBar.getLayoutParams().height = loadingStateProgressBarHeight;
            //            loadingStateProgressBar.requestLayout();

            //设置加载视图文字大小和颜色
            //            loadingStateContentTextView.setTextSize(loadingStateContentTextSize);
            //            loadingStateContentTextView.setTextColor(loadingStateContentTextColor);

            //Set background color if not TRANSPARENT 设置加载视图背景颜色
            if (loadingStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(loadingStateBackgroundColor);
            }

            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            //            layoutParams.addRule(CENTER_IN_PARENT);

            addView(loadingStateLinearLayout, layoutParams);
        }

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (loadingDrawable != null) {
            loadingDrawable.stop();
        }
        if (loadingTextView != null && loadingTextView.isFlipping()) {
            loadingTextView.stop();
        }
    }

    /**
     * 设置空视图
     */
    private void setEmptyView() {
        if (emptyStateLinearLayout == null) {
            view = inflater.inflate(R.layout.state_empty_view, null);
            if (view == null) {
                throw new NullPointerException(
                        "Empty view is null! Have you specified a loading view in your layout xml file?");
            }
            emptyStateLinearLayout = (LinearLayout) view.findViewById(R.id.emptyViewLinearLayout);
            emptyStateLinearLayout.setTag(TAG_EMPTY);

            emptyStateImageView = (ImageView) view.findViewById(R.id.emptyStateImageView);
            emptyStateTitleTextView = (TextView) view.findViewById(R.id.emptyStateTitleTextView);
            emptyStateContentTextView = (TextView) view.findViewById(R.id.emptyStateContentTextView);
            emptyStateButton = (TextView) view.findViewById(R.id.emptyStateButton);
            //Set empty state image width and height 设置空视图 显示的图片宽高
            //            emptyStateImageView.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
            //            emptyStateImageView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            //            emptyStateImageView.requestLayout();

            //设置空视图文字大小和颜色
            emptyStateTitleTextView.setTextSize(emptyStateTitleTextSize);
            emptyStateContentTextView.setTextSize(emptyStateContentTextSize);
            emptyStateTitleTextView.setTextColor(emptyStateTitleTextColor);
            emptyStateContentTextView.setTextColor(emptyStateContentTextColor);
            emptyStateButton.setTextColor(emptyStateButtonTextColor);
            //Set background color if not TRANSPARENT 设置空视图背景颜色
            if (emptyStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(emptyStateBackgroundColor);
            }

            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            //            layoutParams.addRule(CENTER_IN_PARENT);

            addView(emptyStateLinearLayout, layoutParams);
        }

    }

    /**
     * 设置错误视图
     */
    private void setErrorView() {
        if (errorStateLinearLayout == null) {
            view = inflater.inflate(R.layout.state_error_view, null);
            errorStateLinearLayout = (LinearLayout) view.findViewById(R.id.errorViewLinearLayout);
            errorStateLinearLayout.setTag(TAG_ERROR);

            errorStateImageView = (ImageView) view.findViewById(R.id.errorStateImageView);
            errorStateTitleTextView = (TextView) view.findViewById(R.id.errorStateTitleTextView);
            errorStateContentTextView = (TextView) view.findViewById(R.id.errorStateContentTextView);
            errorStateButtonLeft = (TextView) view.findViewById(R.id.errorStateButtonLeft);
            errorStateButtonRight = (TextView) view.findViewById(R.id.errorStateButtonRight);
            //Set error state image width and height 设置错误视图  图片宽高
            //            errorStateImageView.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
            //            errorStateImageView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            //            errorStateImageView.requestLayout();

            //设置错误视图文字大小和颜色
            errorStateTitleTextView.setTextSize(errorStateTitleTextSize);
            errorStateContentTextView.setTextSize(errorStateContentTextSize);
            errorStateTitleTextView.setTextColor(errorStateTitleTextColor);
            errorStateContentTextView.setTextColor(errorStateContentTextColor);
            errorStateButtonLeft.setTextColor(errorStateButtonTextColor);
            errorStateButtonRight.setTextColor(errorStateButtonTextColor);
            //Set background color if not TRANSPARENT 设置错误视图背景颜色
            if (errorStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(errorStateBackgroundColor);
            }

            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            //            layoutParams.addRule(CENTER_IN_PARENT);

            addView(errorStateLinearLayout, layoutParams);
        }
    }

    /**
     * 隐藏加载视图
     */
    public void hideLoadingView() {
        if (loadingStateLinearLayout != null) {
            loadingStateLinearLayout.setVisibility(GONE);
            //Restore the background color if not TRANSPARENT
            if (loadingStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundDrawable(currentBackground);
            }
        }

        if (loadingTextView != null && loadingTextView.isFlipping()) {
            loadingTextView.stop();
        }
    }

    /**
     * 隐藏空的视图
     */
    public void hideEmptyView() {
        if (emptyStateLinearLayout != null) {
            emptyStateLinearLayout.setVisibility(GONE);

            //Restore the background color if not TRANSPARENT
            if (emptyStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundDrawable(currentBackground);
            }
        }
    }

    /**
     * 隐藏错误的视图
     */
    public void hideErrorView() {
        if (errorStateLinearLayout != null) {
            errorStateLinearLayout.setVisibility(GONE);

            //Restore the background color if not TRANSPARENT
            if (errorStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundDrawable(currentBackground);
            }

        }
    }

    public void hideContentView() {
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }


    public void showErrorView() {
        if (errorStateLinearLayout != null) {
            errorStateLinearLayout.setVisibility(VISIBLE);
        }
    }


    private void showEmptyView() {
        if (emptyStateLinearLayout != null) {
            emptyStateLinearLayout.setVisibility(VISIBLE);

        }
    }

    private void showLoadingView() {
        if (loadingStateLinearLayout != null) {
            loadingStateLinearLayout.setVisibility(VISIBLE);

        }

        if (loadingTextView != null && !loadingTextView.isFlipping()) {
            loadingTextView.start();
        }
    }


    /**
     * 显示内容视图
     *
     * @param loadingView
     * @param contentView
     * @param isShowAnim
     */
    private void showContent(final View loadingView, final View contentView, boolean isShowAnim) {
        if (isShowAnim) {
            final Resources resources = loadingView.getResources();
            final int translateInPixels = resources.getDimensionPixelSize(R.dimen.c_animation_translate_y);
            // Not visible yet, so animate the view in
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator contentFadeIn = ObjectAnimator.ofFloat(contentView, View.ALPHA, 0f, 1f);
            ObjectAnimator contentTranslateIn = ObjectAnimator.ofFloat(contentView, View.TRANSLATION_Y,
                    translateInPixels, 0);

            ObjectAnimator loadingFadeOut = ObjectAnimator.ofFloat(loadingView, View.ALPHA, 1f, 0f);
            ObjectAnimator loadingTranslateOut = ObjectAnimator.ofFloat(loadingView, View.TRANSLATION_Y, 0,
                    -translateInPixels);

            set.playTogether(contentFadeIn, contentTranslateIn, loadingFadeOut, loadingTranslateOut);
            set.setDuration(resources.getInteger(R.integer.c_show_animation_time));

            set.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    contentView.setTranslationY(0);
                    loadingView.setTranslationY(0);
                    contentView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loadingView.setVisibility(View.GONE);
                    loadingView.setAlpha(1f); // For future showLoading calls
                    contentView.setTranslationY(0);
                    loadingView.setTranslationY(0);
                }
            });

            set.start();
        } else {
            loadingView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 动画显示错误 空视图
     *
     * @param loadingView
     * @param eView
     * @param isShowAnim
     */
    private void showEmptyErrorView(final View loadingView, final View eView, boolean isShowAnim) {
        if (isShowAnim) {
            final Resources resources = loadingView.getResources();
            // Not visible yet, so animate the view in
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator in = ObjectAnimator.ofFloat(eView, View.ALPHA, 1f);
            ObjectAnimator loadingOut = ObjectAnimator.ofFloat(loadingView, View.ALPHA, 0f);

            set.playTogether(in, loadingOut);
            set.setDuration(resources.getInteger(R.integer.e_show_animation_time));

            set.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    eView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    loadingView.setVisibility(View.GONE);
                    loadingView.setAlpha(1f);
                }
            });
            set.start();
        } else {
            eView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }

    }

    //        public MaterialProgressBar getLoadingStateProgressBar() {
    //            return loadingStateProgressBar;
    //        }

    public LinearLayout getLoadingStateLinearLayout() {
        return loadingStateLinearLayout;
    }

    public LinearLayout getEmptyStateLinearLayout() {
        return emptyStateLinearLayout;
    }

    public LinearLayout getErrorStateLinearLayout() {
        return errorStateLinearLayout;
    }

    public ImageView getLoadingView() {
        return mLoadingView;
    }
}
