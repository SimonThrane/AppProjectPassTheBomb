package com.thrane.simon.passthebomb;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thrane.simon.passthebomb.Util.CalibrationHelper;

public class CalibrateActivity extends AppCompatActivity {

    TextView descriptionTxt;
    ImageView avatarImg;
    Button doneBtn;
    private SensorManager sensorManager;
    private Sensor magSensor;
    private CalibrationHelper calibrationHelper;
    private Sensor accSensor;
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        calibrationHelper = new CalibrationHelper();

        descriptionTxt = findViewById(R.id.calibrateDescriptiveTxt);
        avatarImg = findViewById(R.id.calibrateAvatarImg);
        doneBtn = findViewById(R.id.calibrateDoneBtn);

        sensorManager = (SensorManager)getSystemService(this.SENSOR_SERVICE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });

        fetchPlayers();
    }

    private void done() {
        sensorManager.getRotationMatrix(rotationMatrix, null,
                calibrationHelper.accelerometerReading, calibrationHelper.magnetometerReading);

        sensorManager.getOrientation(rotationMatrix, orientationAngles);

        Log.d("TAG", "alpha: " +  orientationAngles[0] + " beta: " + orientationAngles[1] + " gamma: " + orientationAngles[2]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(calibrationHelper, magSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(calibrationHelper, accSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void fetchPlayers() {

    }
}
