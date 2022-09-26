package com.personal.lifttoliveapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.personal.lifttoliveapp.API.BackendAPI;

public class HomeActivity extends AppCompatActivity {
    private Button button, button2, myTraineesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("LiftToLiveApp");

        button2 = findViewById(R.id.button2);
        button = findViewById(R.id.button);
        myTraineesButton = findViewById(R.id.myTraineesButton);

        initializeButtons();
    }

    private void initializeButtons() {
        myTraineesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TraineeProfileActivity.class);
                intent.putExtra("userId", BackendAPI.userId);
                startActivity(intent);
            }
        });
    }
}