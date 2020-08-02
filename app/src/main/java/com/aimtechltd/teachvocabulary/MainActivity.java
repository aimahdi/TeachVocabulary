package com.aimtechltd.teachvocabulary;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button vocabularyList, vocabularyGame, rankList, signOut;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_grid);


        initializeViews();

        vocabularyList.setOnClickListener(this);
        vocabularyGame.setOnClickListener(this);
        rankList.setOnClickListener(this);
        signOut.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) {
            logOutUser();
        }
    }

    private void initializeViews() {

        vocabularyList = findViewById(R.id.vocabulary_activity_button);
        vocabularyGame = findViewById(R.id.game_activity_button);
        rankList = findViewById(R.id.ranklist_activity_button);
        signOut = findViewById(R.id.sign_out_button);

        firebaseAuth = FirebaseAuth
                .getInstance();

        user = firebaseAuth
                .getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.vocabulary_activity_button) {
            Intent intent = new Intent(MainActivity.this, VocabularyListActivity.class);
            startActivity(intent);

        } else if (id == R.id.game_activity_button) {
            Intent intent = new Intent(MainActivity.this, VocabularyGameActivity.class);
            startActivity(intent);

        } else if (id == R.id.ranklist_activity_button) {
            Intent intent = new Intent(MainActivity.this, RankListActivity.class);
            startActivity(intent);

        } else if (id == R.id.sign_out_button) {
            logOutUser();
        }
    }

    private void logOutUser() {
        firebaseAuth
                .signOut();

        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
