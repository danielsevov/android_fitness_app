package com.personal.lifttoliveapp.adapters.profilePage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personal.lifttoliveapp.R;
import com.personal.lifttoliveapp.TraineeProfileActivity;
import com.personal.lifttoliveapp.entities.User;

import java.util.List;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.ViewHolder> {
    private List<User> values;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public FloatingActionButton visitButton;
        public TextView nameText, emailText;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            visitButton = (FloatingActionButton) v.findViewById(R.id.visitButton);
            nameText = v.findViewById(R.id.firstLineText);
            emailText = v.findViewById(R.id.secondLineText);
        }
    }

    public void add(int position, User item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyUserAdapter(List<User> myDataset, Context context) {
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
                inflater.inflate(R.layout.trainee_item, parent, false);

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
        holder.nameText.setText(values.get(position).getName());
        holder.emailText.setText(values.get(position).getEmail());

        holder.visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TraineeProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", values.get(position).getEmail());
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
