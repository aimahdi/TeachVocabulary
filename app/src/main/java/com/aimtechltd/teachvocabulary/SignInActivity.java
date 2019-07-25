package com.aimtechltd.teachvocabulary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView goToSignUp;

    private TextInputEditText emailInput, passwordInput;

    private Button signIn;

    private FirebaseAuth firebaseAuth;

    private Context context;

    private ProgressDialog loading;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeViews();

        initializeFirebase();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        mAdView = findViewById(R.id.adViewSignIn);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        signIn.setOnClickListener(this);

        goToSignUp.setOnClickListener(this);
    }


    private void initializeViews() {

        goToSignUp = findViewById(R.id.sign_up);

        emailInput = findViewById(R.id.email_input_SI);
        passwordInput = findViewById(R.id.password_input_SI);

        signIn = findViewById(R.id.sign_in_button);

        context = SignInActivity.this;

        loading = new ProgressDialog(context);
    }

    private void initializeFirebase() {

        firebaseAuth = FirebaseAuth
                .getInstance();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.sign_in_button) {
            signInNow();
        } else if (id == R.id.sign_up) {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    private void signInNow() {

        loading.setTitle("Signing In");
        loading.setMessage("Please wait, while we are signing you in");


        String email = emailInput
                .getText()
                .toString()
                .trim();

        String password = passwordInput
                .getText()
                .toString()
                .trim();

        if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(),
                    "Please check all values",
                    Toast.LENGTH_SHORT).show();

        } else {
            loading.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loading.dismiss();

                                Toast.makeText(context,
                                        "Successfully signed in",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                loading.dismiss();
                                Toast.makeText(context,
                                        task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
