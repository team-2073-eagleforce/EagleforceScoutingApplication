package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.FragmentViewModel;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentEndgameBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentTeleopBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIEndGameFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentEndgameBinding fragmentEndgameBinding;
    private FragmentViewModel viewModel;
    //private AddSubtractValuesBinding defensePerform;
    //private AddSubtractValuesBinding driverPerform;


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
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentEndgameBinding = UiFragmentEndgameBinding.inflate(inflater, container, false);
        viewModel.getTrapNumber().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    if (integer == 1) {
                        if (readData("trapOne").equals("1"))
                            fragmentEndgameBinding.endTrap.trapOne.setImageResource(R.drawable.filled_trap_box);
                        if (readData("trapTwo").equals("1"))
                            fragmentEndgameBinding.endTrap.trapTwo.setImageResource(R.drawable.filled_trap_box);
                        if (readData("trapThree").equals("1"))
                            fragmentEndgameBinding.endTrap.trapThree.setImageResource(R.drawable.filled_trap_box);
                    }
                    if (integer == 0) {
                        if (readData("trapOne").equals("0"))
                            fragmentEndgameBinding.endTrap.trapOne.setImageResource(R.drawable.empty_trap_box);
                        if (readData("trapTwo").equals("0"))
                            fragmentEndgameBinding.endTrap.trapTwo.setImageResource(R.drawable.empty_trap_box);
                        if (readData("trapThree").equals("0"))
                            fragmentEndgameBinding.endTrap.trapThree.setImageResource(R.drawable.empty_trap_box);
                    }
                }
            }
        });
        //driverPerform = fragmentEndgameBinding.driverPerformance;
        //defensePerform = fragmentEndgameBinding.defensePerformance;
        return fragmentEndgameBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDataFields();
        //initTextFields();
        toggleClimb();
        fragmentEndgameBinding.endTrap.trapOne.setOnClickListener(endTrapOne -> toggle_trap(fragmentEndgameBinding.endTrap.trapOne, "trapOne"));
        fragmentEndgameBinding.endTrap.trapTwo.setOnClickListener(endTrapTwo -> toggle_trap(fragmentEndgameBinding.endTrap.trapTwo, "trapTwo"));
        fragmentEndgameBinding.endTrap.trapThree.setOnClickListener(endTrapThree -> toggle_trap(fragmentEndgameBinding.endTrap.trapThree, "trapThree"));
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

    //private void initTextFields() {
        //driverPerform.formField.setText(getResources().getString(R.string.driver_performance));
        //driverPerform.formScore.setText("0");
    //    defensePerform.formField.setText(getResources().getString(R.string.defense_performance));
    //    defensePerform.formScore.setText("0");
   // }


    private void toggleClimb() {
        fragmentEndgameBinding.endStageClimb.setOnClickListener(stageClimb ->
                fragmentEndgameBinding.endStageClimb.setImageResource(scoutingFormPresenter.toggleClimb("endStageClimb")));

    }



    private void toggle_trap(ImageButton trapButtonEndgame, String trapNumber) {
        if (readData(trapNumber).equals("0")) {
            trapButtonEndgame.setImageResource(R.drawable.filled_trap_box);
            saveData(trapNumber, "1");
            updateViewModel(1);
        } else {
            trapButtonEndgame.setImageResource(R.drawable.empty_trap_box);
            saveData(trapNumber, "0");
            updateViewModel(0);

        }
        Timber.d("%s:%s", trapNumber, scoutingFormPresenter.readData(trapNumber));

    }

    public String readData(String key) {
        return scoutingFormPresenter.readData(key);
    }

    public void saveData(String key, String data) {
        scoutingFormPresenter.saveData(key, data);
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

    private void updateViewModel(Integer integer) {
        viewModel.setTrapNumber(integer);
    }
}


