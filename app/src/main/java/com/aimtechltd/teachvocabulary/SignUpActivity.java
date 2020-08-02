package com.aimtechltd.teachvocabulary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText fullNameInput, emailInput, passwordInput;

    private Button signUp;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference userReference;

    private Context context;

    private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViews();

        initializeFirebase();

        signUp.setOnClickListener(this);
    }


    private void initializeViews() {

        fullNameInput = findViewById(R.id.full_name_input_SU);
        emailInput = findViewById(R.id.email_input_SU);
        passwordInput = findViewById(R.id.password_input_SU);

        signUp = findViewById(R.id.sign_up_button);

        context = SignUpActivity.this;

        loading = new ProgressDialog(context);
    }

    private void initializeFirebase() {

        firebaseAuth = FirebaseAuth
                .getInstance();

        userReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.sign_up_button) {
            signUpNow();
        }
    }

    private void signUpNow() {

        loading.setTitle("Signing up");
        loading.setMessage("Please wait, while we are signing you up");


        String email = emailInput
                .getText()
                .toString()
                .trim();

        String password = passwordInput
                .getText()
                .toString()
                .trim();

        final String fullName = fullNameInput
                .getText()
                .toString()
                .trim();

        if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password)
                || TextUtils.isEmpty(fullName)) {

            Toast.makeText(getApplicationContext(),
                    "Please check all values",
                    Toast.LENGTH_SHORT).show();

        } else {
            loading.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loading.dismiss();

                                String userId = firebaseAuth
                                        .getCurrentUser()
                                        .getUid();


                                userReference.child(userId)
                                        .child("fullName")
                                        .setValue(fullName);

                                Toast.makeText(context,
                                        "Successfully signed up",
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
