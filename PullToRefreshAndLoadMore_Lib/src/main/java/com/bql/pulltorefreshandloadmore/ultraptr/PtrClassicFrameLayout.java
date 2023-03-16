package com.bql.pulltorefreshandloadmore.ultraptr;

import android.content.Context;
import android.util.AttributeSet;

public class PtrClassicFrameLayout extends PtrFrameLayout {

    //    private PtrClassicDefaultHeader mPtrClassicHeader;
    private PtrClassicLeHeader mPtrClassicLeHeader;

    public PtrClassicFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        //        mPtrClassicHeader = new PtrClassicDefaultHeader(getContext());
        mPtrClassicLeHeader = new PtrClassicLeHeader(getContext());
        setHeaderView(mPtrClassicLeHeader);
        addPtrUIHandler(mPtrClassicLeHeader);
    }

    public PtrClassicLeHeader getHeader() {
        return mPtrClassicLeHeader;
    }

    //    /**
    //     * Specify the last update time by this key string
    //     *
    //     * @param key
    //     */
    //    public void setLastUpdateTimeKey(String key) {
    //        if (mPtrClassicHeader != null) {
    //            mPtrClassicHeader.setLastUpdateTimeKey(key);
    //        }
    //    }

    //    /**
    //     * Using an object to specify the last update time.
    //     *
    //     * @param object
    //     */
    //    public void setLastUpdateTimeRelateObject(Object object) {
    //        if (mPtrClassicHeader != null) {
    //            mPtrClassicHeader.setLastUpdateTimeRelateObject(object);
    //        }
    //    }
}
