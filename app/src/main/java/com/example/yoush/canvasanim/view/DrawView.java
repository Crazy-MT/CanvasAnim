package com.example.yoush.canvasanim.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Random;


/**
 * Created by yoush on 2017/8/10.
 */

public class DrawView extends View {

    private static final String TAG = "DrawView";

    private Rectangle[] mRectangles = new Rectangle[10];
    public int width;
    public int height;
    private boolean isRunning;

    private Context mContext;

    private byte[] mBytes;

    private int mRandomChange = 0;

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
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = getMeasuredWidth();
                height = getMeasuredHeight();
                setRectangles();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setRectangles() {
        int distanceWidth = width / 10;
        int distanceHeight = height / 10;
        mRectangles[0] = new Rectangle(mContext,DrawView.this, width / 2 + distanceWidth, height / 2, 7, 7, 200, 10);
        mRectangles[1] = new Rectangle(mContext,DrawView.this, width / 2 + distanceWidth * 2, height / 2, 1, 1, 200, -20);
        mRectangles[2] = new Rectangle(mContext,DrawView.this, width / 2 + distanceWidth * 3, height / 2, 5, 1, 200, 30);
        mRectangles[3] = new Rectangle(mContext,DrawView.this, width / 2 + distanceWidth * 4, height / 2, 7, 7, 200, 40);
        mRectangles[4] = new Rectangle(mContext,DrawView.this, width / 2 + distanceWidth * 5, height / 2, 1, 1, 200, -50);
        mRectangles[5] = new Rectangle(mContext,DrawView.this, width / 2 - distanceWidth, height / 2, 5, 1, 200, 60);
        mRectangles[6] = new Rectangle(mContext,DrawView.this, width / 2 - distanceWidth * 2, height / 2, 5, 1, 200, -70);
        mRectangles[7] = new Rectangle(mContext,DrawView.this, width / 2 - distanceWidth * 3, height / 2, 7, 7, 200, 80);
        mRectangles[8] = new Rectangle(mContext,DrawView.this, width / 2 - distanceWidth * 4, height / 2, 1, 1, 200, 90);
        mRectangles[9] = new Rectangle(mContext,DrawView.this, width / 2 - distanceWidth * 5, height / 2, 5, 1, 200, 100);
    }

    private void drawRectangleView(Canvas canvas, int randomChange, int randomCount, float dbm, Rectangle rectangle, float ratio, int degreeSpeed) {
        float speedX = dbm * ratio + 1;
        float speedY = speedX;
        rectangle.setSpeedX(speedX);
        rectangle.setSpeedY(speedY);
        speedX = randomChange < randomCount / 2 ? -speedX : speedX;
        rectangle.setDegreeSpeed(degreeSpeed * (speedX / Math.abs(speedX)));
        rectangle.move();
        rectangle.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBytes == null) {
            return;
        }

        if (isRunning) {
            invalidate();

            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[0], 10, 3);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[1], 10, 1);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[2], 10, 2);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[3], 10, -2);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[4], 10, -1);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[5], 10, 3);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[6], 10, 2);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[7], 10, -1);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[8], 10, -3);
            drawRectangleView(canvas, mRandomChange, mRandomCount, dbmArray[0], mRectangles[9], 10, 2);

        }
        //super.onDraw(canvas);
    }


    public void setBytes(byte[] bytes) {
        mBytes = bytes;

        mRandomChange++;
        if (mRandomChange > mRandomCount) {
            mRandomChange = 0;
            mRandomCount = new Random().nextInt(10);
        }
        dataReceivedImpl(mBytes, 4);

        invalidate();
    }

    private void dataReceivedImpl(byte[] bytes, int layersCount) {
        // calculate dBs
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
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public byte[] getBytes() {
        return mBytes;
    }
}
