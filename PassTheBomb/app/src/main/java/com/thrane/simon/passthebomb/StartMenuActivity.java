package com.thrane.simon.passthebomb;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thrane.simon.passthebomb.Models.Game;
import com.thrane.simon.passthebomb.Models.User;
import com.thrane.simon.passthebomb.Util.Globals;

//Inspired by https://github.com/firebase/FirebaseUI-Android/blob/master/app/src/main/java/com/firebase/uidemo/database/realtime/RealtimeDbChatActivity.java
public class StartMenuActivity extends AppCompatActivity {
    private static final String TAG = "STARTMENU";
    private FirebaseListAdapter<Game> gameAdapter;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseListOptions<Game> options;
    private Game selectedGame;
    private Button createLobbyBtn;
    private Button joinLobbyBtn;
    private ListView lv;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_list);

        sharedPref = getSharedPreferences(null, MODE_PRIVATE);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Games");
        Query query = mRef.orderByChild("isPublic").equalTo(true);
        options = new FirebaseListOptions.Builder<Game>()
                .setLayout(R.layout.lobby_list_item)
                .setQuery(query, Game.class)
                .build();

        createLobbyBtn = findViewById(R.id.createBtn);
        joinLobbyBtn = findViewById(R.id.joinLobbyBtn);

        createLobbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CreateLobbyIntent = new Intent(getBaseContext(), CreateLobbyActivity.class);
                startActivity(CreateLobbyIntent);
            }
        });
        joinLobbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (selectedGame != null) {
//                    Intent LobbyIntent = new Intent(getBaseContext(), LobbyActivity.class);
//                    startActivity(LobbyIntent);
//                }
                Intent JoinLobbyIntent = new Intent(getBaseContext(), JoinLobbyActivity.class);
                startActivity(JoinLobbyIntent);
            }
        });
        gameAdapter = new FirebaseListAdapter<Game>(options) {
            @Override
            protected void populateView(View v, Game gameDataItem, int position) {
                TextView txtLobbyName = v.findViewById(R.id.tvLobbyName);
                txtLobbyName.setText(gameDataItem.name);

                TextView txtHostName = v.findViewById(R.id.tvHostName);
                txtHostName.setText(gameDataItem.host.name);

                TextView txtPlayerNum = v.findViewById(R.id.tvPlayerNum);
                txtPlayerNum.setText(String.valueOf(gameDataItem.users.size()));

                TextView txtCategory = v.findViewById(R.id.tvCategoryValue);
                txtCategory.setText(gameDataItem.category.name);
            }
        };
        lv = findViewById(R.id.listViewLobby);
        gameAdapter.startListening();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(gameAdapter);

        //https://stackoverflow.com/questions/18405299/onitemclicklistener-using-arrayadapter-for-listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                selectedGame = (Game) adapter.getItemAtPosition(position);
                v.setSelected(true);

                final int pos = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(StartMenuActivity.this, R.style.Theme_AppCompat_Dialog);
                builder
                        .setTitle(R.string.start_dialog_title)
                        .setMessage(R.string.start_dialog_message + " " + selectedGame.name + "?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                joinLobby(gameAdapter.getRef(pos).getKey(), selectedGame);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do nothing
                            }
                        })
                        .show();

          }
        });

    }

    private void joinLobby(String gameKey, Game game) {
        if(game.users.size() >= Globals.MAX_PLAYERS) {
            Toast.makeText(this, "The lobby you are trying to join is full", Toast.LENGTH_SHORT).show();
            return;
        }
        User currentUser = new User();
        currentUser.name = sharedPref.getString(Globals.USER_NAME, null);
        currentUser.id = sharedPref.getString(Globals.USER_ID, null);
        currentUser.photoUri = sharedPref.getString(Globals.USER_PHOTO_URI, null);
        if(currentUser.name == null) currentUser.name = "Unknown user"; // can only be null in testing env
        game.users.add(currentUser);
        mRef.child(gameKey).child("users").setValue(game.users);

        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra(Globals.GAME_KEY, gameKey);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        gameAdapter.stopListening();

    }


}
