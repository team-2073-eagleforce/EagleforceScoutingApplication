package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIEndGameFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private ImageButton bottomClimb;
    private ImageButton middleClimb;
    private ImageButton middleClimb2;
    private ImageButton topClimb;

    private ImageButton bottomBar;
    private ImageButton middleBar;
    private ImageButton middleBar2;
    private ImageButton topBar;


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

        bottomClimb = root.findViewById(R.id.BottomClimb);
        middleClimb = root.findViewById(R.id.MiddleClimb);
        middleClimb2 = root.findViewById(R.id.MiddleClimb2);
        topClimb = root.findViewById(R.id.TopClimb);

        bottomBar = root.findViewById(R.id.BottomBar);
        middleBar = root.findViewById(R.id.MiddleBar);
        middleBar2 = root.findViewById(R.id.MiddleBarTwo);
        topBar = root.findViewById(R.id.TopBar);

        initFields();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bottomClimb.setOnClickListener(this);
        bottomBar.setOnClickListener(this);
        bottomClimb.setTag("0");

        middleClimb.setOnClickListener(this);
        middleBar.setOnClickListener(this);
        middleClimb.setTag("0");

        middleClimb2.setOnClickListener(this);
        middleBar2.setOnClickListener(this);
        middleClimb2.setTag("0");

        topClimb.setOnClickListener(this);
        topBar.setOnClickListener(this);
        topClimb.setTag("0");

    }

    private void initFields() {
        scoutingFormPresenter.saveData("Climb", "0");
    }

    public void reset(){
        bottomBar.setImageResource(R.drawable.climberbutton);
        bottomClimb.setTag("0");

        middleBar.setImageResource(R.drawable.climberbutton);
        middleClimb.setTag("0");

        middleBar2.setImageResource(R.drawable.climberbutton);
        middleClimb2.setTag("0");

        topBar.setImageResource(R.drawable.climbertopbutton);
        topClimb.setTag("0");
    }

    @Override
    public void onClick(View v) {
        Integer value = 0;

        switch (v.getId()){

            case R.id.BottomClimb:
            case R.id.BottomBar:
                if (bottomClimb.getTag()=="1"){
                    reset();
                    value = 0;
                } else {
                    reset();
                    bottomBar.setImageResource(R.drawable.climberbutton_yellow);
                    bottomClimb.setTag("1");
                    value = 1;
                }
                scoutingFormPresenter.saveData("Climb", value.toString());

                Timber.d("shared Preferences: " + "Climb" + ", " + scoutingFormPresenter.readData("Climb"));
                break;

            case R.id.MiddleClimb:
            case R.id.MiddleBar:
                if (middleClimb.getTag()=="1"){
                    reset();
                    value = 0;
                } else {
                    reset();
                    middleBar.setImageResource(R.drawable.climberbutton_yellow);
                    middleClimb.setTag("1");
                    value = 2;
                }
                scoutingFormPresenter.saveData("Climb", value.toString());

                Timber.d("shared Preferences: " + "Climb" + ", " + scoutingFormPresenter.readData("Climb"));
                break;

            case R.id.MiddleClimb2:
            case R.id.MiddleBarTwo:
                if (middleClimb2.getTag()=="1"){
                    reset();
                    value = 0;
                } else {
                    reset();
                    middleBar2.setImageResource(R.drawable.climberbutton_yellow);
                    middleClimb2.setTag("1");
                    value = 3;
                }

                scoutingFormPresenter.saveData("Climb", value.toString());

                Timber.d("shared Preferences: " + "Climb" + ", " + scoutingFormPresenter.readData("Climb"));
                break;

            case R.id.TopClimb:
            case R.id.TopBar:
                if (topClimb.getTag()=="1"){
                    reset();
                    value = 0;
                } else {
                    reset();
                    topBar.setImageResource(R.drawable.climberbutton_yellow);
                    topClimb.setTag("1");
                    value = 4;
                }
                scoutingFormPresenter.saveData("Climb", value.toString());

                Timber.d("shared Preferences: " + "Climb" + ", " + scoutingFormPresenter.readData("Climb"));
                break;

        }

    }
}
