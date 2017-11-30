package com.thrane.simon.passthebomb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_list);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Games");
        Query query = mRef.limitToLast(50);
        options = new FirebaseListOptions.Builder<Game>()
                .setLayout(R.layout.lobby_list_item)
                .setQuery(query, Game.class)
                .build();
        //
        gameAdapter = new FirebaseListAdapter<Game>(options) {
            @Override
            protected void populateView(View v, Game gameDataItem, int position) {
                TextView txtLobbyName = v.findViewById(R.id.tvLobbyName);
                txtLobbyName.setText(gameDataItem.Name);

                //TextView txtHostName = v.findViewById(R.id.tvHostName);
                //txtHostName.setText(Integer.toString(gameDataItem.Host.Name));

                TextView txtPlayerNum = v.findViewById(R.id.tvPlayerNum);
                txtPlayerNum.setText(Double.toString(gameDataItem.Users.size()));

            }
        };
        final ListView lv = (ListView) findViewById(R.id.listVievLobby);
        lv.setAdapter(gameAdapter);

        //https://stackoverflow.com/questions/18405299/onitemclicklistener-using-arrayadapter-for-listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                String Id = (String)adapter.getItemAtPosition(position);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
            }
        });
    }
}
