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
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentEndgameBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

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
        return fragmentEndgameBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDataFields();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentEndgameBinding = null;
    }

    private void initDataFields() {
        fragmentEndgameBinding.cage.setOnClickListener(cage -> toggleClimb());
    }

    public void toggleClimb() {
        int drawable = 0;
        switch (readData("climb")) {
            case "0":
                saveData("climb", "1");
                drawable = R.drawable.cage_park;
                break;
            case "1":
                saveData("climb", "2");
                drawable = R.drawable.cage_shallow;
                break;
            case "2":
                saveData("climb", "3");
                drawable = R.drawable.cage_deep;
                break;
            case "3":
                saveData("climb", "0");
                drawable = R.drawable.cage;
                break;
        }
        Timber.d("Climb:%s", readData("climb"));
        fragmentEndgameBinding.cage.setImageResource(drawable);
    }

    public String readData(String key) {
        return scoutingFormPresenter.readData(key);
    }
    public void saveData(String key, String data) {
        scoutingFormPresenter.saveData(key, data);
    }
}


