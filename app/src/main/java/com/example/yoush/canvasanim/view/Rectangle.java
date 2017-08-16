package com.example.yoush.canvasanim.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by yoush on 2017/8/10.
 */

public class Rectangle extends View {

    private static final String TAG = "Rectangle";
    // 透明度
    private static final int ALPHA = 255;

    // 原始位置
    private float mStartX = 0;
    private float mStartY = 0;

    // 终点位置
    private float mEndX = 0;
    private float mEndY = 0;

    // 长度
    private int mLength = 100;

    // 运动速度
    private float mSpeedX = 3;
    private float mSpeedY = 3;

    // 旋转速度
    private float mDegreeSpeed = 1;


    // 角度
    private float mDegree = 0;

    // 方向
    private boolean goRight = true;
    private boolean goDown = true;

    private DrawView mDrawView;
    private Paint mPaint;

    private float mOriginX;
    private float mOriginY;
    private float mDistance = 200;

    private int mFPS = 0;         // the value to show
    private int mFPSCounter = 0;  // the value to count
    private long mFPSTime = 0;     // last update time

    public Rectangle(Context context, DrawView drawView) {
        super(context);
        mDrawView = drawView;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 消除锯齿
        mPaint.setStrokeWidth(10f);
        mPaint.setARGB(255, 121, 121, 121);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float toX = mStartX;
        float toY = mStartY;

        float[] point = calculatePoint(mDegree, toX, toY);

        mStartX = (int) point[0];
        mStartY = (int) point[1];

        mEndX = (int) point[2];
        mEndY = (int) point[3];
        canvas.drawLine(point[0], point[1], point[2], point[3], mPaint);

       /* if (SystemClock.uptimeMillis() - mFPSTime > 1000) {
            mFPSTime = SystemClock.uptimeMillis();
            mFPS = mFPSCounter;
            mFPSCounter = 0;
        } else {
            mFPSCounter++;
        } */
    }


    public void move() {
        moveTo(mSpeedX, mSpeedY, mDegreeSpeed);
    }

    private void moveTo(float goX, float goY, float degree) {

        int width = (int) (mOriginX + mDistance);
        int height = (int) (mOriginY + mDistance);

        int x = (int) (mOriginX - mDistance);
        int y = (int) (mOriginY - mDistance);

        if (mEndX > width || mStartX > width) {
            goRight = false;
            goX = Math.abs(goX);
        }
        if (mEndX < x || mStartX < x) {
            goRight = true;
            goX = Math.abs(goX);
        }

        if (mEndY > height || mStartY > height) {
            goDown = false;
            goY = Math.abs(goY);
        }
        if (mEndY < y || mStartY < y) {
            goDown = true;
            goY = Math.abs(goY);
        }


        mStartX = goRight ? (mStartX + goX) : (mStartX - goX);
        mStartY = goDown ? (mStartY + goY) : (mStartY - goY);
        if (mDegree >= 360){
            mDegree = 0;
        }
        if (mDegree < -360){
            mDegree = 0;
        }
        mDegree = mDegree + degree;

    }

    /***
     *  根据角度，计算线段起始点坐标位置
     * @param angle
     * @param toX
     * @param toY
     * @return
     */
    private float[] calculatePoint(float angle, float toX, float toY) {
        float[] points = new float[4];
        points[0] = toX;
        points[1] = toY;

//        Log.e(TAG, "calculatePoint: " + angle);
        if (angle < 0) {
            angle = 360 + angle;
//            Log.e(TAG, "calculatePoint: " + angle);
        }

        if (angle <= 90f && angle >= 0) {
            points[2] = (float) Math.sin(angle * Math.PI / 180) * mLength + toX;
            points[3] = -(float) Math.cos(angle * Math.PI / 180) * mLength + toY;
        } else if (angle <= 180f) {

            points[2] = (float) Math.cos((angle - 90) * Math.PI / 180) * mLength + toX;
            points[3] = (float) Math.sin((angle - 90) * Math.PI / 180) * mLength + toY;
        } else if (angle <= 270f) {
            points[2] = -(float) Math.sin((angle - 180) * Math.PI / 180) * mLength + toX;
            points[3] = (float) Math.cos((angle - 180) * Math.PI / 180) * mLength + toY;
        } else if (angle <= 360f) {
            points[2] = -(float) Math.cos((angle - 270) * Math.PI / 180) * mLength + toX;
            points[3] = -(float) Math.sin((angle - 270) * Math.PI / 180) * mLength + toY;
        }
        return points;
    }


    public void setARGB(int a, int r, int g, int b) {
        mPaint.setARGB(a, r, g, b);
    }

    public void setX(float newValue) {
        mStartX = newValue;
    }

    public float getX() {
        return mStartX;
    }

    public void setY(float newValue) {
        mStartY = newValue;
    }

    public float getY() {
        return mStartY;
    }

    public float getSpeedX() {
        return mSpeedX;
    }

    public void setSpeedX(float speedX) {
        mSpeedX = speedX;
    }

    public float getmSpeedY() {
        return mSpeedY;
    }

    public void setSpeedY(float speedY) {
        mSpeedY = speedY;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public int getLength() {
        return mLength;
    }

    public float getDegree() {
        return mDegree;
    }

    public void setDegree(float degree) {
        mDegree = degree;
    }

    public float getDegreeSpeed() {
        return mDegreeSpeed;
    }

    public void setDegreeSpeed(float degreeSpeed) {
        mDegreeSpeed = degreeSpeed;
    }

    public float getOriginX() {
        return mOriginX;
    }

    public void setOriginX(float originX) {
        mOriginX = originX;
    }

    public float getOriginY() {
        return mOriginY;
    }

    public void setOriginY(float originY) {
        mOriginY = originY;
    }
}
