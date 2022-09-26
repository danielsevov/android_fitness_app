package com.personal.lifttoliveapp.adapters.workoutCreationPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.personal.lifttoliveapp.API.BackendAPI;
import com.personal.lifttoliveapp.R;
import com.personal.lifttoliveapp.entities.ExerciseSet;
import com.personal.lifttoliveapp.entities.WorkSet;

import java.util.ArrayList;

public class MyExerciseSetAdapter extends RecyclerView.Adapter<MyExerciseSetAdapter.ViewHolder> {
    public ArrayList<ExerciseSet> values;
    public Context context;
    public ArrayList<String> exercises;
    private boolean isNewWorkout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View layout;
        public AutoCompleteTextView exercise;
        public EditText note;
        public Button newSetButton;
        public TextView number;
        public LinearLayout setLayout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            exercise = v.findViewById(R.id.exerciseNameText);
            note = v.findViewById(R.id.exerciseNoteText);
            setLayout = v.findViewById(R.id.exerciseSetsText);
            number = v.findViewById(R.id.exerciseNumText);
            newSetButton = v.findViewById(R.id.addNewSetButton);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void add(int position, ExerciseSet item) {
        values.add(position, item);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public ArrayList<ExerciseSet> getValues() {
        return values;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyExerciseSetAdapter(ArrayList<ExerciseSet> myDataset, Context context, ArrayList<String> exercises, boolean isNewWorkout) {
        values = myDataset;
        this.context = context;
        this.exercises = exercises;
        this.isNewWorkout = isNewWorkout;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.exercise_set_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        int position = holder.getAbsoluteAdapterPosition();

        if(BackendAPI.myRoles.contains("admin") || BackendAPI.myRoles.contains("coach")) {
            holder.exercise.setEnabled(true);
            holder.note.setEnabled(true);
        }
        else {
            holder.exercise.setEnabled(false);
            holder.note.setEnabled(false);
        }

        holder.number.setText("Exercise " + (position + 1));

        holder.exercise.setText(values.get(position).getExercise());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_dropdown_item, exercises);
        holder.exercise.setThreshold(1);//will start working from first character
        holder.exercise.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        holder.exercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                values.get(position).setExercise(holder.exercise.getText().toString());
            }
        });
        holder.exercise.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && position < getItemCount()) {
                    if (!exercises.contains(holder.exercise.getText().toString().toLowerCase()) ) {
                        holder.exercise.setText("");
                        values.get(position).setExercise("");
                    }

                    else values.get(position).setExercise(holder.exercise.getText().toString());
                }
            }
        });

        String n = values.get(position).getNote();
        holder.note.setText(n);
        holder.note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                values.get(position).setNote(holder.note.getText().toString());
            }
        });

        if(BackendAPI.myRoles.contains("admin") || BackendAPI.myRoles.contains("coach")) {
            holder.newSetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    values.get(position).getWorksets().add(new WorkSet("0", "0"));
                    notifyDataSetChanged();
                }
            });
        }
        else {
            holder.newSetButton.setVisibility(View.GONE);
        }

        holder.setLayout.removeAllViews();

        int j =0;
        for(WorkSet w : values.get(position).getWorksets()) {
            LinearLayout linLay = new LinearLayout(context);
            linLay.setOrientation(LinearLayout.HORIZONTAL);

            TextView goBackView, setView, repsView, xView, kgsView;
            EditText rps, kgs;
            CheckBox checkBox;

            if(BackendAPI.myRoles.contains("admin") || BackendAPI.myRoles.contains("coach")) {
                goBackView = new TextView(context);
                goBackView.setText("X   ");
                goBackView.setTextSize(20);
                goBackView.setTextColor(Color.RED);
                goBackView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        values.get(position).getWorksets().remove(w);
                        notifyDataSetChanged();
                    }
                });
                linLay.addView(goBackView);
            }

            setView = new TextView(context);
            setView.setText("Set " + ++j + ":   ");
            linLay.addView(setView);

            repsView = new TextView(context);
            repsView.setText("Reps: ");
            linLay.addView(repsView);

            rps = new EditText(context);
            rps.setInputType(InputType.TYPE_CLASS_NUMBER);
            rps.setText(w.getReps());
            rps.setWidth(150);
            int finalJ = j;
            rps.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    values.get(position).getWorksets().get(finalJ -1).setReps(rps.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            linLay.addView(rps);

            xView = new TextView(context);
            xView.setText("    X   ");
            linLay.addView(xView);

            kgsView = new TextView(context);
            kgsView.setText("KGs: ");
            linLay.addView(kgsView);

            kgs = new EditText(context);
            kgs.setInputType(InputType.TYPE_CLASS_NUMBER);
            kgs.setText(w.getKilos());
            kgs.setWidth(150);
            int finalJ1 = j;
            kgs.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    values.get(position).getWorksets().get(finalJ1 -1).setKilos(kgs.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            linLay.addView(kgs);

            checkBox = new CheckBox(context);
            checkBox.setChecked(w.isComplete());
            int finalJ2 = j;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    values.get(position).getWorksets().get(finalJ2-1).setComplete(checkBox.isChecked());
                }
            });
            if(isNewWorkout) {
                checkBox.setEnabled(false);
                checkBox.setVisibility(View.GONE);
            }
            linLay.addView(checkBox);

            holder.setLayout.addView(linLay);
        }
        holder.setLayout.setBackground(null);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
