package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentEndgameBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIEndGameFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentEndgameBinding fragmentEndgameBinding;
    private AddSubtractValuesBinding defensePerform;
    private AddSubtractValuesBinding driverPerform;


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
        //driverPerform = fragmentEndgameBinding.driverPerformance;
        //defensePerform = fragmentEndgameBinding.defensePerformance;
        return fragmentEndgameBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDataFields();
        initTextFields();
        toggleClimb();
        toggleTrap();
        //togglePerformanceRatings();
        //editTextToggle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentEndgameBinding = null;
    }

    private void initDataFields() {
        scoutingFormPresenter.saveData("endStageClimb", "0");

    }

    private void initTextFields() {
        driverPerform.formField.setText(getResources().getString(R.string.driver_performance));
        driverPerform.formScore.setText("0");

        defensePerform.formField.setText(getResources().getString(R.string.defense_performance));
        defensePerform.formScore.setText("0");
    }

    private void toggleClimb() {
        fragmentEndgameBinding.endStageClimb.setOnClickListener(stageClimb ->
                fragmentEndgameBinding.endStageClimb.setImageResource(scoutingFormPresenter.toggleClimb("endStageClimb")));
    }

    //TODO Add the trap layout
    private void toggleTrap() {
        //fragmentEndgameBinding.endTrap.setOnClickListener(trap -> fragmentEndgameBinding.endTrap.setImageResource());
    }


    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            } catch (Exception e) {
                Timber.d("setUserVisibleHint: EndGame ");
            }
        }
    }
}


