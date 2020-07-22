package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.course.instagram.R;
import com.course.instagram.config.FirebaseConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextView textRegister;
    private EditText email;
    private EditText password;
    private Button buttonLogin;
    private ProgressBar progressBar;

    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //retrieve variables
        textRegister = findViewById(R.id.textRegisterLogin);
        email = findViewById(R.id.editEmailLogin);
        password = findViewById(R.id.editPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBarLogin);

        //initialize variables
        authentication = FirebaseConfig.getFirebaseAuth();


        //
        setListeners();
    }

    private void setListeners() {
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        progressBar.setVisibility(View.GONE);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                if (!userEmail.isEmpty()) {
                    if (!userPassword.isEmpty()) {

                        loginUser(userEmail, userPassword);

                    } else {
                        Toast.makeText(LoginActivity.this, "Please fill password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill email address!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loggedUser() {
        if (authentication.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("Login", "Login successful");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {

                    String exception;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        exception = "Email address does not exist!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "Password does not match with email!";
                    } catch (Exception e) {
                        exception = "Error logging in: " + e.getMessage();
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser();
    }
}
