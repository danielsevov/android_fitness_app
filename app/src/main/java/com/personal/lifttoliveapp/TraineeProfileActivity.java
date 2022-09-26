package com.personal.lifttoliveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personal.lifttoliveapp.API.BackendAPI;
import com.personal.lifttoliveapp.API.PropertyConstructor;
import com.personal.lifttoliveapp.adapters.profilePage.MyUserWorkoutAdapter;
import com.personal.lifttoliveapp.entities.UserWorkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TraineeProfileActivity extends AppCompatActivity {

    private static final String TAG = "TraineeProfileActivity";

    private String userId;
    private JSONObject userObject;
    private RecyclerView recyclerViewActive, recyclerViewCompleted;
    private MyUserWorkoutAdapter workoutAdapterActive, workoutAdapterCompleted;
    private Button myTraineesButton;
    private FloatingActionButton cancelButton, newWorkoutButton, logOutButton;
    private RecyclerView.LayoutManager layoutManagerActive, layoutManagerCompleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_profile);
        setTitle("LiftToLiveApp");

        initializeTextFields();
        initializeButtons();
        initializeRecyclerViews();

    }

    private void initializeTextFields() {
        Bundle b = getIntent().getExtras();
        userId = b.getString("userId");

        TextView email = findViewById(R.id.userEmailText);
        TextView phone = findViewById(R.id.userPhoneText);
        TextView nationality = findViewById(R.id.userNationalityText);
        TextView birthday = findViewById(R.id.userBirthday);
        TextView title = findViewById(R.id.profilePageTitleText);

        BackendAPI.Send(new PropertyConstructor().addProperty("id", userId), "get_user", "POST", (result2) -> {
            try {
                userObject = result2;

                title.setText(userObject.getString("name"));
                email.setText(String.format("email:                    %s", userObject.getString("email")));
                phone.setText(String.format("phone number:    %s", userObject.getString("phone_number")));
                nationality.setText(String.format("nationality:           %s", userObject.getString("nationality")));
                birthday.setText(String.format("date of birth:        %s", userObject.getString("date_of_birth")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeRecyclerViews() {
        recyclerViewActive = (RecyclerView) findViewById(R.id.activeWorkoutsRecyclerView);
        recyclerViewCompleted = (RecyclerView) findViewById(R.id.finishedWorkoutsRecyclerView);

        recyclerViewActive.setHasFixedSize(false);
        layoutManagerActive = new LinearLayoutManager(this);
        recyclerViewActive.setLayoutManager(layoutManagerActive);

        recyclerViewCompleted.setHasFixedSize(false);
        layoutManagerCompleted = new LinearLayoutManager(this);
        recyclerViewCompleted.setLayoutManager(layoutManagerCompleted);

        ArrayList<UserWorkout> inputActive = new ArrayList<>(), inputCompleted = new ArrayList<>();
        BackendAPI.Send(new PropertyConstructor().addProperty("id", userId), "workouts_for_user", "POST", (result2) -> {
            JSONArray array2;
            try {
                array2 = result2.getJSONArray("data");
                int i =0;
                while(array2.length() >= i+1){
                    JSONObject obj = array2.getJSONObject(i);

                    if( obj.get("completed_on") == null || obj.getString("completed_on").isEmpty()) {
                        inputActive.add(new UserWorkout(obj.getInt("id"), obj.getString("workout_name"), obj.getLong("created_on"), 0, obj.getString("userId")));
                    }
                    else {
                        inputCompleted.add(new UserWorkout(obj.getInt("id"), obj.getString("workout_name"), obj.getLong("created_on"), obj.getLong("completed_on"), obj.getString("userId")));
                    }

                    i++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Collections.sort(inputActive, new Comparator<UserWorkout>() {
                @Override
                public int compare(UserWorkout lhs, UserWorkout rhs) {
                    return (int) (rhs.getCreatedOn() - lhs.getCreatedOn());
                }
            });

            Collections.sort(inputCompleted, new Comparator<UserWorkout>() {
                @Override
                public int compare(UserWorkout lhs, UserWorkout rhs) {
                    return (int) (rhs.getCompletedOn() - lhs.getCompletedOn());
                }
            });

            workoutAdapterActive = new MyUserWorkoutAdapter(inputActive, getApplicationContext());
            recyclerViewActive.setAdapter(workoutAdapterActive);

            workoutAdapterCompleted = new MyUserWorkoutAdapter(inputCompleted, getApplicationContext());
            recyclerViewCompleted.setAdapter(workoutAdapterCompleted);
        });
    }

    private void initializeButtons() {
        cancelButton =  findViewById(R.id.goBackButtom);
        newWorkoutButton =  findViewById(R.id.newWorkoutButton);
        logOutButton =  findViewById(R.id.logOutButton);
        myTraineesButton =  findViewById(R.id.myTraineeButton);

        if(userId.equals(BackendAPI.userId)) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), OverviewTraineesActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        if(userId.equals(BackendAPI.userId)) {
            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackendAPI.ResetJWT();
                    BackendAPI.userId = null;
                    BackendAPI.myRoles.clear();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else logOutButton.setVisibility(View.GONE);

        if( (BackendAPI.myRoles.contains("coach") || BackendAPI.myRoles.contains("admin") ) && userId.equals(BackendAPI.userId)) {
            myTraineesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), OverviewTraineesActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            myTraineesButton.setVisibility(View.GONE);
        }

        if(BackendAPI.myRoles.contains("coach") || BackendAPI.myRoles.contains("admin")) {
            newWorkoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), WorkoutCreationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("userId", userId);
                    intent.putExtra("workoutId", 0);
                    startActivity(intent);
                }
            });
        }
        else {
            newWorkoutButton.setVisibility(View.GONE);
        }
    }
}