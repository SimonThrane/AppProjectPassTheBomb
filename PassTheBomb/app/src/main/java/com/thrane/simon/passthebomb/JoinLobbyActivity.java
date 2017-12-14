package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thrane.simon.passthebomb.Models.Game;
import com.thrane.simon.passthebomb.Models.User;
import com.thrane.simon.passthebomb.Util.Globals;

import java.util.List;

public class JoinLobbyActivity extends AppCompatActivity {
    private Button btnBack;
    private Button btnJoin;
    private EditText edtPassword;
    private TextView txtEnterPassword;
    private FirebaseDatabase database;
    private DatabaseReference gamesRef;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);

        btnBack = findViewById(R.id.btnBack);
        btnJoin = findViewById(R.id.btnJoin);
        edtPassword = findViewById(R.id.edtPassword);
        txtEnterPassword = findViewById(R.id.txtEnterPassword);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onJoinClicked(edtPassword.getText().toString());
            }
        });

        database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("Games");
        sharedPref = getSharedPreferences(null, MODE_PRIVATE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // inspired from https://stackoverflow.com/a/40367515
    private void onJoinClicked(final String password) {
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Game currentGame = snapshot.getValue(Game.class);
                    //HashMap<Integer, String> gameHash = (HashMap<Integer, String>) snapshot.getValue();
                    // if a game with the password is found, add the user to the lobby
                    if(currentGame.password != null && currentGame.password.equals(password)) {
                        String key = snapshot.getKey();
                        List<User> users = snapshot.getValue(Game.class).users;
                        // check if the lobby is full
                        if(users.size() >= Globals.MAX_PLAYERS) {
                            Toast.makeText(JoinLobbyActivity.this, "The lobby you are trying to join is full", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        User currentUser = new User();
                        currentUser.name = sharedPref.getString(Globals.USER_NAME, null);
                        currentUser.id = sharedPref.getString(Globals.USER_ID, null);
                        currentUser.photoUri = sharedPref.getString(Globals.USER_PHOTO_URI, null);
                        if(currentUser.name == null) currentUser.name = "Unknown user"; // can only be null in testing env
                        users.add(currentUser);
                        gamesRef.child(key).child("users").setValue(users);

                        Intent intent = new Intent(getBaseContext(), LobbyActivity.class);
                        intent.putExtra(Globals.GAME_KEY, key);
                        startActivity(intent);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
