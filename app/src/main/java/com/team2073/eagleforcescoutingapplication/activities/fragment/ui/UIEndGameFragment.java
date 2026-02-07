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

    private ImageButton[] buttons;

    private void initDataFields() {

        buttons = new ImageButton[]{
                fragmentEndgameBinding.bottomLeftEndgame,
                fragmentEndgameBinding.bottomCenterEndgame,
                fragmentEndgameBinding.bottomRightEndgame,
                fragmentEndgameBinding.middleLeftEndgame,
                fragmentEndgameBinding.middleCenterEndgame,
                fragmentEndgameBinding.middleRightEndgame,
                fragmentEndgameBinding.topLeftEndgame,
                fragmentEndgameBinding.topCenterEndgame,
                fragmentEndgameBinding.topRightEndgame,
                fragmentEndgameBinding.climbButtonEndgame
        };

        for (ImageButton btn : buttons) {
            btn.setTag(false);     // not selected
            btn.setAlpha(1f);      // fully visible
            btn.setOnClickListener(v -> toggleClimb((ImageButton) v));
        }
    }


    public void toggleClimb(ImageButton b) {

        // 1️⃣ Reset ALL buttons
        for (ImageButton btn : buttons) {
            btn.setTag(false);
            btn.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start();
        }

        // 2️⃣ Activate ONLY the clicked button
        b.setTag(true);
        b.animate()
                .alpha(0.3f)
                .setDuration(200)
                .start();

        // 3️⃣ Save data
        if (b.equals(fragmentEndgameBinding.bottomLeftEndgame)) {
            saveData("endClimb", "1");
        } else if (b.equals(fragmentEndgameBinding.climbButtonEndgame)) {
            saveData("endClimb", "0");
        } else if (b.equals(fragmentEndgameBinding.bottomCenterEndgame)) {
            saveData("endClimb", "2");
        } else if (b.equals(fragmentEndgameBinding.bottomRightEndgame)) {
            saveData("endClimb", "3");
        } else if (b.equals(fragmentEndgameBinding.middleLeftEndgame)) {
            saveData("endClimb", "4");
        } else if (b.equals(fragmentEndgameBinding.middleCenterEndgame)) {
            saveData("endClimb", "5");
        } else if (b.equals(fragmentEndgameBinding.middleRightEndgame)) {
            saveData("endClimb", "6");
        } else if (b.equals(fragmentEndgameBinding.topLeftEndgame)) {
            saveData("endClimb", "7");
        } else if (b.equals(fragmentEndgameBinding.topCenterEndgame)) {
            saveData("endClimb", "8");
        } else if (b.equals(fragmentEndgameBinding.topRightEndgame)) {
            saveData("endClimb", "9");
        }

        Timber.d("pressed");
    }


    public String readData(String key) {
        return scoutingFormPresenter.readData(key);
    }
    public void saveData(String key, String data) {
        scoutingFormPresenter.saveData(key, data);
    }
}