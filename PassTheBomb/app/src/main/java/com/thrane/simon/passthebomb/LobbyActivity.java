package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thrane.simon.passthebomb.Models.Game;
import com.thrane.simon.passthebomb.Models.User;
import com.thrane.simon.passthebomb.Util.Globals;

public class LobbyActivity extends AppCompatActivity {

    private TextView txtTriviaCategory;
    private TextView txtTriviaDifficulty;
    private TextView txtGameName;
    private TextView txtPasswordTitle;
    private ListView lvPlayers;
    private FirebaseDatabase database;
    private DatabaseReference gamesRef;
    private SharedPreferences sharedPref;
    private Button btnStart;
    private Button btnLeave;
    private TextView txtPassword;
    private String gameKey;
    private User currentUser;
    private Game gameSnapshot;
    private ValueEventListener gameStartedListener;
    private FirebaseListAdapter<User> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        currentUser = new User();
        database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("Games");
        Intent intent = getIntent();
        gameKey = intent.getStringExtra(Globals.GAME_KEY);

        txtTriviaCategory = findViewById(R.id.txtTriviaCategory);
        txtTriviaDifficulty = findViewById(R.id.txtTriviaDifficulty);
        txtGameName = findViewById(R.id.txtGameName);
        txtPassword = findViewById(R.id.txtPassword);
        txtPassword.setVisibility(View.INVISIBLE);
        txtPasswordTitle = findViewById(R.id.txtPasswordTitle);
        txtPasswordTitle.setVisibility(View.INVISIBLE);
        lvPlayers = findViewById(R.id.lvPlayers);
        btnLeave = findViewById(R.id.btnLeave);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setVisibility(View.GONE);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gamesRef.child(gameKey).child("gameStarted").setValue(true);
                startCalibrateActivity();
            }
        });

        sharedPref = getSharedPreferences(null, MODE_PRIVATE);
        // Init the player list
        Query query = gamesRef.child(gameKey).child("users");
        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.player_list_item)
                .setQuery(query, User.class)
                .build();
        adapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(View v, User model, int position) {
                TextView txtName = v.findViewById(R.id.txtName);
                txtName.setText(model.name);
                TextView txtHost = v.findViewById(R.id.txtHost);
                ImageView imgPlayer = v.findViewById(R.id.imgPlayer);
                //https://github.com/bumptech/glide
                Glide.with(getBaseContext()).load(model.photoUri).into(imgPlayer);

                txtHost.setVisibility(View.INVISIBLE);
                boolean isHost = isHost(model);
                if(isHost) {
                    txtHost.setVisibility(View.VISIBLE);
                }
            }
        };

        // Get game info once
        gamesRef.child(gameKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gameSnapshot = dataSnapshot.getValue(Game.class);
                txtTriviaCategory.setText(gameSnapshot.category.name);
                txtTriviaDifficulty.setText(gameSnapshot.difficulty);
                txtGameName.setText(gameSnapshot.name);
                adapter.startListening();
                lvPlayers.setAdapter(adapter);
                // If the game has a password, show it
                if(gameSnapshot.password != null) {
                    txtPasswordTitle.setVisibility(View.VISIBLE);
                    txtPassword.setText(gameSnapshot.password);
                    txtPassword.setVisibility(View.VISIBLE);
                }

                // Show START GAME button if this is the host
                currentUser.id = sharedPref.getString(Globals.USER_ID,null);
                currentUser.name = sharedPref.getString(Globals.USER_NAME, null);

                // If we are the host, show the START GAME button, and change BACK button to say DESTROY
                // If we are the host, destroy the game lobby if we leave

                if(isHost(currentUser)) {
                    btnStart.setVisibility(View.VISIBLE);
                    btnLeave.setText(getString(R.string.lobby_destroy));
                    btnLeave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gamesRef.child(gameKey).removeValue();
                            finish();
                        }
                    });
                } else {
                    btnLeave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            leaveLobby();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gameStartedListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    return;
                }
                Boolean started = dataSnapshot.getValue(Boolean.class);
                if(started != null && started) {
                    if(!isHost(currentUser)) {
                        startCalibrateActivity();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gamesRef.child(gameKey).child("gameStarted").addValueEventListener(gameStartedListener);

        // if game stops existing, leave the lobby
        gamesRef.child(gameKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void startCalibrateActivity() {
        Intent calibrateIntent = new Intent(this, CalibrateActivity.class);
        calibrateIntent.putExtra(Globals.GAME_KEY, gameKey);
        startActivity(calibrateIntent);

    }

    // TO DO make check better with ID's
    private boolean isHost(User user) {
        if(gameSnapshot.host.id.equals(user.id)) {
            return true;
        } return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        gamesRef.removeEventListener(gameStartedListener);
    }

    @Override
    public void onBackPressed()
    {
        if(isHost(currentUser)) {
            gamesRef.child(gameKey).removeValue();
        } else {
            leaveLobby();
        }
        super.onBackPressed();
    }

    public void leaveLobby() {
        Query query = gamesRef
                .child(gameKey)
                .child("users")
                .orderByChild("id")
                .equalTo(currentUser.id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    gamesRef
                            .child(gameKey)
                            .child("users")
                            .child(data.getKey())
                            .removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
