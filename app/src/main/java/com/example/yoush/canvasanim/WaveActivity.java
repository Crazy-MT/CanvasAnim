package com.example.yoush.canvasanim;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class WaveActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0x255;
    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;

    private WaveView mWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);
        mWaveView = (WaveView) findViewById(R.id.id_wave);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initVisualizer();
        }
    }

    private void initVisualizer() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.starsky);
        prepareVisualizer();

//        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(true);
//                mMediaPlayer.start();
            }
        });
    }

    private static final String TAG = "MainActivity";
    private void prepareVisualizer() {
//        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer = new Visualizer(0);
        mVisualizer.setEnabled(false);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
//        mVisualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);

        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        /*if (SystemClock.uptimeMillis() - mFPSTime > 1000) {
                            mFPSTime = SystemClock.uptimeMillis();
                            mFPS = mFPSCounter;
                            mFPSCounter = 0;
                        } else {
                            mFPSCounter++;
                        }*/
//                        mDrawView.setBytes(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {

//                        mDrawView.setBytes(bytes);
                        mWaveView.setBytes(bytes);
                    }
                }, Visualizer.getMaxCaptureRate() / 10 , false, true);
        mVisualizer.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( mMediaPlayer != null) {
            mVisualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer == null){
            initVisualizer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initVisualizer();
        } else {
            Toast.makeText(WaveActivity.this, "Allow Permission from settings", Toast.LENGTH_LONG).show();
        }
    }
}
