package com.team2073.eagleforcescoutingapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ScoutingFormRecyclerViewAdapter extends RecyclerView.Adapter<ScoutingFormRecyclerViewAdapter.DataViewHolder> {

    private static final String TAG = ScoutingFormRecyclerViewAdapter.class.getSimpleName();
    private ArrayList<String> fieldName;
    private Context activity;
    private PrefsDataManager prefsDataManager;

    public ScoutingFormRecyclerViewAdapter(ArrayList<String> fieldName, Activity activity) {
        this.fieldName = fieldName;
        this.activity = activity;
        prefsDataManager = PrefsDataManager.getInstance(activity);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_subtract_values, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Timber.d("onBindViewHolder: called");

        holder.field.setText(fieldName.get(position));
        holder.add.setOnClickListener(view -> {
            Integer value = Integer.parseInt(holder.data.getText().toString()) + 1;
            holder.data.setText(value.toString());

            prefsDataManager.writeToPreferences(fieldName.get(position), value.toString());
            prefsDataManager.commitToPreferences();
            Timber.d("shared Preferences: " + fieldName.get(position) + ", " + prefsDataManager.readFromPreferences(fieldName.get(position)));
        });
        holder.subtract.setOnClickListener(view -> {
            Integer value = Integer.parseInt(holder.data.getText().toString()) - 1;
            if(value < 0){
                value = 0;
            }
            holder.data.setText(value.toString());


            prefsDataManager.writeToPreferences(fieldName.get(position), value.toString());
            prefsDataManager.commitToPreferences();

            Timber.d("shared Preferences: " + fieldName.get(position) + ", " + prefsDataManager.readFromPreferences(fieldName.get(position)));
        });

        holder.data.setOnFocusChangeListener((view, b) -> {
            if(!b){
                prefsDataManager.writeToPreferences(fieldName.get(position), holder.data.getText().toString());
                Timber.d("shared Preferences: " + fieldName.get(position) + ", " + prefsDataManager.readFromPreferences(fieldName.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return fieldName.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.addSubtractConstraint) ConstraintLayout layout;
        @BindView(R.id.formAdd) ImageButton add;
        @BindView(R.id.formSubtract) ImageButton subtract;
        @BindView(R.id.formEditText) EditText data;
        @BindView(R.id.formField) TextView field;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
