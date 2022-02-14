package com.team2073.eagleforcescoutingapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.util.Match;
import com.team2073.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;

import java.util.List;

/**
 * Manages all the ViewHolder objects for the schedule. Uses the ViewHolder that's instantiated in this class.
 */
public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder> {

            private Context context;
            private List<Match> listOfTeams;
            private PrefsDataManager prefsDataManager;
            private Activity activity;

            public ScheduleRecyclerViewAdapter(Context context, List<Match> listOfTeams, PrefsDataManager prefsDataManager, Activity activity) {
                this.context = context;
                this.listOfTeams = listOfTeams;
                this.prefsDataManager = prefsDataManager;
                this.activity = activity;
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.match_cell, parent, false);
                final ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }

            /**
             *  Binds the schedule's ViewHolder instances to specific data.
             * @param holder
             * @param position
             */
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                Integer matchNumber = position + 1;
                holder.qualNumber.setText("Qual " + matchNumber);
                holder.red1.setText(String.valueOf(listOfTeams.get(position).getRed1()));
                holder.red2.setText(String.valueOf(listOfTeams.get(position).getRed2()));
                holder.red3.setText(String.valueOf(listOfTeams.get(position).getRed3()));
                holder.blue1.setText(String.valueOf(listOfTeams.get(position).getBlue1()));
                holder.blue2.setText(String.valueOf(listOfTeams.get(position).getBlue2()));
                holder.blue3.setText(String.valueOf(listOfTeams.get(position).getBlue3()));

                holder.red1.setOnClickListener(view -> {
                    prefsDataManager.writeToPreferences("teamNumber", listOfTeams.get(position).getRed1());
                    prefsDataManager.writeToPreferences("matchNumber", matchNumber.toString());
                    activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                });
                holder.red2.setOnClickListener(view -> {
                    prefsDataManager.writeToPreferences("teamNumber", listOfTeams.get(position).getRed2());
                    prefsDataManager.writeToPreferences("matchNumber", matchNumber.toString());
                    activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                });
                holder.red3.setOnClickListener(view -> {
                    prefsDataManager.writeToPreferences("teamNumber", listOfTeams.get(position).getRed3());
                    prefsDataManager.writeToPreferences("matchNumber", matchNumber.toString());
                    activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                });
                holder.blue1.setOnClickListener(view -> {
                    prefsDataManager.writeToPreferences("teamNumber", listOfTeams.get(position).getBlue1());
                    prefsDataManager.writeToPreferences("matchNumber", matchNumber.toString());
                    activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                });
                holder.blue2.setOnClickListener(view -> {
                    prefsDataManager.writeToPreferences("teamNumber", listOfTeams.get(position).getBlue2());
                    prefsDataManager.writeToPreferences("matchNumber", matchNumber.toString());
                    activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                });
                holder.blue3.setOnClickListener(view -> {
                    prefsDataManager.writeToPreferences("teamNumber", listOfTeams.get(position).getBlue3());
                    prefsDataManager.writeToPreferences("matchNumber", matchNumber.toString());
                    activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                });
            }

            @Override
            public int getItemCount() {
                return listOfTeams.size();
            }

            /**
             * Creates a ViewHolder for the cells of the schedule.
             * Based on a view called match_cell.xml found in the directory res/layout.
             */
            public class ViewHolder extends RecyclerView.ViewHolder {
                RelativeLayout matchLayout;
                Button red1;
                Button red2;
                Button red3;
                Button blue1;
                Button blue2;
                Button blue3;
                TextView qualNumber;

                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    matchLayout = itemView.findViewById(R.id.match_cell);
                    red1 = itemView.findViewById(R.id.red_team_1_text);
                    red2 = itemView.findViewById(R.id.red_team_2_text);
                    red3 = itemView.findViewById(R.id.red_team_3_text);
                    blue1 = itemView.findViewById(R.id.blue_team_1_text);
                    blue2 = itemView.findViewById(R.id.blue_team_2_text);
                    blue3 = itemView.findViewById(R.id.blue_team_3_text);
                    qualNumber = itemView.findViewById(R.id.match_number);
                }
    }
}
