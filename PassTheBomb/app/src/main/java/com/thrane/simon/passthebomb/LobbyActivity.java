package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
    private TextView txtPassword;
    private String gameKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("Games");
        Intent intent = getIntent();
        gameKey = intent.getStringExtra("GameKey");

        txtTriviaCategory = findViewById(R.id.txtTriviaCategory);
        txtTriviaDifficulty = findViewById(R.id.txtTriviaDifficulty);
        txtGameName = findViewById(R.id.txtGameName);
        txtPassword = findViewById(R.id.txtPassword);
        txtPassword.setVisibility(View.INVISIBLE);
        txtPasswordTitle = findViewById(R.id.txtPasswordTitle);
        txtPasswordTitle.setVisibility(View.INVISIBLE);
        lvPlayers = findViewById(R.id.lvPlayers);
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

        // Get game info once
        gamesRef.child(gameKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                // TO DO Fix the category thing with the enum and the thing
                txtTriviaCategory.setText("I'm a category");
                txtTriviaDifficulty.setText(game.difficulty);
                txtGameName.setText(game.name);

                // If the game has a password, show it
                if(game.password != null) {
                    txtPasswordTitle.setVisibility(View.VISIBLE);
                    txtPassword.setText(game.password);
                    txtPassword.setVisibility(View.VISIBLE);
                }

                // Show START GAME button if this is the host
                String currentUser = sharedPref.getString("UserName", null);

                // TO DO remove - for test purposes. If currentUser is null we're in test environment, so set it explicitly
                if(currentUser == null) {
                    currentUser = "Bobby";
                }

                if(game.host.name.equals(currentUser)) {
                    btnStart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gamesRef.child(gameKey).child("gameStarted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If game has started, go to calibrateActivtiy

                Boolean started = dataSnapshot.getValue(Boolean.class);
                if(started) {
                    startCalibrateActivity();
                }

//                Log.d("gameStarted", dataSnapshot.getValue(Boolean.class).toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Init the player list
        Query query = gamesRef.child(gameKey).child("users");
        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.player_list_item)
                .setQuery(query, User.class)
                .build();

        FirebaseListAdapter<User> adapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(View v, User model, int position) {
                TextView txtName = v.findViewById(R.id.txtName);
                txtName.setText(model.name);
            }
        };

        adapter.startListening();
        lvPlayers.setAdapter(adapter);
    }

    private void startCalibrateActivity() {
        Intent calibrateIntent = new Intent();
        calibrateIntent.putExtra("GameKey", getIntent().getStringExtra("GameKey"));
        startActivity(new Intent(getBaseContext(), CalibrateActivity.class));
    }
}
