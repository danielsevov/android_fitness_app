package com.personal.lifttoliveapp.adapters.workoutCreationPage;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.personal.lifttoliveapp.R;
import com.personal.lifttoliveapp.entities.WorkSet;

import java.util.ArrayList;
import java.util.List;

public class MyWorkSetAdapter extends RecyclerView.Adapter<MyWorkSetAdapter.ViewHolder> {
    public ArrayList<WorkSet> values;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View layout;
        public TextView title;
        public EditText kilos, reps;
        public CheckBox isComplete;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = v.findViewById(R.id.discardButton);
            kilos = v.findViewById(R.id.kiloText);
            reps = v.findViewById(R.id.repsText);
            isComplete = v.findViewById(R.id.isCompleteCheckBox);
        }
    }

    public void add(int position, WorkSet item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    public List<WorkSet> getValues() {
        return values;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyWorkSetAdapter(ArrayList<WorkSet> myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.workset_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        int position = holder.getAbsoluteAdapterPosition();

        holder.kilos.setText(values.get(position).getKilos() + "");
        holder.reps.setText(values.get(position).getReps() + "");

        holder.kilos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                values.get(position).setKilos(holder.kilos.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        holder.reps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                values.get(position).setReps(holder.reps.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.isComplete.setChecked(values.get(position).isComplete());
        holder.isComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values.get(position).setComplete(holder.isComplete.isChecked());
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
