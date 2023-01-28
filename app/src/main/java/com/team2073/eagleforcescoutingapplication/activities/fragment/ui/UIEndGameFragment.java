package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentEndgameBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

public class UIEndGameFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private ScoutingFormPresenter scoutingFormPresenter;

    private UiFragmentEndgameBinding fragmentEndgameBinding;


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
        PageViewModel pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
        scoutingFormPresenter = new ScoutingFormPresenter(this.getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentEndgameBinding = UiFragmentEndgameBinding.inflate(inflater, container, false);
        View view = fragmentEndgameBinding.getRoot();
        initFields();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentEndgameBinding.chargingStation.setOnClickListener(view1 -> {changeEndState();});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentEndgameBinding = null;
    }

    private void initFields() {
        scoutingFormPresenter.saveData("Charging Station", "0");
    }

    public void changeEndState() {
        switch (scoutingFormPresenter.readData("Charging Station")){
            case "0":
                fragmentEndgameBinding.chargingStation.setImageResource(R.drawable.community_zone);
                scoutingFormPresenter.saveData("Charging Station", "1");
                break;
            case "1":
                fragmentEndgameBinding.chargingStation.setImageResource(R.drawable.docked);
                scoutingFormPresenter.saveData("Charging Station", "2");
                break;
            case "2":
                fragmentEndgameBinding.chargingStation.setImageResource(R.drawable.engaged);
                scoutingFormPresenter.saveData("Charging Station", "3");
                break;
            case "3":
                fragmentEndgameBinding.chargingStation.setImageResource(R.drawable.dotted_box2);
                scoutingFormPresenter.saveData("Charging Station", "0");
                break;
        }

    }

}
