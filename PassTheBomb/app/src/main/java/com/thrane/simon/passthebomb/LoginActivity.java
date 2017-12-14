package com.thrane.simon.passthebomb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.thrane.simon.passthebomb.Util.Globals;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "GOOGLE_AUTH";
    private static final int RC_SIGN_IN = 100;
    private static final String DEFAULT_IMAGE_URL = "https://openclipart.org/download/252171/bomb.svg";
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestProfile()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            updateUI();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(getApplicationContext(), "Google authenticated succeed",
                    Toast.LENGTH_SHORT).show();
            // Signed in successfully, show authenticated UI.

            saveAccountToSharedPrefs(account);

            updateUI();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void saveAccountToSharedPrefs(GoogleSignInAccount account) {
        if (mPrefs == null) {
            mPrefs = getSharedPreferences(null,MODE_PRIVATE);
        }
        String name = account.getDisplayName();
        String id = account.getId();
        String photoUrl;
        Uri photo = account.getPhotoUrl();
        if(photo != null) {
            photoUrl = photo.toString();
        } else {
            photoUrl = DEFAULT_IMAGE_URL;
        }

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putString(Globals.USER_NAME, name);
        prefsEditor.putString(Globals.USER_ID, id);
        prefsEditor.putString(Globals.USER_PHOTO_URI, photoUrl);
        prefsEditor.commit();
    }

    private void updateUI() {
        Intent LobbyListIntent = new Intent(this, StartMenuActivity.class);
        startActivity(LobbyListIntent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

}


