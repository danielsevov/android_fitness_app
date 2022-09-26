package com.personal.lifttoliveapp.adapters.profilePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.WorkSource;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.collection.ArraySet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personal.lifttoliveapp.API.BackendAPI;
import com.personal.lifttoliveapp.API.PropertyConstructor;
import com.personal.lifttoliveapp.R;
import com.personal.lifttoliveapp.TraineeProfileActivity;
import com.personal.lifttoliveapp.WorkoutCreationActivity;
import com.personal.lifttoliveapp.adapters.workoutCreationPage.MyExerciseSetAdapter;
import com.personal.lifttoliveapp.entities.ExerciseSet;
import com.personal.lifttoliveapp.entities.UserWorkout;
import com.personal.lifttoliveapp.entities.WorkSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyUserWorkoutAdapter extends RecyclerView.Adapter<MyUserWorkoutAdapter.ViewHolder> {
    private List<UserWorkout> values;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public FloatingActionButton visitButton, copyButton, deleteButton;
        public TextView nameText, createdOnText, completedOnText;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            visitButton = (FloatingActionButton) v.findViewById(R.id.visitButton);
            copyButton = v.findViewById(R.id.copyButton);
            deleteButton = v.findViewById(R.id.deleteWorkoutItemButton);
            nameText = v.findViewById(R.id.firstLineText);
            createdOnText = v.findViewById(R.id.secondLineText);
            completedOnText = v.findViewById(R.id.completedOnText);
        }
    }

    public void add(int position, UserWorkout item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyUserWorkoutAdapter(List<UserWorkout> myDataset, Context context) {
        values = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.trainee_workout_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        int position = holder.getAbsoluteAdapterPosition();
        int workoutId = values.get(position).getId();
        holder.nameText.setText(values.get(position).getName());

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        if(values.get(position).getCompletedOn() == 0) {
            holder.completedOnText.setText(R.string.incomplete);
        }
        else {
            date.setTime((long) values.get(position).getCompletedOn());
            holder.completedOnText.setText(String.format("completed on: %s", dateFormat.format(date)));
        }

        date.setTime((long) values.get(position).getCreatedOn());
        holder.createdOnText.setText(String.format("created on: %s", dateFormat.format(date)));

        holder.visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WorkoutCreationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", values.get(position).getUserId());
                intent.putExtra("workoutId", values.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackendAPI.Send(null, "workouts/" + workoutId, "DELETE");

                Intent intent = new Intent(context, TraineeProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", values.get(position).getUserId());
                context.startActivity(intent);
            }
        });

        holder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BackendAPI.Send(null, "workouts/" + workoutId, "GET", (res) -> {
                    String wkname, wknote, userId, coachId;
                    ArraySet<ExerciseSet> storedExerciseSets = new ArraySet<>();

                    try{
                        wkname = res.getString("workout_name");
                        wknote = res.getString("coach_note");
                        userId = res.getString("userId");
                        coachId = res.getString("coachId");

                        JSONArray sets = res.getJSONArray("sets");
                        int i = 0;
                        while(true) {
                            if(i >= sets.length()) break;

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

                        PropertyConstructor workout = new PropertyConstructor();
                        ArrayList<Boolean> complete = new ArrayList<>();
                        workout.addProperty("userId", userId).addProperty("coachId", coachId)
                                .addProperty("workout_name", wkname + " (Copy)").addProperty("coach_note", wknote);

                        ArrayList<PropertyConstructor> setsArray = new ArrayList<>();
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

                            setsArray.add(set);
                        });

                        workout.addProperty("sets", setsArray);

                        if(complete.contains(false)) {
                            workout.addProperty("completed_on", "");
                        }
                        else {
                            workout.addProperty("completed_on", String.valueOf(Instant.now().getEpochSecond()));
                        }

                        BackendAPI.Send(workout, "workouts", "POST");

                        Intent intent = new Intent(context, TraineeProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userId", userId);
                        context.startActivity(intent);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                });
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
