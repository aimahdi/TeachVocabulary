package com.aimtechltd.teachvocabulary;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class RankListActivity extends AppCompatActivity {

    private ListView rankList;

    private Query query;

    private FirebaseListAdapter<Rank> firebaseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Rank List");

        rankList = findViewById(R.id.ranklist);

        query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Scores")
                .orderByChild("score");


        FirebaseListOptions<Rank> options = new FirebaseListOptions.Builder<Rank>()
                .setLayout(R.layout.custom_ranklist_layout)
                .setQuery(query, Rank.class)
                .build();

        firebaseListAdapter = new FirebaseListAdapter<Rank>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Rank model, int position) {
                TextView name, score;

                name = v.findViewById(R.id.user_name_textView);
                score = v.findViewById(R.id.user_score_textView);

                name.setText(model.getFullName());
                score.setText(model.getScore());
            }

            @NonNull
            @Override
            public Rank getItem(int position) {
                return super.getItem(getCount() - 1 - position);
            }
        };

        rankList.setAdapter(firebaseListAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseListAdapter.stopListening();
    }
}
