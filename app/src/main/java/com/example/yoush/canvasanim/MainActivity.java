package com.example.yoush.canvasanim;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.yoush.canvasanim.view.DrawView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0x255;
    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    private DrawView mDrawView;

    private double min = 0;
    private double max = 0;
    private int    mFPS = 0;         // the value to show
    private int    mFPSCounter = 0;  // the value to count
    private long   mFPSTime = 0;     // last update time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawView = (DrawView) findViewById(R.id.draw);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initVisualizer();
        }
    }

    private void initVisualizer() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.starsky);
        prepareVisualizer();

        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(true);
                mMediaPlayer.start();
            }
        });
    }

    private static final String TAG = "MainActivity";
    private void prepareVisualizer() {
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
//        mVisualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);

        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        // TODO: 2017/8/11 声音文件
                        /*for (int b : bytes){
                            min = min < b ? min : b;
                            max = max > b ? max : b;
                            Log.e(TAG, "onWaveFormDataCapture: " + b + "  " + min + "  " + max);
                        }*/

                        if (SystemClock.uptimeMillis() - mFPSTime > 1000) {
                            mFPSTime = SystemClock.uptimeMillis();
                            mFPS = mFPSCounter;
                            mFPSCounter = 0;
                        } else {
                            mFPSCounter++;
                        }

                        for (int b : bytes){
//                            Log.e(TAG, "onWaveFormDataCapture: " + b);
                        }
                        // 128 , 每秒 2 次采样，长度 128
                        //Log.e(TAG, "onWaveFormDataCapture: " + mFPS + "  " + bytes.length  );
                        
                        mDrawView.setBytes(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 10, true, true);
        mVisualizer.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initVisualizer();
        } else {
            Toast.makeText(MainActivity.this, "Allow Permission from settings", Toast.LENGTH_LONG).show();
        }
    }
}
