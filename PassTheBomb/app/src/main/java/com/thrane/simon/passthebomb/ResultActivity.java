package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thrane.simon.passthebomb.Util.Globals;

public class ResultActivity extends AppCompatActivity {

    private TextView txtLoser;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtLoser = findViewById(R.id.txtLoser);
        btnBack = findViewById(R.id.btnReturnToMenu);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startMenuIntent = new Intent(getBaseContext(), StartMenuActivity.class);
                startActivity(startMenuIntent);
            }
        });

        Intent intent = getIntent();
        String loserName = intent.getStringExtra(Globals.LOSER);

        txtLoser.setText(loserName + " " + getString(R.string.result_loser_description));
    }
}
