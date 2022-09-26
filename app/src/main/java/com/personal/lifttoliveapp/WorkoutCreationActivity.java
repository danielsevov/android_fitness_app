package com.personal.lifttoliveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.personal.lifttoliveapp.API.BackendAPI;
import com.personal.lifttoliveapp.API.PropertyConstructor;
import com.personal.lifttoliveapp.adapters.workoutCreationPage.MyExerciseSetAdapter;
import com.personal.lifttoliveapp.adapters.workoutCreationPage.MyWorkSetAdapter;
import com.personal.lifttoliveapp.entities.ExerciseSet;
import com.personal.lifttoliveapp.entities.WorkSet;
import com.personal.lifttoliveapp.misc.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

public class WorkoutCreationActivity extends AppCompatActivity {
    private String traineeId;
    private int workoutId = 0, deleteTaps = 0;
    private EditText workoutName, workoutNote;

    private RecyclerView storedExercisesRecyclerView;
    private MyExerciseSetAdapter exerciseSetAdapter;
    private LinearLayoutManager exercisesLayoutManager;

    private Button saveExerciseButton, goBackButton, saveWorkoutButton, deleteButton;

    private ArrayList<String> exercises;
    private ArrayList<ExerciseSet> storedExerciseSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_creation);

        prepareExercises();
        initializeRecyclerViews();
        initializeButtons();
    }

    private void prepareExercises() {
        exercises = new ArrayList<>();
        BackendAPI.Send(null, "exercises", "GET", (data) -> {
            try {
                JSONArray arr = data.getJSONArray("data");
                int ix = 0;

                while (true) {
                    if (ix == arr.length()) break;
                    JSONObject cur = arr.getJSONObject(ix++);
                    System.out.println(cur.get("name"));
                    exercises.add(cur.get("name").toString());
                }
                clearForm();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeButtons() {
        saveExerciseButton = findViewById(R.id.saveExerciseButton);
        goBackButton = findViewById(R.id.cancelButton);
        saveWorkoutButton = findViewById(R.id.saveWorkoutButton);
        deleteButton = findViewById(R.id.deleteWorkoutButton);

        saveWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWorkout();
            }
        });

        if(BackendAPI.myRoles.contains("admin") || BackendAPI.myRoles.contains("coach")) {
            saveExerciseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exerciseSetAdapter.values.add(new ExerciseSet("", "My note"));
                    exerciseSetAdapter.notifyDataSetChanged();
                }
            });

            if(workoutId != 0) {
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTaps++;
                        if(deleteTaps >= 2) {
                            BackendAPI.Send(null, "workouts/" + workoutId, "DELETE");
                            goBackButton.performClick();
                        }
                        else {
                            WorkoutCreationActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Click again to delete workout!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
            else {
                deleteButton.setVisibility(View.GONE);
            }
        }
        else {
            saveExerciseButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TraineeProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", traineeId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeRecyclerViews() {
        Bundle b = getIntent().getExtras();
        traineeId = b.getString("userId");
        workoutId = b.getInt("workoutId");

        workoutName = findViewById(R.id.workoutCreationNameText);
        workoutNote = findViewById(R.id.workoutNoteText);
        storedExercisesRecyclerView = findViewById(R.id.storedSetsRecyclerView);

        clearForm();

        if(BackendAPI.myRoles.contains("admin") || BackendAPI.myRoles.contains("coach")) {
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                    new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                                target) {
                            int fromPosition = viewHolder.getAdapterPosition();
                            int toPosition = target.getAdapterPosition();
                            Collections.swap(exerciseSetAdapter.values, fromPosition, toPosition);
                            exerciseSetAdapter.notifyDataSetChanged();
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                            exerciseSetAdapter.remove(viewHolder.getAbsoluteAdapterPosition());
                        }
                    };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(storedExercisesRecyclerView);
        }
    }

    private void clearForm() {
        storedExerciseSets = new ArrayList<>();

        if(workoutId != 0) {
            BackendAPI.Send(null, "workouts/" + workoutId, "GET", (res) -> {

                try{

                    workoutName.setText(res.getString("workout_name"));
                    workoutNote.setText(res.getString("coach_note"));

                    if(!BackendAPI.myRoles.contains("admin") && !BackendAPI.myRoles.contains("coach")) {
                        workoutName.setEnabled(false);
                        workoutNote.setEnabled(false);
                    }

                    JSONArray sets = res.getJSONArray("sets");
                    int i = 0;
                    while(true) {
                        if(!sets.getJSONObject(i).has("exercise")) break;

                        JSONObject set = sets.getJSONObject(i++);
                        boolean isComplete = set.getBoolean("is_completed");
                        ArrayList<WorkSet> worksets = new ArrayList<>();
                        JSONArray rps, kgs;
                        rps = set.getJSONArray("reps");
                        kgs = set.getJSONArray("kilos");

                        for(int j =0; j< kgs.length(); j++) {
                            worksets.add(new WorkSet(rps.getString(j), kgs.getString(j), isComplete));
                        }

                        storedExerciseSets.add(new ExerciseSet(set.getString("exercise"), set.getString("set_note"), worksets));
                    }
                }
                 catch (Exception e) {

                 }

                storedExercisesRecyclerView.setHasFixedSize(false);
                exercisesLayoutManager = new LinearLayoutManager(this);
                storedExercisesRecyclerView.setLayoutManager(exercisesLayoutManager);
                exerciseSetAdapter = new MyExerciseSetAdapter(storedExerciseSets, getApplicationContext(), exercises, false);
                storedExercisesRecyclerView.setAdapter(exerciseSetAdapter);
            });
        }
        else {
            storedExerciseSets.add(new ExerciseSet("", "My note"));
            storedExercisesRecyclerView.setHasFixedSize(false);
            exercisesLayoutManager = new LinearLayoutManager(this);
            storedExercisesRecyclerView.setLayoutManager(exercisesLayoutManager);
            exerciseSetAdapter = new MyExerciseSetAdapter(storedExerciseSets, getApplicationContext(), exercises, true);
            storedExercisesRecyclerView.setAdapter(exerciseSetAdapter);
        }
    }

    private void saveWorkout() {
        String name, exnote;
        final boolean[] mistakes = {false};

        if (workoutName.getText().toString().isEmpty()) {
            mistakes[0] = true;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Valid name must be selected!", Toast.LENGTH_SHORT).show();

                }
            });
            return;
        } else name = workoutName.getText().toString();

        if (workoutNote.getText().length() > 0) exnote = workoutNote.getText().toString();

        ArrayList<ExerciseSet> finalSets = new ArrayList<>();

        exerciseSetAdapter.getValues().forEach(s -> {

            if(!exercises.contains(s.getExercise())) {
                mistakes[0] = true;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(),  s.getExercise() + " is not a valid exercise!", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            ArrayList<WorkSet> newWorkSets = new ArrayList<>();

            for(WorkSet set : s.getWorksets()) {
                try {
                    int rps, kgs;
                    rps = Integer.parseInt(set.getReps());
                    kgs = Integer.parseInt(set.getKilos());
                    if(rps > 0 && kgs >= 0) {
                        newWorkSets.add(new WorkSet(set.getReps(), set.getKilos(), set.isComplete()));
                    }
                    else {
                        this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error in a set entry for " + s.getExercise() + "!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mistakes[0] = true;
                    }
                }
                catch (Exception e) {
                }
            }

            if(newWorkSets.size() <= 0) {
                mistakes[0] = true;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "No valid sets entered for " + s.getExercise() + "!", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            finalSets.add(new ExerciseSet(s.getExercise(), s.getNote(), newWorkSets));
        });

        System.out.println(finalSets);

        if (finalSets.size() <= 0) {
            mistakes[0] = true;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "At least one valid exercise must be entered!", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        if(!mistakes[0]) {
            storedExerciseSets = finalSets;
            persistWorkout();
        }
    }

    private void persistWorkout() {
        PropertyConstructor workout = new PropertyConstructor();
        ArrayList<Boolean> complete = new ArrayList<>();
        BackendAPI.Send(null, "whoAmI", "GET", (res) -> {
            String myId;
            try {
                myId = res.getString("userId");
                workout.addProperty("userId", traineeId).addProperty("coachId", myId)
                        .addProperty("workout_name", workoutName.getText().toString()).addProperty("coach_note", workoutNote.getText().toString());

                ArrayList<PropertyConstructor> sets = new ArrayList<>();
                storedExerciseSets.forEach(s -> {
                    ArrayList<String> kilo = new ArrayList<>(), reps = new ArrayList<>();
                    ArrayList<Boolean> bools = new ArrayList<>();
                    s.getWorksets().forEach(w -> {
                        kilo.add(w.getKilos());
                        reps.add(w.getReps());
                        bools.add(w.isComplete());
                    });

                    PropertyConstructor set = new PropertyConstructor().addProperty("exercise", s.getExercise())
                    .addProperty("set_note", s.note).addProperty("is_completed", !bools.contains(false))
                    .addProperty("kilos", kilo).addProperty("reps", reps);

                    complete.add(!bools.contains(false));

                    sets.add(set);
                });

                workout.addProperty("sets", sets);

                if(complete.contains(false)) {
                    workout.addProperty("completed_on", "");
                }
                else {
                    workout.addProperty("completed_on", String.valueOf(Instant.now().getEpochSecond()));
                }

                System.out.println(workout.construct());

                if(workoutId == 0) BackendAPI.Send(workout, "workouts", "POST");
                else BackendAPI.Send(workout, "workouts/" + workoutId, "PATCH");

                goBackButton.performClick();
                finish();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}