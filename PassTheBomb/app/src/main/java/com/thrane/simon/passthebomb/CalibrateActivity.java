package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.thrane.simon.passthebomb.Fragments.LoadingDialogFragment;
import com.thrane.simon.passthebomb.Models.User;
import com.thrane.simon.passthebomb.Util.CalibrationHelper;
import com.thrane.simon.passthebomb.Util.Globals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FirebaseDatabase database;
    private DatabaseReference gameRef;
    private ValueEventListener userListenerOnce;
    private ValueEventListener userListener;
    private ValueEventListener gameListener;
    private String gameId;
    private String phoneUserId;
    private LoadingDialogFragment loadingDialog;
    private FragmentManager fm;
    private int usersInGame = 0;
    private boolean firstUserCheck = true;
    private SharedPreferences mPrefs;
    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        //Set loading dialog
        //Fragment setup
        fm = getSupportFragmentManager();

        //Make getting data ready dialog here
        loadingDialog = LoadingDialogFragment.newInstance();
        loadingDialog.show(fm,"Loading Dialog");

        //Get data from LobbyActivity
        Intent fromLobbyIntent = getIntent();
        //gameId = "-L03qOwiiDLyBVh8EFtI";
        gameId = fromLobbyIntent.getStringExtra(Globals.GAME_KEY);

        mPrefs = getSharedPreferences(null,MODE_PRIVATE);
        phoneUserId  = mPrefs.getString(Globals.USER_ID,null);

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

        database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("Games/" + gameId + "/users");
        userListenerOnce = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchPlayers(dataSnapshot);
                showUser(currentUser);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> firebaseUsers = new ArrayList<>();

                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    firebaseUsers.add(user);
                }

                // if this is the first time we fetch the players, save the number of players
                // if this is NOT the first time, check if the number of players are still the same, and leave if they are not
                if(firstUserCheck) {
                    usersInGame = firebaseUsers.size();
                } else if (firebaseUsers.size() != usersInGame) {
                    finish();
                }

                firstUserCheck = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gameRef.addListenerForSingleValueEvent(userListenerOnce);
        gameRef.addValueEventListener(userListener);

        gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gameRef.addValueEventListener(gameListener);
    }

    private void done() {
        //Get rotation angles based on euler angles: https://stackoverflow.com/questions/8905000/android-axes-vectors-from-orientation-rotational-angles
        //We are only interested in alpha
        sensorManager.getRotationMatrix(rotationMatrix, null,
                calibrationHelper.accelerometerReading, calibrationHelper.magnetometerReading);

        sensorManager.getOrientation(rotationMatrix, orientationAngles);

        //Log all angles
        Log.d("TAG", "alpha: " +  orientationAngles[0] + " beta: " + orientationAngles[1] + " gamma: " + orientationAngles[2]);

        //Save alpha on current user
        currentUser.angleAlpha = orientationAngles[0];

        onAngleUpdated();
    }

    private void onAngleUpdated() {
        //If more users show next
        if(--numberOfUsersNotCalibrated > 0) {
            currentUser = users.get(numberOfUsersNotCalibrated-1);
            showUser(currentUser);
        } else {
            Log.d("DONE CALIBRATING","numberOfUsersNotCalibrated " + numberOfUsersNotCalibrated + ". Now starting game...");
            Intent gameIntent = new Intent(this, GameActivity.class);
            gameIntent.putExtra(Globals.GAME_KEY,gameId);
            gameIntent.putParcelableArrayListExtra(Globals.CALIBRATED_USERS, users);
            startActivity(gameIntent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameRef.removeEventListener(userListener);
        gameRef.removeEventListener(gameListener);
        gameRef.removeEventListener(userListenerOnce);
        Log.d("CalibrateActivity", "onStop");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // if back is pressed, destroy the game, we'll start over
        database.getReference("Games/" + gameId).removeValue();

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

    private void fetchPlayers(DataSnapshot dataSnapshot) {
        ArrayList<User> firebaseUsers = new ArrayList<>();

        for(DataSnapshot snap : dataSnapshot.getChildren()) {
            User user = snap.getValue(User.class);
            user.firebaseId= snap.getKey();
            firebaseUsers.add(user);

            if(user.id.equals(phoneUserId)) {
                // save the key to our self, so we can be removed from the game in case we close the app
                userKey = snap.getKey();
            }
        }

        users = firebaseUsers;

        totalNumberOfUsers = users.size();
        numberOfUsersNotCalibrated = totalNumberOfUsers;
        //Start from top to bottom
        currentUser = users.get(totalNumberOfUsers-1);
    }

    private void showUser(User user) {
        if(!phoneUserId.equals(user.id)){
            String descriptionString = getResources().getString(R.string.calibrate_activity_description) + " " + user.name;
            descriptionTxt.setText(descriptionString);
            Glide.with(this).load(user.photoUri).into(avatarImg);
        }else{
            onAngleUpdated();
        }

        //Set image with user image
    }
}
