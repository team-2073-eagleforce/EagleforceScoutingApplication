package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIEndGameFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_SECTION_NUMBER = "Detail";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private ImageView unleveled;
    private ImageView leveled;

    private ImageView climb1;
    private ImageView climb2;
    private ImageView climb3;
    private ImageView climb4;

    private Button changeLevelButton;
    private Button climbButton;


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

        leveled = root.findViewById(R.id.climb_bar_straight);
        unleveled = root.findViewById(R.id.climb_bar_tilt);

        leveled = root.findViewById(R.id.climb2);

        climb1 = root.findViewById(R.id.climb1);
        climb2 = root.findViewById(R.id.climb2);
        climb3 = root.findViewById(R.id.climb3);
        climb4 = root.findViewById(R.id.climb4);

        climbButton = root.findViewById(R.id.climb_button);
        changeLevelButton = root.findViewById(R.id.level_button);
        changeLevelButton.setBackgroundColor(Color.TRANSPARENT);

        initFields();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeLevelButton.setOnClickListener(this);
        climbButton.setOnClickListener(this);

    }

    private void initFields() {
        scoutingFormPresenter.saveData("Climb", "0");
        scoutingFormPresenter.saveData("Level", "0");

        leveled.setVisibility(View.GONE);
        unleveled.setVisibility(View.GONE);
        climb1.setVisibility(View.GONE);
        climb2.setVisibility(View.GONE);
        climb3.setVisibility(View.GONE);
        climb4.setVisibility(View.GONE);

        unleveled.setRotation(20);
        unleveled.setVisibility(View.GONE);

        climbButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        Integer value = 0;
        switch(view.getId()){
            case R.id.level_button:
                value = Math.abs(Integer.parseInt(scoutingFormPresenter.readData("Auto Cross")) - 1);
                if (value == 1) {
                    unleveled.setVisibility(View.VISIBLE);
                    leveled.setVisibility(View.GONE);
                } else {
                    leveled.setVisibility(View.VISIBLE);
                    unleveled.setVisibility(View.GONE);
                }
                scoutingFormPresenter.saveData("Level", value.toString());

                Timber.d("shared Preferences: " + "Level" + ", " + scoutingFormPresenter.readData("Level"));
                break;
            case R.id.climb_button:
                value = Integer.parseInt(scoutingFormPresenter.readData("Climb") + 1);
                if(value == 5) {
                    value = 0;
                }
                if(value == 0) {
                    climb1.setVisibility(View.GONE);
                    climb2.setVisibility(View.GONE);
                    climb3.setVisibility(View.GONE);
                    climb4.setVisibility(View.GONE);
                } else if(value == 1) {
                    climb1.setVisibility(View.VISIBLE);
                    climb2.setVisibility(View.GONE);
                    climb3.setVisibility(View.GONE);
                    climb4.setVisibility(View.GONE);
                }else if(value == 2) {
                    climb1.setVisibility(View.GONE);
                    climb2.setVisibility(View.VISIBLE);
                    climb3.setVisibility(View.GONE);
                    climb4.setVisibility(View.GONE);
                }else if(value == 3){
                    climb1.setVisibility(View.GONE);
                    climb2.setVisibility(View.VISIBLE);
                    climb3.setVisibility(View.VISIBLE);
                    climb4.setVisibility(View.GONE);
                }else if(value == 4){
                    climb1.setVisibility(View.GONE);
                    climb2.setVisibility(View.VISIBLE);
                    climb3.setVisibility(View.VISIBLE);
                    climb4.setVisibility(View.VISIBLE);
                }
                scoutingFormPresenter.saveData("Climb", value.toString());
                Timber.d("shared Preferences: " + "Climb" + ", " + scoutingFormPresenter.readData("Climb"));

        }
    }
}
