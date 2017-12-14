package com.thrane.simon.passthebomb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.renderscript.Sampler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thrane.simon.passthebomb.Fragments.LoadingDialogFragment;
import com.thrane.simon.passthebomb.Fragments.QuestionDialogFragment;
import com.thrane.simon.passthebomb.Models.Bomb;
import com.thrane.simon.passthebomb.Models.Game;
import com.thrane.simon.passthebomb.Models.Question;
import com.thrane.simon.passthebomb.Services.QuestionService;
import com.thrane.simon.passthebomb.Util.CalibrationHelper;

import com.thrane.simon.passthebomb.Models.User;
import com.thrane.simon.passthebomb.Util.Globals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class GameActivity extends AppCompatActivity implements QuestionDialogFragment.QuestionAnswerListener {

    ImageView bombImageView;
    List<User> calibratedUsers= new ArrayList<>();

    private FirebaseDatabase database;
    private DatabaseReference gameRef;
    private DatabaseReference userRef;
    private DatabaseReference bombRef;

    private SensorManager sensorManager;
    private Sensor magSensor;
    private CalibrationHelper calibrationHelper;
    private Sensor accSensor;
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private User phoneUser;
    private String gameId;
    private Game game;
    private Bomb bomb= new Bomb();
    private User host;
    private FragmentManager fm;
    private BroadcastReceiver questionsReciever;
    private ArrayList<Question> allQuestions;
    private LoadingDialogFragment loadingDialog;
    private String phoneUserId;
    private ValueEventListener gameListener;
    private ValueEventListener userListener;
    private ValueEventListener bombListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Fragment setup
        fm = getSupportFragmentManager();

        //Make getting data ready dialog here
        loadingDialog = LoadingDialogFragment.newInstance();
        loadingDialog.show(fm,"Test");

        //Register reciever
        questionsReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadingDialog.dismiss();
                //Start game when questions is ready
                allQuestions = intent.getParcelableArrayListExtra(Globals.QUESTION_EVENT_DATA);
                gameSetup();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(questionsReciever, new IntentFilter(Globals.QUESTION_EVENT));

        //Start questionService
        Intent questionIntent = new Intent(this, QuestionService.class);
        startService(questionIntent);
    }

    private void gameSetup() {
        //Get Intent from calibration
        Intent intent = getIntent();
        gameId = intent.getStringExtra(Globals.GAME_KEY);
        calibratedUsers = intent.getParcelableArrayListExtra(Globals.CALIBRATED_USERS);
        SharedPreferences mPrefs = getSharedPreferences(null,MODE_PRIVATE);
        phoneUserId  = mPrefs.getString(Globals.USER_ID,"DEFAULT" );
        setPhoneUser();
        //gameId ="-L-koVti07m6lQ9xU3f8";
        database = FirebaseDatabase.getInstance();

        //TODO: Get phoneUser from sharedPrefs

        // get database references
        gameRef = database.getReference("Games/"+gameId);
        bombRef = database.getReference("Games/"+gameId+"/bomb");
        userRef = database.getReference("Games/"+gameId+"/users/"+phoneUser.firebaseId);

        // create listeners and add them to the references
        gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    // game does not exist anymore, finish game activity
                    finish();
                    return;
                }
                Game currentGame = dataSnapshot.getValue(Game.class);
                host = currentGame.host;

                passBombToRandomUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gameRef.addListenerForSingleValueEvent(gameListener);

        bombListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bomb = dataSnapshot.getValue(Bomb.class);
                bombCountdown(bomb);
                bombImageView.setAlpha(1.0f);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO: update phoneUser
                if(!dataSnapshot.exists()) {
                    // users dont exist anymore, which means the game has ended
                    finish();
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                phoneUser.hasBomb = user.hasBomb;

                if(!phoneUser.hasBomb){
                    bombImageView.setAlpha(0.0f);
                }else{
                    bombRef.addListenerForSingleValueEvent(bombListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(userListener);

        //Setup view
        bombImageView = (ImageView)findViewById(R.id.bombImageView);
        bombImageView.setImageResource(R.drawable.bomb);

        //Setup listener
        bombImageView.setOnTouchListener(new OnBombTouchListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameRef.removeEventListener(gameListener);
        userRef.removeEventListener(userListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        calibrationHelper = new CalibrationHelper();
        sensorManager = (SensorManager)getSystemService(this.SENSOR_SERVICE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(calibrationHelper, magSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(calibrationHelper, accSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void setPhoneUser(){
        for(User user: calibratedUsers) {
            if(user.id.equals(phoneUserId)) {
                phoneUser = user;
            }
        }
    }

    //The host pass the bomb to a random user
    private void passBombToRandomUser(){
        if(phoneUser.id.equals(host.id)) {
            initBomb();
            Random randomizer = new Random();
            User randomUser = calibratedUsers.get(randomizer.nextInt(calibratedUsers.size()));
            gameRef.child("bomb").setValue(bomb);
            userRef.getParent().child(randomUser.firebaseId).child("hasBomb").setValue(true);
        }
    }

    private void calculateNearestUser(float coor){

        User nearestUser = new User();
        nearestUser.angleAlpha = 100;
        for(User user : calibratedUsers){
            if(phoneUserId != user.id && abs(user.angleAlpha-coor) < abs(nearestUser.angleAlpha-coor)){
                nearestUser = user;
            }
        }
        passBombToPlayer(nearestUser);
    }

    //Pass the bomb, and
    private void passBombToPlayer(User user){
        gameRef.child("bomb").setValue(bomb);
        userRef.getParent().child(user.firebaseId).child("hasBomb").setValue(true);
        userRef.getParent().child(phoneUser.firebaseId).child("hasBomb").setValue(false);
    }

    //Init bomb
    private void initBomb(){
        bomb = new Bomb();
        Random rand = new Random();
        long upper = 60000;
        long lower = 30000;
        bomb.timeToLive = lower +(long)(rand.nextDouble()*(upper - lower));
    }

    //Countdown bomb
    private void bombCountdown(Bomb bombToCountdown){
        new CountDownTimer(bombToCountdown.timeToLive, 1000) {

            public void onTick(long millisUntilFinished) {
                bomb.timeToLive = millisUntilFinished;
            }

            public void onFinish() {

            }
        }.start();
    }

    @Override
    public void onQuestionCorrectAnswer() {
        Log.d("CorrectAnswer", "Correct answer");
    }

    @Override
    public void onQuestionWrongAnswer() {
        Log.d("WrongAnswer", "Wrong answer");
        getRandomQuestion();
    }

    private void getRandomQuestion(){

        Random randomizer = new Random();
        Question randomQuestion = allQuestions.get(randomizer.nextInt(allQuestions.size()));
        QuestionDialogFragment qFrag = QuestionDialogFragment.newInstance(randomQuestion);
        qFrag.show(fm,"FragmentTest");
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

                if(!phoneUser.hasBomb){
                    getRandomQuestion();
                    return false;

                }
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
                            //TODO: fix animation
                            bombImageView.setAlpha(0.0f);
                            //Get the current Aplha
                            sensorManager.getRotationMatrix(rotationMatrix, null,
                                    calibrationHelper.accelerometerReading, calibrationHelper.magnetometerReading);

                            sensorManager.getOrientation(rotationMatrix, orientationAngles);
                            //Calulate and update who have the bomb
                            calculateNearestUser(orientationAngles[0]);
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
