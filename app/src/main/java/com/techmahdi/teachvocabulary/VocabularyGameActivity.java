package com.techmahdi.teachvocabulary;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VocabularyGameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewQuestion, textViewScore, textViewQuestionCount, textViewCountDown;

    private RadioGroup radioGroup;

    private RadioButton option1, option2, option3, option4;

    private Button nextButton;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private int questionCounter;
    private int questionCountTotal;


    private Question currentQuestion;

    private int score;
    private long prevScore;

    private DatabaseReference questionReference;
    private DatabaseReference scoreReference;
    private FirebaseAuth firebaseAuth;

    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private boolean answered;

    private List<Question> questions = new ArrayList<>();


    private long backPressedTime;

    private boolean check = false;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_game);

        getSupportActionBar().setTitle("Knockout Vocabulary Game");

        initializeViews();

        initializeFirebase();

        pd = new ProgressDialog(this);

        pd.show();

       questionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    currentQuestion = child.getValue(Question.class);
                    questions.add(currentQuestion);

                }

                setUpQuestion(questions);
                pd.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        nextButton.setOnClickListener(this);


    }


    private void initializeViews() {

        textViewQuestion = findViewById(R.id.text_view_question);

        textViewScore = findViewById(R.id.text_view_score);

        textViewQuestionCount = findViewById(R.id.text_view_question_count);

        textViewCountDown = findViewById(R.id.text_view_count_down);

        radioGroup = findViewById(R.id.radio_group);

        option1 = findViewById(R.id.radio_button_1);
        option2 = findViewById(R.id.radio_button_2);
        option3 = findViewById(R.id.radio_button_3);
        option4 = findViewById(R.id.radio_button_4);

        nextButton = findViewById(R.id.button_next);

        textColorDefaultRb = option1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();
    }

    private void initializeFirebase() {

        firebaseAuth = FirebaseAuth
                .getInstance();
        questionReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("GameQuestion");

        scoreReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Scores");


    }

    private void setUpQuestion(List<Question> questions) {

        this.questions = questions;

        Collections.shuffle(questions);

        questionCountTotal = questions.size();

        showNextQuestion();

    }

    private void startCountDown() {

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();

    }

    private void checkAnswer() {

        answered = true;

        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(radioGroup.getCheckedRadioButtonId());
        String ans;
        if (rbSelected != null) {
            ans = rbSelected.getText().toString().trim();
        } else ans = "hijibiji";
        String solution = questions.get(questionCounter - 1).getSolution();


        if (ans.equals(solution)) {
            score++;
            textViewScore.setText("Score: " + score);
            showNextQuestion();
        }else {
            finishGame(currentQuestion.getSolution());
        }





    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewCountDown.setText(timeFormatted);

        if (timeLeftInMillis < 10000) {
            textViewCountDown.setTextColor(Color.RED);
        } else {
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    private void showNextQuestion() {
        option1.setTextColor(textColorDefaultRb);
        option2.setTextColor(textColorDefaultRb);
        option3.setTextColor(textColorDefaultRb);
        option4.setTextColor(textColorDefaultRb);
        radioGroup.clearCheck();


        if (questionCounter < questionCountTotal) {
            currentQuestion = questions.get(questionCounter);

            if (questionCounter == questionCountTotal - 1) nextButton.setText("Finish");


            textViewQuestion.setText(currentQuestion.getQuestion());


            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            option4.setText(currentQuestion.getOption4());

            questionCounter++;

            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);

            answered = false;
            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();

        } else {
            finishGame(currentQuestion.getSolution());
        }



    }

    private void finishGame(final String solution) {

        final String userId = firebaseAuth
                .getCurrentUser()
                .getUid();

        DatabaseReference userReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users")
                .child(userId);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String fullName = dataSnapshot.child("fullName").getValue().toString();
                Log.e("fullName", fullName+"");

                prevScore = 0;
                scoreReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Map <String, String> values = new HashMap<>();

                        values.put("fullName", fullName);
                        values.put("score", score+"");

                        if (dataSnapshot.exists()) {
                            Log.e("Score", dataSnapshot+"");
                            String s = dataSnapshot.child("score").getValue(String.class);

                            prevScore = Long.parseLong(s);



                            if (prevScore < score) scoreReference.child(userId).setValue(values);
                        }else {
                            scoreReference.child(userId).setValue(values);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (check == false) showResultWithAnswer(solution, score);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(VocabularyGameActivity.this, MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }
        }.start();


    }

    private void showResultWithAnswer(String solution, int score) {

        check = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(VocabularyGameActivity.this);


        builder.setTitle("Game is finished");
        builder.setMessage("The answer was: "+solution+
                "\n\nYour score is: "+score+
                "\n\nBetter luck next time");

        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(VocabularyGameActivity.this, MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();



        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

         dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button_next) {
            if (!answered) {
                if (option1.isChecked() ||
                        option2.isChecked() ||
                        option3.isChecked() ||
                        option4.isChecked()) {

                    checkAnswer();
                } else {
                    Toast.makeText(VocabularyGameActivity.this,
                            "Please select an answer",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                if (questionCounter != questionCountTotal) showNextQuestion();
                else finishGame(currentQuestion.getSolution());
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishGame(currentQuestion.getSolution());
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
