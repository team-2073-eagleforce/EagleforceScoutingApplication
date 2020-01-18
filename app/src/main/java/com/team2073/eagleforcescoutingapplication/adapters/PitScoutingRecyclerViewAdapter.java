package com.team2073.eagleforcescoutingapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;

import java.util.ArrayList;

import timber.log.Timber;

public class PitScoutingRecyclerViewAdapter extends RecyclerView.Adapter<PitScoutingRecyclerViewAdapter.DataViewHolder> {

    private ArrayList<String> fieldNames;
    private Context activity;
    private PrefsDataManager prefsDataManager;
    private View.OnClickListener onClickListener;

    public PitScoutingRecyclerViewAdapter(ArrayList<String> fieldNames, Activity activity, View.OnClickListener onClickListener) {
        this.fieldNames = fieldNames;
        this.activity = activity;
        this.onClickListener = onClickListener;
        prefsDataManager = PrefsDataManager.getInstance(activity);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == R.layout.add_subtract_values) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_subtract_values, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_pit_scouting_submit, parent, false);
        }

        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {

        if(position == fieldNames.size()) {
            holder.pitSubmit.setOnClickListener(onClickListener);

        } else {
            holder.field.setText(fieldNames.get(position));
            holder.add.setOnClickListener(view -> {
                Integer value = Integer.parseInt(holder.data.getText().toString()) + 1;
                holder.data.setText(value.toString());

                prefsDataManager.writeToPreferences(fieldNames.get(position), value.toString());
                prefsDataManager.commitToPreferences();
                Timber.d("shared Preferences: " + fieldNames.get(position) + ", " + prefsDataManager.readFromPreferences(fieldNames.get(position)));
            });
            holder.subtract.setOnClickListener(view -> {
                Integer value = Integer.parseInt(holder.data.getText().toString()) - 1;
                if (value < 0) {
                    value = 0;
                    Toast.makeText(activity, "Can't Have Negative Values", Toast.LENGTH_SHORT).show();
                }
                holder.data.setText(value.toString());


                prefsDataManager.writeToPreferences(fieldNames.get(position), value.toString());
                prefsDataManager.commitToPreferences();

                Timber.d("shared Preferences: " + fieldNames.get(position) + ", " + prefsDataManager.readFromPreferences(fieldNames.get(position)));
            });

            holder.data.setOnFocusChangeListener((view, b) -> {
                if (!b) {
                    prefsDataManager.writeToPreferences(fieldNames.get(position), holder.data.getText().toString());
                    Timber.d("shared Preferences: " + fieldNames.get(position) + ", " + prefsDataManager.readFromPreferences(fieldNames.get(position)));
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == fieldNames.size()) ? R.layout.button_pit_scouting_submit : R.layout.add_subtract_values;
    }

    @Override
    public int getItemCount() {
        return fieldNames.size() + 1;
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout;
        ImageButton add;
        ImageButton subtract;
        EditText data;
        TextView field;

        Button pitSubmit;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.addSubtractConstraint);
            add = itemView.findViewById(R.id.formAdd);
            subtract = itemView.findViewById(R.id.formSubtract);
            data = itemView.findViewById(R.id.formEditText);
            field = itemView.findViewById(R.id.formField);

            pitSubmit = itemView.findViewById(R.id.pitScoutingSubmit);
        }
    }
}
