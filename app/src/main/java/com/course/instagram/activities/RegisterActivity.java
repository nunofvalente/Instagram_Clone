package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.course.instagram.R;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.constants.Constants;
import com.course.instagram.helper.Base64Custom;
import com.course.instagram.helper.UserFirebase;
import com.course.instagram.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;


public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private FirebaseAuth authentication;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //retrieve variables
        name = findViewById(R.id.editNameRegister);
        email = findViewById(R.id.editEmailRegister);
        password = findViewById(R.id.editPasswordRegister);
        confirmPassword = findViewById(R.id.editConfirmPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBarRegister);

        //initialize variables
        database = FirebaseConfig.getFirebaseDb();
        authentication = FirebaseConfig.getFirebaseAuth();



        setListeners();
    }

    private void setListeners() {
        progressBar.setVisibility(View.GONE);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                String userConfirmPassword = confirmPassword.getText().toString();

                if (!userName.isEmpty()) {
                    if (!userEmail.isEmpty()) {
                        if (!userPassword.isEmpty() && password.length() >= 6) {

                            UserModel userModel = new UserModel();
                            userModel.setName(userName);
                            userModel.setEmail(userEmail);
                            userModel.setPassword(userPassword);

                            String encodedEmail = UserFirebase.userEncodedEmail(userModel);
                            userModel.setId(encodedEmail);

                            if (userPassword.equals(userConfirmPassword)) {

                                progressBar.setVisibility(View.VISIBLE);
                                register(userModel);
                                saveUser(userModel);

                            } else {
                                Toast.makeText(RegisterActivity.this, "Password does not match!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "Password needs to have at least 6 characters!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(RegisterActivity.this, "Please enter your e-mail!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(final UserModel userModel) {

        authentication.createUserWithEmailAndPassword(userModel.getEmail(), userModel.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Registered with success", Toast.LENGTH_SHORT).show();
                    UserFirebase.updateUserName(userModel.getName());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    progressBar.setVisibility(View.GONE);
                    String exception;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exception = "Please enter a stronger password!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "Please enter a valid email!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        exception = "Account already registered!";
                    } catch (Exception e) {
                        exception = "Error registering user: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveUser(UserModel userModel) {

        String userEmail = Base64Custom.encodeEmail(userModel.getEmail());

        DatabaseReference userRef = database.child(Constants.USERS);
        userRef.child(userEmail).setValue(userModel);
    }
}
