package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;

public class UIEndGameFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private String leveledPreferenceVal = "levelState";
    private String endgamePreferenceVal = "climbState";

    private ImageView straightClimb;
    private ImageView tiltedClimb;

    private ImageButton robotStateBalanced;
    private ImageButton robotStateLeveled;
    private ImageButton robotStateUnbalanced;
    private Button robotChangeLevelButton;

    private ImageView robotStateClimbNull;
    private ImageButton robotStateClimb;
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
//        robotStateLeveled = root.findViewById(R.id.robot_change_level_button);
//        robotStateLeveled.setVisibility(View.VISIBLE);

        robotStateBalanced = root.findViewById(R.id.robot_state_balanced);
        robotStateBalanced.setVisibility(View.GONE);

        robotStateUnbalanced = root.findViewById(R.id.robot_state_unbalanced);
        robotStateUnbalanced.setVisibility(View.GONE);

        robotChangeLevelButton = root.findViewById(R.id.robot_change_level_button);
        robotChangeLevelButton.setBackgroundColor(Color.TRANSPARENT);


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


//        changeLevelButton.setOnClickListener((View v) ->{
//           changeLevelState();
//        });
        robotChangeLevelButton.setOnClickListener((View v) -> {
            if (Integer.parseInt(scoutingFormPresenter.readFromPreferences(endgamePreferenceVal)) != 0 && Integer.parseInt(scoutingFormPresenter.readFromPreferences(endgamePreferenceVal)) != 1) {
//                if (Integer.parseInt(scoutingFormPresenter.readFromPreferences(leveledPreferenceVal)) == 0) {
//                    tiltedClimb.setVisibility(View.VISIBLE);
//                    straightClimb.setVisibility(View.INVISIBLE);
//                    scoutingFormPresenter.writeToPreferences(leveledPreferenceVal, 2);
//                } else if
               if (Integer.parseInt(scoutingFormPresenter.readFromPreferences(leveledPreferenceVal)) == 1) {
                    tiltedClimb.setVisibility(View.VISIBLE);
                    straightClimb.setVisibility(View.INVISIBLE);
                    scoutingFormPresenter.writeToPreferences(leveledPreferenceVal, 2);
                } else if (Integer.parseInt(scoutingFormPresenter.readFromPreferences(leveledPreferenceVal)) == 2) {
                    tiltedClimb.setVisibility(View.INVISIBLE);
                    straightClimb.setVisibility(View.VISIBLE);
                    scoutingFormPresenter.writeToPreferences(leveledPreferenceVal, 1);
                }
            }
            });


        robotStateClimb.setOnClickListener((View v) -> {


            if (imageChangeCounter == 0) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 1);
                robotStateClimb.setVisibility(View.VISIBLE);
                robotStateClimbNull.setVisibility(View.INVISIBLE);
                defaultClimb();
                imageChangeCounter++;
            } else if (imageChangeCounter == 1) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 2);
                robotStateBalanced.setVisibility(View.VISIBLE);
                balanceScore();
                imageChangeCounter++;

            } else if (imageChangeCounter == 2) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 3);
                robotStateBalanced2.setVisibility(View.VISIBLE);
                balanceScore();
                imageChangeCounter++;

            } else if (imageChangeCounter == 3) {
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 4);
                robotStateBalanced3.setVisibility(View.VISIBLE);
                balanceScore();
                imageChangeCounter++;


            } else if (imageChangeCounter == 4) {
                imageChangeCounter = 0;
                robotStateClimbNull.setVisibility(View.VISIBLE);
                robotStateBalanced2.setVisibility(View.INVISIBLE);
                robotStateBalanced3.setVisibility(View.INVISIBLE);
                robotStateBalanced.setVisibility(View.INVISIBLE);
                defaultClimb();
                scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 0);
            }
        });

    }


    private void changeLevelState(){
        Integer current = Integer.parseInt(scoutingFormPresenter.readFromPreferences(leveledPreferenceVal));
        if(current == 0){
            balancedClimb();
        }else if(current == 1){
            unBalancedClimb();
        }
    }

    private void balancedClimb(){
        robotStateBalanced.setVisibility(View.VISIBLE);
        robotStateUnbalanced.setVisibility(View.GONE);
        tiltedClimb.setVisibility(View.GONE);
        straightClimb.setVisibility(View.VISIBLE);
        scoutingFormPresenter.writeToPreferences(leveledPreferenceVal,1);
    }

    private void unBalancedClimb(){
        robotStateBalanced.setVisibility(View.GONE);
        straightClimb.setVisibility(View.GONE);
        robotStateUnbalanced.setVisibility(View.VISIBLE);
        tiltedClimb.setVisibility(View.VISIBLE);
        scoutingFormPresenter.writeToPreferences(leveledPreferenceVal, 0);
    }

    private void initalWriteToPreferences(){
        scoutingFormPresenter.writeToPreferences(leveledPreferenceVal, 1);
        scoutingFormPresenter.writeToPreferences(endgamePreferenceVal, 0);
    }
    private void defaultClimb() {
        scoutingFormPresenter.writeToPreferences(leveledPreferenceVal, 0);
        tiltedClimb.setVisibility(View.INVISIBLE);
        straightClimb.setVisibility(View.VISIBLE);
    }
    private void balanceScore() {
        if (Integer.parseInt(scoutingFormPresenter.readFromPreferences(leveledPreferenceVal)) == 0) {
            scoutingFormPresenter.writeToPreferences(leveledPreferenceVal, 1);
        }
    }
}
