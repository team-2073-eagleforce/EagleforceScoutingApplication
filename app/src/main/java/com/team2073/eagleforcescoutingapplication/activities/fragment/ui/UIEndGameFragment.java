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
        driverPerform = fragmentEndgameBinding.driverPerformance;
        defensePerform = fragmentEndgameBinding.defensePerformance;
        return fragmentEndgameBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDataFields();
        initTextFields();
        toggleClimb();
        togglePerformanceRatings();
        editTextToggle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentEndgameBinding = null;
    }

    private void initDataFields() {
        scoutingFormPresenter.saveData("endChargingStation", "0");

        scoutingFormPresenter.saveData("driverRanking", "0");
        scoutingFormPresenter.saveData("defenseRanking", "0");
    }

    private void initTextFields() {
        driverPerform.formField.setText(getResources().getString(R.string.driver_performance));
        driverPerform.formScore.setText("0");

        defensePerform.formField.setText(getResources().getString(R.string.defense_performance));
        defensePerform.formScore.setText("0");
    }

    private void toggleClimb() {
        fragmentEndgameBinding.endChargingStation.setOnClickListener(chargingStation ->
                fragmentEndgameBinding.endChargingStation.setImageResource(scoutingFormPresenter.toggleClimb("endChargingStation")));
    }

    private void togglePerformanceRatings() {
        driverPerform.formAdd.setOnClickListener(addDriverPerformance ->
                addPerformanceValue(driverPerform.formScore, "driverRanking"));
        driverPerform.formSubtract.setOnClickListener(subtractDriverPerformance ->
                subtractPerformanceValue(driverPerform.formScore, "driverRanking"));

        defensePerform.formAdd.setOnClickListener(addDefensePerformance ->
                addPerformanceValue(defensePerform.formScore, "defenseRanking"));
        defensePerform.formSubtract.setOnClickListener(subtractDefensePerformance ->
                subtractPerformanceValue(defensePerform.formScore, "defenseRanking"));
    }

    private void addPerformanceValue(TextView formScore, String performanceType) {
        int value = Integer.parseInt(formScore.getText().toString()) + 1;
        if (value > 5) {
            value = 5;
        }
        formScore.setText(String.valueOf(value));

        scoutingFormPresenter.saveData(performanceType, String.valueOf(value));

        Timber.d(performanceType + ", " + scoutingFormPresenter.readData(performanceType));
    }

    private void subtractPerformanceValue(TextView formScore, String performanceType) {
        int value = Integer.parseInt(formScore.getText().toString()) - 1;
        if (value < 0) {
            value = 0;
        }
        formScore.setText(String.valueOf(value));

        scoutingFormPresenter.saveData(performanceType, String.valueOf(value));

        Timber.d(performanceType + ", " + scoutingFormPresenter.readData(performanceType));
    }

    private void editTextToggle() {
        fragmentEndgameBinding.uiComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scoutingFormPresenter.saveData("comment", fragmentEndgameBinding.uiComments.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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


