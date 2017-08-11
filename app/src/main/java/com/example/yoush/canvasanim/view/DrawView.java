package com.example.yoush.canvasanim.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by yoush on 2017/8/10.
 */

public class DrawView extends View {

    private static final String TAG = "DrawView";
    private Rectangle mRectangle1;
    private Rectangle mRectangle2;
    private Rectangle mRectangle3;
    public int width;
    public int height;
    private boolean isRunning;

    private Context mContext;

    private byte[] mBytes;
    private int[] mMusicByte = new int[]{};

    private Paint mPaint;
    private int test = 0;
    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        isRunning = true;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 消除锯齿
        mPaint.setStrokeWidth(10f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        mRectangle1 = createF(mContext, 0, 0, 3, 3, 200);
        mRectangle2 = createF(mContext, 0, 0, 1, 1, 200);
        mRectangle3 = createF(mContext, 0, 200, 5, 1, 200);

        mRectangle1.setARGB(255,121,121,121);
        mRectangle2.setARGB(255,121,121,121);
        mRectangle3.setARGB(255,121,121,121);
        mRectangle1.setDegree(30);
        mRectangle2.setDegree(10);
        mRectangle3.setDegree(60);
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public void setBytes(byte[] bytes) {
        mBytes = bytes;

        if (mBytes == null)
            return;

        mMusicByte = new int[mBytes.length / 2];
        for (int i = 0; i < mBytes.length / 2; i++) {
            byte rfk = mBytes[2 * i];
            byte ifk = mBytes[2 * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (10 * Math.log10(magnitude));
            mMusicByte[i] = (dbValue * 2 - 10); 
        }
        Log.e(TAG, "setBytes: **************************************************" );

        test = 0;
       /* int all = 0;
        for (int b : mBytes){
            all += b;
        }

        mMusicByte = all / mBytes.length;
        Log.e(TAG, "setBytes: " + mMusicByte);
*/
        invalidate();
    }


    private Rectangle createF(Context context, int x, int y, int speedX, int speedY, int length) {
        Rectangle mRectangle = new Rectangle(context, this);

        mRectangle.setX(x);
        mRectangle.setY(y);
        mRectangle.setLength(length);
        mRectangle.setSpeedX(speedX);
        mRectangle.setSpeedY(speedY);
        return mRectangle;
    }

    @Override
    protected void onDraw(Canvas canvas) {



        if (mBytes == null){
            return;
        }
        test++;
        int change = 0;
        if (test > 0 && test < mMusicByte.length) {
             change = mMusicByte[test] - mMusicByte[test - 1];
        }

        Log.e(TAG, "onDraw: " + test + "  " + change);
        if (isRunning) {
            invalidate();

//            mRectangle1.setSpeedX(change);
            mRectangle1.move();
            mRectangle1.draw(canvas);
            /*mRectangle1.move();
            mRectangle1.draw(canvas);

            mRectangle2.move();
            mRectangle2.draw(canvas);

            mRectangle3.move();
            mRectangle3.draw(canvas);*/
        }



        // postInvalidate() 子线程请求调用 onDraw() ，系统自动调用，不允许手动调用。
        // invalidate(); 主线程请求调用 onDraw()
        //super.onDraw(canvas);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }


}
