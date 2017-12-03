package com.thrane.simon.passthebomb;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.thrane.simon.passthebomb.Models.User;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    ImageView bombImageView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Get the users example
        Intent intent = getIntent();
        ArrayList<User> users = intent.getParcelableArrayListExtra("CALIBRATED_USERS");
        //Get the users example

        bombImageView = (ImageView)findViewById(R.id.bombImageView);
        bombImageView.setImageResource(R.drawable.bomb);

        bombImageView.setOnTouchListener(new OnBombTouchListener());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.isis_theme_song);

        mediaPlayer.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();

    }

    public class OnBombTouchListener implements View.OnTouchListener {
        private Context context;
        private final GestureDetector gdt = new GestureDetector(this.context, new GestureListener());
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            gdt.onTouchEvent(event);
            return true;
        }
        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, float velocityY) {

                float x1 = e1.getX();
                float y1 = e1.getY();

                float x2 = e2.getX();
                float y2 = e2.getY();
                double deltaX = x2 - x1;
                double deltaY = y2 - y1;
                double rad = Math.atan2(deltaY, deltaX);
                double deg = rad * (180 / Math.PI);
                double start= bombImageView.getTranslationY();
                if(deg < -45 && deg> -135 && y2< 0) {
                    final TranslateAnimation animationt1 = new TranslateAnimation(0, 0, bombImageView.getTranslationY(), y2 * 5);
                    animationt1.setDuration(300);

                    bombImageView.startAnimation(animationt1);
                    animationt1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
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
