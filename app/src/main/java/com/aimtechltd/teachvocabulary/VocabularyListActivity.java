package com.aimtechltd.teachvocabulary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class VocabularyListActivity extends AppCompatActivity {

    private ListView wordList;

    private Query query;

    private FirebaseListAdapter<Word> firebaseListAdapter, firebaseSearchAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wordList = findViewById(R.id.wordList);

        query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("WordList");

        FirebaseListOptions<Word> options = new FirebaseListOptions.Builder<Word>()
                .setLayout(R.layout.custom_word_layout)
                .setQuery(query, Word.class)
                .build();

        firebaseListAdapter = new FirebaseListAdapter<Word>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull final Word model, int position) {
                TextView wordView = v.findViewById(R.id.text_view_word);

                wordView.setText(model.getWord());

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LayoutInflater inflater = LayoutInflater.from(VocabularyListActivity.this);
                        View view = inflater.inflate(R.layout.word_view_dialog, null);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(VocabularyListActivity.this);

                        //dialog.setCancelable(false);
                        TextView word = view.findViewById(R.id.word);
                        TextView meaning = view.findViewById(R.id.meaning);
                        TextView synonyms = view.findViewById(R.id.synonyms);
                        TextView example = view.findViewById(R.id.example);

                        word.setText("Word: " + model.getWord());
                        meaning.setText("Meaning: " + model.getMeaning());
                        synonyms.setText("Synonyms: " + model.getSynonyms());
                        example.setText("Example: " + model.getExample());
                        dialog.setView(view);

                        dialog.create();
                        dialog.show();
                    }
                });
            }
        };

        firebaseListAdapter.notifyDataSetChanged();

        firebaseListAdapter.startListening();


        wordList.setAdapter(firebaseListAdapter);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchQuery) {
                Log.e("QueryS", searchQuery);

                if (!TextUtils.isEmpty(searchQuery)) {
                    searchQuery = searchQuery.substring(0, 1).toUpperCase() + searchQuery.substring(1, searchQuery.length()).toLowerCase();

                    Query testQuery = query
                            .orderByChild("word")
                            .startAt(searchQuery)
                            .endAt(searchQuery + "\uf8ff");
                    FirebaseListOptions<Word> options = new FirebaseListOptions.Builder<Word>()
                            .setQuery(testQuery, Word.class)
                            .setLayout(R.layout.custom_word_layout)
                            .build();
                    callAdapter(options);
                } else {
                    wordList.setAdapter(firebaseListAdapter);
                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                Log.e("QueryN", searchQuery);

                if (!TextUtils.isEmpty(searchQuery)) {
                    searchQuery = searchQuery.substring(0, 1).toUpperCase() + searchQuery.substring(1, searchQuery.length()).toLowerCase();

                    Query testQuery = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("WordList")
                            .orderByChild("word")
                            .startAt(searchQuery)
                            .endAt(searchQuery + "\uf8ff");

                    FirebaseListOptions<Word> options = new FirebaseListOptions.Builder<Word>()
                            .setQuery(testQuery, Word.class)
                            .setLayout(R.layout.custom_word_layout)
                            .build();
                    callAdapter(options);
                } else wordList.setAdapter(firebaseListAdapter);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    void callAdapter(FirebaseListOptions<Word> options) {
        firebaseSearchAdpater = new FirebaseListAdapter<Word>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull final Word model, int position) {
                TextView wordView = v.findViewById(R.id.text_view_word);

                wordView.setText(model.getWord());

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LayoutInflater inflater = LayoutInflater.from(VocabularyListActivity.this);
                        View view = inflater.inflate(R.layout.word_view_dialog, null);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(VocabularyListActivity.this);

                        //dialog.setCancelable(false);
                        TextView word = view.findViewById(R.id.word);
                        TextView meaning = view.findViewById(R.id.meaning);
                        TextView synonyms = view.findViewById(R.id.synonyms);
                        TextView example = view.findViewById(R.id.example);

                        word.setText("Word: " + model.getWord());
                        meaning.setText("Meaning: " + model.getMeaning());
                        synonyms.setText("Synonyms: " + model.getSynonyms());
                        example.setText("Example: " + model.getExample());
                        dialog.setView(view);

                        dialog.create();
                        dialog.show();
                    }
                });
            }
        };

        firebaseSearchAdpater.notifyDataSetChanged();

        firebaseSearchAdpater.startListening();
        wordList.setAdapter(firebaseSearchAdpater);
    }
}
