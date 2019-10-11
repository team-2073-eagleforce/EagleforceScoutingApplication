package com.team2073.eagleforcescoutingapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.Match;

import java.nio.ByteBuffer;
import java.util.List;

public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<Match> listOfTeams;

    public ScheduleRecyclerViewAdapter(Context context, List<Match> listOfTeams) {
        this.context = context;
        this.listOfTeams = listOfTeams;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.match_cell, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int matchNumber = position + 1;
        holder.qualNumber.setText("Qual " + matchNumber);
        holder.red1.setText(String.valueOf(listOfTeams.get(position).getRed1()));
        holder.red2.setText(String.valueOf(listOfTeams.get(position).getRed2()));
        holder.red3.setText(String.valueOf(listOfTeams.get(position).getRed3()));
        holder.blue1.setText(String.valueOf(listOfTeams.get(position).getRed1()));
        holder.blue2.setText(String.valueOf(listOfTeams.get(position).getRed2()));
        holder.blue3.setText(String.valueOf(listOfTeams.get(position).getRed3()));
    }

    @Override
    public int getItemCount() {
        return listOfTeams.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

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
