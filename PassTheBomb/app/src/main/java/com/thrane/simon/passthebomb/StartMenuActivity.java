package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thrane.simon.passthebomb.Models.Game;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_list);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Games");
        Query query = mRef;
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
                if (selectedGame != null) {
                    Intent LobbyIntent = new Intent(getBaseContext(), LobbyActivity.class);
                    startActivity(LobbyIntent);
                }
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
                txtCategory.setText(gameDataItem.category);
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
            }
        });

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
