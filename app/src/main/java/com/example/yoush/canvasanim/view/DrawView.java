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

        mRectangle1 = createF(mContext, 500, 500, 7, 7, 200);
        mRectangle2 = createF(mContext, 500, 500, 1, 1, 200);
        mRectangle3 = createF(mContext, 200, 200, 5, 1, 200);

        mRectangle4 = createF(mContext, 567, 345, 7, 7, 200);
        mRectangle5 = createF(mContext, 600, 600, 1, 1, 200);
        mRectangle6 = createF(mContext, 700, 700, 5, 1, 200);

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

        if (test > 6){
            test = 0;
            Log.e(TAG, "setBytes: " );
        }
        dataReceivedImpl(mBytes, 4);

        invalidate();

        if (true) {
            return;
        }

        if (mBytes == null)
            return;
        /*max = 0;
        min = 0;*/
        // 每秒 2 次， 每次 128 个数据取相隔两个数据，做运算，存到 64 个数据的数组
        for (int i = 0; i < mBytes.length; i++) {
            /*byte rfk = mBytes[2 * i];
            byte ifk = mBytes[2 * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (10 * Math.log10(magnitude));*/
////            mMusicByte[i] = (dbValue * 2 - 10);
            max = max > Math.abs(mBytes[i]) ? max : Math.abs(mBytes[i]);
            min = min < Math.abs(mBytes[i]) ? min : Math.abs(mBytes[i]);
//            max = max > mBytes[i] ? max : mBytes[i];
//            min = min < mBytes[i] ? min : mBytes[i];
        }


        Log.e(TAG, "setBytes: **************************************************");


        /*int sum = 0;
        for (int i = 0; i < mBytes.length; i++) {
            sum += Math.abs(mBytes[i]);
            Log.e(TAG, "calculateDecibel: " + buf[i]);
        }
        // avg 10-50
        return sum / mBufSize;*/

        int all = 0;
/*        for (int b : mBytes) {
            all += Math.abs(b);
        }
        mAverage = all / mBytes.length;*/
//        Log.e(TAG, "setBytes: " + all / mBytes.length);
        all = 0;
        for (int b : mBytes) {
            all += b;
        }
        mAverage = all / mBytes.length;
//        Log.e(TAG, "setBytes: " + all / mBytes.length);
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
        return mRectangle;
    }

    @Override
    protected void onDraw(Canvas canvas) {


        if (mBytes == null) {
            return;
        }
       /* int change = 0;
        //  每次 64 个数据，取两次数据的差值
        if (test > 0 && test < mMusicByte.length) {
            change = mMusicByte[test] - mMusicByte[test - 1];
        }

        Log.e(TAG, "onDraw: " + test + "  " + change);
*/

        if (isRunning) {
            invalidate();

/*            float k = Math.abs((mAverage - min) / (max - min));
            int ratio = Math.random() > 0.5 ? 1 : 1;
            float speed1 = (ratio * (80 * k));
            float speed2 = (ratio * (20 * k));
            float speed3 = (ratio * (40 * k));*/
            float speed =  dbmArray[0] * 5 + 1;

            speed = test < 3 ? -speed : speed;

            mRectangle1.setDegreeSpeed(3 * (speed / Math.abs(speed)));
            mRectangle1.setSpeedX(speed);
            mRectangle1.setSpeedY(speed);

               /* mRectangle2.setSpeedX(speed2);
                mRectangle2.setSpeedY(speed2);

                mRectangle3.setSpeedX(speed3);
                mRectangle3.setSpeedY(speed3);


                mRectangle4.setSpeedX(speed3);
                mRectangle4.setSpeedY(speed3);


                mRectangle5.setSpeedX(speed3);
                mRectangle5.setSpeedY(speed3);


                mRectangle6.setSpeedX(speed3);
                mRectangle6.setSpeedY(speed3);*/
//            Log.e(TAG, "onDraw: " + "speed1: " + speed + "  dbmArray: " + dbmArray[0] + "  test: " + test + "  " + (speed / Math.abs(speed)) +  "  " + mRectangle1.getDegreeSpeed());

            mRectangle1.move();
            mRectangle1.draw(canvas);


          /*  mRectangle2.move();
            mRectangle2.draw(canvas);

            mRectangle3.move();
            mRectangle3.draw(canvas);
            mRectangle4.move();
            mRectangle4.draw(canvas);
            mRectangle5.move();
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
