package com.thrane.simon.passthebomb;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thrane.simon.passthebomb.Util.CalibrationHelper;

import com.thrane.simon.passthebomb.Models.User;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    ImageView bombImageView;
    MediaPlayer mediaPlayer;
    TextView txt;


    private SensorManager sensorManager;
    private Sensor magSensor;
    private CalibrationHelper calibrationHelper;
    private Sensor accSensor;
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Set up for Calibration
        calibrationHelper = new CalibrationHelper();
        sensorManager = (SensorManager)getSystemService(this.SENSOR_SERVICE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Setup view
        //Get the users example
        Intent intent = getIntent();
        ArrayList<User> users = intent.getParcelableArrayListExtra("CALIBRATED_USERS");
        //Get the users example

        bombImageView = (ImageView)findViewById(R.id.bombImageView);
        bombImageView.setImageResource(R.drawable.bomb);

        txt = (TextView)findViewById(R.id.testView);


        //Setup listener
        bombImageView.setOnTouchListener(new OnBombTouchListener());

        //Setup Mediaplayer
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.isis_theme_song);
        mediaPlayer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(calibrationHelper, magSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(calibrationHelper, accSensor, SensorManager.SENSOR_DELAY_UI);
    }

    //Release mediaplayer
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();

    }

    //Listing on bomb touch
    public class OnBombTouchListener implements View.OnTouchListener {
        private Context context;

        //Detect gestures when bomb is touched
        private final GestureDetector gdt = new GestureDetector(this.context, new GestureListener());
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            gdt.onTouchEvent(event);
            return true;
        }

        //Listing on gestures and raise events
        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            //called when user fling
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, float velocityY) {

                //Touch start coordinates
                float x1 = e1.getX();
                float y1 = e1.getY();

                //Touch end coordinates
                float x2 = e2.getX();
                float y2 = e2.getY();

                //calculate angle degress, to determine if swipe is legit
                double deltaX = x2 - x1;
                double deltaY = y2 - y1;
                double rad = Math.atan2(deltaY, deltaX);
                double deg = rad * (180 / Math.PI);

                if(deg < -45 && deg> -135 && y2< 0) {

                    //Animation setup
                    final TranslateAnimation animationt1 = new TranslateAnimation(0, 0, bombImageView.getTranslationY(), y2 * 5);
                    animationt1.setDuration(300);
                    bombImageView.startAnimation(animationt1);
                    animationt1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Get the current Aplhe
                            sensorManager.getRotationMatrix(rotationMatrix, null,
                                    calibrationHelper.accelerometerReading, calibrationHelper.magnetometerReading);

                            sensorManager.getOrientation(rotationMatrix, orientationAngles);
                            txt.setText("Aplha: "+orientationAngles[0]);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                return false;
            }
        }
    }
}
