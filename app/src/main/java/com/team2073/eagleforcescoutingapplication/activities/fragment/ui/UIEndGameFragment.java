package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;

public class UIEndGameFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;
    private String endgamePreferenceVal = "endgameState";

    private ImageView straightClimb;
    private ImageView tiltedClimb;
    private ImageButton robotStateParked;
    private ImageButton robotStateUnbalanced;

    private ImageView robotStateClimbNull;
    private ImageButton robotStateClimb;
    private ImageButton robotStateBalanced;
    private ImageButton robotStateBalanced2;
    private ImageButton robotStateBalanced3;

    private static int imageChangeCounter;

    private ArrayList<String> fieldNames = new ArrayList<>();

    public static UIEndGameFragment newInstance(int index) {
        UIEndGameFragment fragment = new UIEndGameFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
        scoutingFormPresenter = new ScoutingFormPresenter(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_endgame, container, false);

        //Straight Climb View
        straightClimb = root.findViewById(R.id.climb_bar_straight);

        //Titled Climb View
        tiltedClimb = root.findViewById(R.id.climb_bar_tilt);
        tiltedClimb.setRotation(20);
        tiltedClimb.setVisibility(View.GONE);

        //Robot View
        robotStateParked = root.findViewById(R.id.robot_state_parked);

        robotStateBalanced = root.findViewById(R.id.robot_state_balanced);
        robotStateBalanced.setVisibility(View.GONE);

        robotStateUnbalanced = root.findViewById(R.id.robot_state_unbalanced);
        robotStateUnbalanced.setVisibility(View.GONE);

        robotStateClimb = root.findViewById(R.id.robot_state_climbed);
        robotStateClimb.setVisibility(View.VISIBLE);

        robotStateClimbNull = root.findViewById(R.id.robot_state_climbNull);
        robotStateClimbNull.setVisibility(View.INVISIBLE);

        robotStateBalanced2 = root.findViewById(R.id.robot_state_balanced2);
        robotStateBalanced2.setVisibility(View.GONE);

        robotStateBalanced3 = root.findViewById(R.id.robot_state_balanced3);
        robotStateBalanced3.setVisibility(View.GONE);
        imageChangeCounter = 1;

        initalWriteToPreferences();

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        robotStateParked.setOnClickListener((View V) -> {
            balancedClimb();
        });

        robotStateBalanced.setOnClickListener((View v) -> {

        });

        robotStateUnbalanced.setOnClickListener((View v) -> {
            robotStateUnbalanced.setVisibility(View.GONE);
            tiltedClimb.setVisibility(View.GONE);
            robotStateParked.setVisibility(View.VISIBLE);
            straightClimb.setVisibility(View.VISIBLE);
            scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 1);
        });
        //Start of new Button from Afraz
        robotStateClimb.setOnClickListener((View v) -> {


            if (imageChangeCounter == 0) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 1);
                robotStateClimb.setVisibility(View.VISIBLE);
                robotStateClimbNull.setVisibility(View.INVISIBLE);
                imageChangeCounter++;
            }
           else if (imageChangeCounter == 1) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 2);
                robotStateBalanced.setVisibility(View.VISIBLE);
                imageChangeCounter++;

            } else if (imageChangeCounter == 2) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 3);
                robotStateBalanced2.setVisibility(View.VISIBLE);
                imageChangeCounter++;

            } else if (imageChangeCounter == 3) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 4);
                robotStateBalanced3.setVisibility(View.VISIBLE);
                imageChangeCounter++;

            } else if (imageChangeCounter == 4){
                imageChangeCounter = 0;
                robotStateClimbNull.setVisibility(View.VISIBLE);
                robotStateBalanced2.setVisibility(View.INVISIBLE);
                robotStateBalanced3.setVisibility(View.INVISIBLE);
                robotStateBalanced.setVisibility(View.INVISIBLE);
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 0);
            }
        });

    }

    private void balancedClimb(){
        robotStateParked.setVisibility(View.GONE);
        robotStateBalanced.setVisibility(View.VISIBLE);
        scoutingFormPresenter.writeToPreferences(endgamePreferenceVal,2);
    }

    private void initalWriteToPreferences(){
        scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 0);
    }
}
