package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thrane.simon.passthebomb.Models.Category;
import com.thrane.simon.passthebomb.Models.Game;
import com.thrane.simon.passthebomb.Models.User;

import java.security.SecureRandom;
import java.util.ArrayList;

public class CreateLobbyActivity extends AppCompatActivity {
    private NumberPicker nbCategory;
    private Button btnBack;
    private Button btnOpenLobby;
    private RadioGroup rgDifficulty;
    private EditText edtGameName;
    private Switch isPublicSwitch;

    private FirebaseDatabase database;
    private DatabaseReference gamesRef;

    SharedPreferences mPrefs;

    final String categories[] = {"General knowledge", "Books", "Film", "Music", "Musicals and theatres", "Television", "Video games", "Science and nature", "Computers", "Mathematics", "Mythology", "Sports", "Geography", "History", "Politics", "Art", "Celebrities", "Animals", "Vehicles", "Comics", "Gadgets", "Anime and manga", "Cartoon and animations"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        // get widget references
        btnBack = findViewById(R.id.btnBack);
        btnOpenLobby = findViewById(R.id.btnOpenLobby);
        btnOpenLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnOpenLobbyClicked();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edtGameName = findViewById(R.id.edtGameName);
        isPublicSwitch = findViewById(R.id.isPublicSwitch);

        rgDifficulty = findViewById(R.id.rgDifficulty);

        nbCategory = findViewById(R.id.nbCategory);
        configureCategoryPicker();

        database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("Games");

        mPrefs = getSharedPreferences(null,MODE_PRIVATE);
    }

    // gets difficulty and category and creates a gamelobby on firebase
    private void onBtnOpenLobbyClicked() {
        Game game = new Game();

        // set category from the numberPickers current value
        game.category = Category.values()[nbCategory.getValue()];

        //set difficulty from selected radio button
        RadioButton selectedRb = findViewById(rgDifficulty.getCheckedRadioButtonId());
        game.difficulty = selectedRb.getText().toString();

        User user = new User();

        // DELETE THIS - ONLY FOR TESTING
//        SharedPreferences mPrefs = getSharedPreferences(null,MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        prefsEditor.putString("UserName", "Bobby");
//        prefsEditor.commit();



        user.name = mPrefs.getString("UserName", null);

        // ONLY FOR TESTING
        if(user.name ==  null) {
            user.name = "Bobby";
        }
        game.host = user;
        game.users = new ArrayList<>();
        game.users.add(user);
        game.isPublic = isPublicSwitch.isChecked();
        if(!game.isPublic){
            game.password =  generatePassword();
        }
        game.name = edtGameName.getText().toString();

//        gamesRef.push().setValue(game);
        String gameKey = gamesRef.push().getKey();
        gamesRef.child(gameKey).setValue(game);

        Intent lobbyIntent = new Intent(getBaseContext(), LobbyActivity.class);
        lobbyIntent.putExtra("GameKey", gameKey);
        startActivity(lobbyIntent);
    }

    private void configureCategoryPicker() {
        nbCategory.setMinValue(0);
        nbCategory.setMaxValue(categories.length - 1);
        nbCategory.setDisplayedValues(categories);
        nbCategory.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nbCategory.setValue(0);
//        NumberPicker.OnValueChangeListener catChangedListener = new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//
//            }
//        }
    }
    // inspired from https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string/157202#157202
    private String generatePassword() {
        final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom rnd = new SecureRandom();

        int len = 4;
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++) {
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ));
        }
        return sb.toString();
    }
}

// dont delete pls, need dis
// https://stackoverflow.com/questions/33442035/how-to-make-custom-android-string-picker-that-fits-inside-another-view