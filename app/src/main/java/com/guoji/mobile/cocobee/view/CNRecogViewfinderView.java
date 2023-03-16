package com.guoji.mobile.cocobee.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.guoji.mobile.cocobee.R;
import com.yunmai.cc.smart.eye.util.DisplayUtil;
import com.yunmai.cc.smart.eye.util.UtilApp;

/**
 * 车牌识别自定义view
 * Created by Administrator on 2017/4/1.
 */
public class CNRecogViewfinderView extends View{

    private Paint paint;
    private Context mContext;
    private float lineLeft,lineRight,lineTop,lineBottom;
    private Handler mHandler;
    private Point oriPoint = new Point();//坐标的原点
    private float mWidth = DisplayUtil.dip2px(getContext(), 60);
    private float mHeight = mWidth/2;
    private float maxWidth = 0;//最大长度
    private float maxHeight = 0;//最大高度
    private float minWidth = 0;//最小长度
    private float minHeight = 0;//最小高度
    private SharedPreferences preferences;


    public float getLineRight() {
        return lineRight;
    }


    public void setLineRight(float lineRight) {
        this.lineRight = lineRight;
    }


    public float getLineTop() {
        return lineTop;
    }


    public void setLineTop(float lineTop) {
        this.lineTop = lineTop;
    }

    public float getmWidth() {
        return mWidth;
    }


    public float getmHeight() {
        return mHeight;
    }

    public CNRecogViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initFinder();
    }

    private void initFinder(){
        preferences = getContext().getSharedPreferences("CC_EYE", 0);
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        oriPoint.x = screenWidth/8 ;
        oriPoint.y = screenHeight - screenHeight/5;

        paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.finder_1));
        paint.setStrokeWidth(4);
        lineLeft = oriPoint.x;
        lineRight = oriPoint.x + mWidth;
        lineTop = oriPoint.y - mHeight;
        lineBottom =oriPoint.y;
        lineRight = preferences.getFloat("WIDTH", lineRight);//这个值主要是获取之前保存的宽度进行显示

        lineTop = preferences.getFloat("HEIGHT", lineTop);////这个值主要是获取之前保存的高度进行显示

        maxWidth = screenWidth*3/4;
        minWidth = mWidth+lineLeft;

        minHeight = oriPoint.y - mHeight;
        maxHeight = screenHeight/4;

    }


    /**
     * 取景器在屏幕上的位置
     * @return
     */
    public Rect getFinder(){
        return new Rect((int)(lineLeft), (int)lineTop, (int)lineRight, (int)lineBottom);
    }

    public void setHandler(Handler mHandler){
        this.mHandler = mHandler;
    }

    public void setRight(float distance){
        float tempLength = lineRight + distance;
        if(tempLength <= maxWidth){
            lineRight = lineRight + distance;
        }
    }


    public void setLeft(float distacnce){
        float tempLength = lineRight - distacnce;
        if(tempLength >= minWidth){
            lineRight = tempLength;
        }
    }

    public void setUp(float distacnce){
        float tempLength = lineTop-distacnce;
        if(tempLength >= maxHeight){
            lineTop = tempLength;
        }
    }

    public void setDown(float distacnce){
        float tempLength = lineTop + distacnce;
        if(tempLength <= minHeight){
            lineTop = tempLength;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //十字焦点
        canvas.drawLine(oriPoint.x + (lineRight -lineLeft)/2 - 12, oriPoint.y - (lineBottom-lineTop)/2, oriPoint.x + (lineRight -lineLeft)/2 + 12,oriPoint.y - (lineBottom-lineTop)/2, paint);
        canvas.drawLine(oriPoint.x + (lineRight -lineLeft)/2, oriPoint.y - (lineBottom-lineTop)/2 - 12, oriPoint.x + (lineRight -lineLeft)/2, oriPoint.y - (lineBottom-lineTop)/2 + 12, paint);

        //取景器
        canvas.drawLine(lineLeft, lineTop, lineLeft, lineTop + 20, paint);
        canvas.drawLine(lineLeft, lineTop, lineLeft + 20, lineTop, paint);

        canvas.drawLine(lineRight, lineTop, lineRight, lineTop + 20, paint);
        canvas.drawLine(lineRight, lineTop, lineRight - 20, lineTop, paint);
        canvas.drawLine(lineLeft, lineBottom, lineLeft, lineBottom - 20, paint);
        canvas.drawLine(lineLeft, lineBottom, lineLeft + 20, lineBottom, paint);

        canvas.drawLine(lineRight, lineBottom, lineRight - 20, lineBottom, paint);
        canvas.drawLine(lineRight, lineBottom, lineRight, lineBottom - 20, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mHandler != null){
                    mHandler.sendEmptyMessage(UtilApp.ONLY_AUTOFOCUS);
                }
                break;
        }
        return true;
    }


}
