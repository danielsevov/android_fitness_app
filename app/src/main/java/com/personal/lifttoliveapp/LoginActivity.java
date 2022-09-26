package com.personal.lifttoliveapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.personal.lifttoliveapp.API.BackendAPI;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText textEmail;
    private EditText textPassword;
    private Button buttonLog;

    private static final String TAG = "LOGIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("LiftToLiveApp");

        buttonLog = findViewById(R.id.logInButton);
        textEmail = findViewById(R.id.emailText);
        textPassword = findViewById(R.id.enterPasswordText);

        buttonLog.setOnClickListener(this);

        if(BackendAPI.isAuthenticated()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Method for processing the login. Is called from login button
     */
    private void processLogin() {
        if(BackendAPI.Authenticate(textEmail.getText().toString(), textPassword.getText().toString())) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        else {
            textPassword.setText("");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    int backButtonCount = 0;
    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logInButton) {
            processLogin();
        }
    }
}