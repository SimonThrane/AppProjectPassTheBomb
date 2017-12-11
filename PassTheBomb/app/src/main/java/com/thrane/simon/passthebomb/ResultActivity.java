package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.thrane.simon.passthebomb.Util.Globals;

public class ResultActivity extends AppCompatActivity {

    private TextView txtLoser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtLoser = findViewById(R.id.txtLoser);

        Intent intent = getIntent();
        String loserName = intent.getStringExtra(Globals.LOSER);
        loserName = "Bobby";

        txtLoser.setText(loserName + " " + getString(R.string.result_loser_description));
    }
}
