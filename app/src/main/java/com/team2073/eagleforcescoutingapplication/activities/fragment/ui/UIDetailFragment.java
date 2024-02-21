package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentDetailBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentEndgameBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIDetailFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentDetailBinding fragmentDetailBinding;
    private AddSubtractValuesBinding defensePerform;
    private AddSubtractValuesBinding driverPerform;


    public static UIDetailFragment newInstance(int index) {
        UIDetailFragment fragment = new UIDetailFragment();
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
        fragmentDetailBinding = UiFragmentDetailBinding.inflate(inflater, container, false);
        driverPerform = fragmentDetailBinding.driverPerformance;
        defensePerform = fragmentDetailBinding.defensePerformance;
        return fragmentDetailBinding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDataFields();
        initTextFields();
        togglePerformanceRatings();
        editTextToggle();
        toggleRobotProblems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentDetailBinding = null;
    }

    private void initDataFields() {
        scoutingFormPresenter.saveData("driverRanking", "0");
        scoutingFormPresenter.saveData("defenseRanking", "0");
        scoutingFormPresenter.saveData("isBroken", "0");
        scoutingFormPresenter.saveData("isDisabled", "0");
        scoutingFormPresenter.saveData("isTipped", "0");
    }

    private void initTextFields() {
        driverPerform.formField.setText(getResources().getString(R.string.driver_performance));
        driverPerform.formScore.setText("0");

        defensePerform.formField.setText(getResources().getString(R.string.defense_performance));
        defensePerform.formScore.setText("0");
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

    private void toggleRobotProblems() {
        fragmentDetailBinding.checkBroken.setOnClickListener(checkBroken -> toggle_check("isBroken"));
        fragmentDetailBinding.checkDisabled.setOnClickListener(checkDisabled -> toggle_check("isDisabled"));
        fragmentDetailBinding.checkTipped.setOnClickListener(checkTipped -> toggle_check("isTipped"));
    }

    private void toggle_check(String condition) {
        if (scoutingFormPresenter.readData(condition).equals("0")) {
            scoutingFormPresenter.saveData(condition, "1");
        } else {
            scoutingFormPresenter.saveData(condition, "0");
        }
        Timber.d("%s:%s", condition, scoutingFormPresenter.readData(condition));
    }

    private void editTextToggle() {
        fragmentDetailBinding.uiComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String comment = fragmentDetailBinding.uiComments.getText().toString();
                if (comment.contains("'")) {
                    comment = comment.replace("'", "\"");
                }
                scoutingFormPresenter.saveData("comment", comment);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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


