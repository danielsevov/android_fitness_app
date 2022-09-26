package com.personal.lifttoliveapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personal.lifttoliveapp.API.BackendAPI;
import com.personal.lifttoliveapp.adapters.profilePage.MyUserAdapter;
import com.personal.lifttoliveapp.entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OverviewTraineesActivity extends AppCompatActivity {
    private RecyclerView userRecyclerView;
    private MyUserAdapter userAdapter;
    private FloatingActionButton cancelButton, registerButton;
    private RecyclerView.LayoutManager userLayoutManager;
    private EditText searchUserText;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_users);
        setTitle("LiftToLiveApp");

        users = new ArrayList<>();

        userRecyclerView = (RecyclerView) findViewById(R.id.activeWorkoutsRecyclerView);
        cancelButton =  findViewById(R.id.backButton);
        registerButton =  findViewById(R.id.registerUserButton);
        searchUserText = (EditText) findViewById(R.id.enterUserText);

        initializeTextFields();
        initializeButtons();
        initializeRecyclerView();
    }

    private void initializeTextFields() {
        searchUserText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<User> backup = new ArrayList<>();
                users.forEach(s -> {
                    if (s.getName().toLowerCase().contains(searchUserText.getText().toString().toLowerCase()) || s.getEmail().toLowerCase().contains(searchUserText.getText().toString().toLowerCase())) {
                        backup.add(s);
                    }
                });

                userAdapter = new MyUserAdapter(backup, getApplicationContext());
                userRecyclerView.setAdapter(userAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initializeRecyclerView() {
        userRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        userLayoutManager = new LinearLayoutManager(this);
        userRecyclerView.setLayoutManager(userLayoutManager);

        BackendAPI.Send(null, "my_trainees", "GET", (result2) -> {
            JSONArray array2;
            try {
                array2 = result2.getJSONArray("data");
                int i =0;
                while(array2.length() >= i+1){
                    JSONObject obj = array2.getJSONObject(i);
                    users.add(new User(obj.getString("name"), obj.getString("id")));
                    i++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("list = " + users.size());
            userAdapter = new MyUserAdapter(users, getApplicationContext());
            userRecyclerView.setAdapter(userAdapter);
        });
    }

    private void initializeButtons() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TraineeProfileActivity.class);
                intent.putExtra("userId", BackendAPI.userId);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}