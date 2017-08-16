package com.example.yoush.canvasanim.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;


/**
 * Created by yoush on 2017/8/10.
 */

public class DrawView extends View {

    private static final String TAG = "DrawView";
    private Rectangle mRectangle1;
    private Rectangle mRectangle2;
    private Rectangle mRectangle3;
    private Rectangle mRectangle4;
    private Rectangle mRectangle5;
    private Rectangle mRectangle6;
    public int width;
    public int height;
    private boolean isRunning;
    private float mAverage = 0;

    private Context mContext;

    private byte[] mBytes;

    private int max = 0, min = 0;

    private Paint mPaint;
    private int test = 0;

    private float[] dbmArray = new float[4];

    float[] dbs;
    /**
     * Maximum value of dB. Used for controlling wave height percentage.
     */
    private static final float MAX_DB_VALUE = 76;
    private final float[] coefficients = new float[]{
            80 / 44100f,
            350 / 44100f,
            2500 / 44100f,
            10000 / 44100f,
    };

    private int mRandomCount = new Random().nextInt(10);

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

        mRectangle1 = createF(mContext, width / 2, height / 2, 7, 7, 200);
        mRectangle2 = createF(mContext, width / 2 + 400, height / 2, 1, 1, 200);
        mRectangle3 = createF(mContext, width / 2 - 200, height / 2, 5, 1, 200);

        mRectangle4 = createF(mContext, width / 2 - 400, height / 2, 7, 7, 200);
        mRectangle5 = createF(mContext, width / 2 + 200, height / 2, 1, 1, 200);
        mRectangle6 = createF(mContext, width / 2 - 400, height / 2, 5, 1, 200);

        mRectangle1.setARGB(255, 121, 121, 121);
        mRectangle2.setARGB(255, 121, 121, 121);
        mRectangle3.setARGB(255, 121, 121, 121);
        mRectangle4.setARGB(255, 121, 121, 121);
        mRectangle5.setARGB(255, 121, 121, 121);
        mRectangle6.setARGB(255, 121, 121, 121);

        mRectangle1.setDegree(30);
        mRectangle2.setDegree(10);
        mRectangle3.setDegree(60);
        mRectangle4.setDegree(30);
        mRectangle5.setDegree(10);
        mRectangle6.setDegree(60);


        mRectangle1.setDegreeSpeed(3);
        mRectangle2.setDegreeSpeed(-1);
        mRectangle3.setDegreeSpeed(-2);
        mRectangle4.setDegreeSpeed(-3);
        mRectangle5.setDegreeSpeed(-2);
        mRectangle6.setDegreeSpeed(-1);
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public void setBytes(byte[] bytes) {
        mBytes = bytes;

        test++;
        if (test > mRandomCount){
            test = 0;
            mRandomCount = new Random().nextInt(10);
        }
        dataReceivedImpl(mBytes, 4);

        invalidate();
    }

    private void dataReceivedImpl(byte[] bytes, int layersCount) {


        // calculate dBs and amplitudes
        int dataSize = bytes.length / 2 - 1;
        if (dbs == null || dbs.length != dataSize) {
            dbs = new float[dataSize];
        }

        for (int i = 0; i < dataSize; i++) {
            float re = bytes[2 * i];
            float im = bytes[2 * i + 1];
            float sqMag = re * re + im * im;
            dbs[i] = Utils.magnitudeToDb(sqMag);
        }
        for (int i = 0; i < layersCount; i++) {
            int index = (int) (coefficients[i] * bytes.length);
            float db = dbs[index];
            dbmArray[i] = db / MAX_DB_VALUE;
        }

        Log.e(TAG, "dataReceivedImpl: " + dbmArray[0]);
    }


    private Rectangle createF(Context context, int x, int y, int speedX, int speedY, int length) {
        Rectangle mRectangle = new Rectangle(context, this);

        mRectangle.setX(x);
        mRectangle.setY(y);
        mRectangle.setLength(length);
        mRectangle.setSpeedX(speedX);
        mRectangle.setSpeedY(speedY);
        mRectangle.setOriginX(x);
        mRectangle.setOriginY(y);
        return mRectangle;
    }

    @Override
    protected void onDraw(Canvas canvas) {


        if (mBytes == null) {
            return;
        }

        if (isRunning) {
            invalidate();

            float speed1 =  dbmArray[0] * 10 + 1;
            mRectangle1.setSpeedX(speed1);
            mRectangle1.setSpeedY(speed1);
            speed1 = test < mRandomCount/2 ? -speed1 : speed1;
            mRectangle1.setDegreeSpeed(3 * (speed1 / Math.abs(speed1)));
            mRectangle1.move();
            mRectangle1.draw(canvas);
            Log.e(TAG, "onDraw: " + speed1);

            float speed2 = dbmArray[0] * 5 + 1;
            mRectangle2.setSpeedX(speed2);
            mRectangle2.setSpeedY(speed2);
            speed2 = test < mRandomCount / 2 ? -speed2 : speed2;
            mRectangle2.setDegreeSpeed(1 * (speed2 / Math.abs(speed2)));
            mRectangle2.move();
            mRectangle2.draw(canvas);


            float speed3 = dbmArray[0] * 5 + 1;
            mRectangle3.setSpeedX(speed3);
            mRectangle3.setSpeedY(speed3);
            speed3 = test < mRandomCount / 2 ? -speed3 : speed3;
            mRectangle3.setDegreeSpeed(2 * (speed3 / Math.abs(speed3)));
            mRectangle3.move();
            mRectangle3.draw(canvas);

            float speed4 = dbmArray[0] * 5 + 1;
            mRectangle4.setSpeedX(speed4);
            mRectangle4.setSpeedY(speed4);
            speed4 = test < mRandomCount / 2 ? -speed4 : speed4;
            mRectangle4.setDegreeSpeed(4 * (speed4 / Math.abs(speed4)));
            mRectangle4.move();
            mRectangle4.draw(canvas);

                /*mRectangle5.setSpeedX(speed3);
                mRectangle5.setSpeedY(speed3);


                mRectangle6.setSpeedX(speed3);
                mRectangle6.setSpeedY(speed3);*/
//            Log.e(TAG, "onDraw: " + "speed1: " + speed1 + "speed2: " + speed2+ "speed3: " + speed3+ "speed4: " + speed4 + "  dbmArray: " + dbmArray[0] + "  test: " + test + "  " + (speed1 / Math.abs(speed1)) +  "  " + mRectangle1.getDegreeSpeed());





            /* mRectangle5.move();
            mRectangle5.draw(canvas);
            mRectangle6.move();
            mRectangle6.draw(canvas);*/
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
