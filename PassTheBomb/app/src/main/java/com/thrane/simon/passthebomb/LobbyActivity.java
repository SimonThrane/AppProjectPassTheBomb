package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
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
    private ListView lvPlayers;
    private FirebaseDatabase database;
    private DatabaseReference gamesRef;
    private SharedPreferences sharedPref;
    private Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        txtTriviaCategory = findViewById(R.id.txtTriviaCategory);
        txtTriviaDifficulty = findViewById(R.id.txtTriviaDifficulty);
        txtGameName = findViewById(R.id.txtGameName);
        lvPlayers = findViewById(R.id.lvPlayers);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setVisibility(View.GONE);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calibrateIntent = new Intent();
                calibrateIntent.putExtra("GameKey", getIntent().getStringExtra("GameKey"));
                startActivity(new Intent(getBaseContext(), CalibrateActivity.class));
            }
        });

        database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("Games");

        sharedPref = getSharedPreferences(null, MODE_PRIVATE);

        // Get game from firebase
        Intent intent = getIntent();
        String gameKey = intent.getStringExtra("GameKey");
        gamesRef.child(gameKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                // TO DO Fix the category thing with the enum and the thing
                txtTriviaCategory.setText(game.category);
                txtTriviaDifficulty.setText(game.difficulty);
                txtGameName.setText(game.name);

                // Show START GAME button if this is the host
                String currentUser = sharedPref.getString("UserName", null);
                if(game.host.name.equals(currentUser)) {
                    btnStart.setVisibility(View.VISIBLE);
                }
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
}
