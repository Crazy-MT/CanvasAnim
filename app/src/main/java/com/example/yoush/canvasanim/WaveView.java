package com.example.yoush.canvasanim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.yoush.canvasanim.view.Utils;

import java.util.ArrayList;

/**
 * Created by Tag on 2017/9/4.
 */

public class WaveView extends View {

    private Paint mPaintCircle;
    private Paint mPaintLine;
    private Paint mPaintCenter;
    private Paint mPaint;// 画笔
    private static final String TAG = "WaveView";
    private byte[] mBytes;

    private int mLineOff = 42;//上下边距的距离
    float[] dbs;
    int mDivider = 3;
    private ArrayList<Float> mDBValue = new ArrayList<>();

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaintCircle = new Paint();
        mPaintCircle.setColor(Color.rgb(246, 131, 126));
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.rgb(169, 169, 169));

        mPaintCenter = new Paint();
        mPaintCenter.setColor(Color.rgb(39, 199, 175));// 画笔为color
        mPaintCenter.setStrokeWidth(1);// 设置画笔粗细
        mPaintCenter.setAntiAlias(true);
        mPaintCenter.setFilterBitmap(true);
        mPaintCenter.setStyle(Paint.Style.FILL);

        mPaint = new Paint();
        mPaint.setColor(Color.rgb(39, 199, 175));// 画笔为color
        mPaint.setStrokeWidth(1);// 设置画笔粗细
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
    }


    public void setBytes(byte[] bytes) {
        mBytes = bytes;
        dataReceivedImpl(mBytes);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDBValue == null) {
            return;
        }

        canvas.drawARGB(255, 239, 239, 239);
        canvas.drawLine(0, mLineOff / 2, getWidth(), mLineOff / 2, mPaintLine);//最上面的那根线
        canvas.drawLine(0, (getHeight() - mLineOff) * 0.5f + mLineOff / 2, getWidth(), (getHeight() - mLineOff) * 0.5f + mLineOff / 2, mPaintCenter);//中心线
        canvas.drawLine(0, getHeight() - mLineOff / 2, getWidth(), getHeight() - mLineOff / 2, mPaintLine);//最下面的那根线

        int timeLineX = mDBValue.size() * mDivider;

        if (timeLineX > getWidth()) {
            timeLineX = getWidth() - mLineOff;
        }
        canvas.drawCircle(timeLineX, mLineOff / 2, mLineOff / 4, mPaintCircle);// 上圆
        canvas.drawCircle(timeLineX, getHeight() - mLineOff / 2, mLineOff / 4, mPaintCircle);// 下圆
        canvas.drawLine(timeLineX, mLineOff / 2, timeLineX, getHeight() - mLineOff / 2, mPaintCircle);//垂直的线
        for (int i = 0; i < mDBValue.size(); i++) {
            int x = i * mDivider;
            Float y = mDBValue.get(i);
            canvas.drawLine(x, getHeight() / 2 - y, x, getHeight() / 2 + y, mPaint);
        }
    }

    private void dataReceivedImpl(byte[] bytes) {
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

        mDBValue.add(dbs[0] * 2);
        if (mDBValue.size() * mDivider > getWidth()) {
            mDBValue.remove(0);
        }
    }
}
