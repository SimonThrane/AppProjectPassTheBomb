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

import com.thrane.simon.passthebomb.Models.User;
import com.thrane.simon.passthebomb.Util.CalibrationHelper;

import java.util.ArrayList;
import java.util.List;

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
    private int totalNumberOfUsers;
    private int numberOfUsersNotCalibrated;
    private ArrayList<User> users;
    private User currentUser;

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

    private ArrayList<User> getUsers() {
        //Mock users
        ArrayList<User> list = new ArrayList<>();
        User user1 = new User();
        user1.id = "1";
        user1.name = "Kasper";
        User user2 = new User();
        user2.id = "1";
        user1.name = "Jeppe";
        User user3 = new User();
        user3.id = "1";
        user1.name = "Simon";
        list.add(user1);
        list.add(user2);
        list.add(user3);
        return list;
    }

    private void done() {
        //Get rotation angles based on euler angles: https://stackoverflow.com/questions/8905000/android-axes-vectors-from-orientation-rotational-angles
        //We are only interested in alpha
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

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(calibrationHelper);
    }

    private void fetchPlayers() {
        users = getUsers();

        totalNumberOfUsers = users.size();
        numberOfUsersNotCalibrated = totalNumberOfUsers;

    }

    private void showUser(User user) {
        currentUser = user;
        descriptionTxt.setText(R.string.calibrate_activity_description + " " + user.name);
        //Set image with user image
    }
}
