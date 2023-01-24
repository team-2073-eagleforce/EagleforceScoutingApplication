package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.content.res.AssetManager;
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
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentAutoBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentAutoBinding fragmentAutoBinding;
    private Context context;


    public static UIAutoFragment newInstance(int index) {
        UIAutoFragment fragment = new UIAutoFragment();
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
        fragmentAutoBinding = UiFragmentAutoBinding.inflate(inflater, container, false);
        return fragmentAutoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initDataFields();
        initViewLabels();
        initViewImageButtons();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentAutoBinding = null;
    }

    private void initDataFields() {
        String[] gridViews = getResources().getStringArray(R.array.gridViews);
        for (String gridView : gridViews) {
            scoutingFormPresenter.saveData(gridView, "0");
        }
    }

    private void initViewLabels() {
//        autoUpperLabel.setText(getResources().getString(R.string.num_cargo_upper_hub_label));
//        autoLowerLabel.setText(getResources().getString(R.string.num_cargo_lower_hub_label));
    }

    private void initViewImageButtons() {
        //Top Grid
        fragmentAutoBinding.gridOneTopLeftCone.setOnClickListener(gridOneTopLeftCone ->
                toggleCone((ImageButton) gridOneTopLeftCone));
        fragmentAutoBinding.gridOneTopCube.setOnClickListener(gridOneTopCube ->
                toggleCube((ImageButton) gridOneTopCube));
//        fragmentAutoBinding.gridOneTopRightCone.setOnClickListener(gridOneTopRightConeView -> {toggleCone();});
//
//        fragmentAutoBinding.gridTwoTopLeftCone.setOnClickListener(gridTwoTopLeftConeView -> {toggleCone();});
//        fragmentAutoBinding.gridTwoTopCube.setOnClickListener(gridTwoTopCubeView -> {toggleCube();});
//        fragmentAutoBinding.gridTwoTopRightCone.setOnClickListener(gridTwoTopRightConeView -> {toggleCone();});
//
//        fragmentAutoBinding.gridThreeTopLeftCone.setOnClickListener(gridThreeTopLeftConeView -> {toggleCone();});
//        fragmentAutoBinding.gridThreeTopCube.setOnClickListener(gridThreeTopCubeView -> {toggleCube();});
//        fragmentAutoBinding.gridThreeTopRightCone.setOnClickListener(gridThreeTopRightConeView -> {toggleCone();});
//
//        //Middle Grid
//        fragmentAutoBinding.gridOneMiddleLeftCone.setOnClickListener(gridOneTopLeftConeView -> {toggleCone();});
//        fragmentAutoBinding.gridOneMiddleCube.setOnClickListener(gridOneTopCubeView -> {toggleCube();});
//        fragmentAutoBinding.gridOneMiddleRightCone.setOnClickListener(gridOneTopRightConeView -> {toggleCone();});
//
//        fragmentAutoBinding.gridTwoMiddleLeftCone.setOnClickListener(gridTwoTopLeftConeView -> {toggleCone();});
//        fragmentAutoBinding.gridTwoMiddleCube.setOnClickListener(gridTwoTopCubeView -> {toggleCube();});
//        fragmentAutoBinding.gridTwoMiddleRightCone.setOnClickListener(gridTwoTopRightConeView -> {toggleCone();});
//
//        fragmentAutoBinding.gridThreeMiddleLeftCone.setOnClickListener(gridThreeTopLeftConeView -> {toggleCone();});
//        fragmentAutoBinding.gridThreeTopCube.setOnClickListener(gridThreeTopCubeView -> {toggleCube();});
//        fragmentAutoBinding.gridThreeTopRightCone.setOnClickListener(gridThreeTopRightConeView -> {toggleCone();});
//
//        //Bottom Grid
//        fragmentAutoBinding.gridOneBottomLeftHybrid.setOnClickListener(gridOneBottomLeftHybridView -> {hybridToggle();});
//        fragmentAutoBinding.gridOneBottomMiddleHybrid.setOnClickListener(gridOneBottomMiddleHybridView -> {hybridToggle();});
//        fragmentAutoBinding.gridOneBottomRightHybrid.setOnClickListener(gridOneBottomRightHybridView -> {hybridToggle();});
//
//        fragmentAutoBinding.gridTwoBottomLeftHybrid.setOnClickListener(gridTwoBottomLeftHybridView -> {hybridToggle();});
//        fragmentAutoBinding.gridTwoBottomMiddleHybrid.setOnClickListener(gridTwoBottomMiddleHybridView -> {hybridToggle();});
//        fragmentAutoBinding.gridTwoBottomRightHybrid.setOnClickListener(gridTwoBottomRightHybridView -> {hybridToggle();});
//
//        fragmentAutoBinding.gridThreeBottomLeftHybrid.setOnClickListener(gridThreeBottomLeftHybridView -> {hybridToggle();});
//        fragmentAutoBinding.gridThreeBottomMiddleHybrid.setOnClickListener(gridThreeBottomMiddleHybridView -> {hybridToggle();});
//        fragmentAutoBinding.gridThreeBottomRightHybrid.setOnClickListener(gridThreeBottomRightHybridView -> {hybridToggle();});
    }

    private void toggleCone(ImageButton coneImage) {
    }

    private void toggleCube(ImageButton cubeImage) {
        String imageButtonName = cubeImage.getTag().toString();
        String retrievedImage = "";
        int id;

        switch (scoutingFormPresenter.readData(imageButtonName)) {
            case "0":
                scoutingFormPresenter.saveData(imageButtonName, "1");
                imageButtonName += "1";
                break;
            case "1":
                scoutingFormPresenter.saveData(imageButtonName, "0");
                imageButtonName += "0";
                break;
        }
        System.out.println(imageButtonName);
        //TODO Move to Scouting Presenter or separate class
        try {
            Properties properties = new Properties();
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = assetManager.open("grid.properties");
            properties.load(inputStream);
            retrievedImage = properties.getProperty(imageButtonName);
        } catch (IOException e) {
            e.fillInStackTrace();
        }

        id = getResources().getIdentifier(retrievedImage, "drawable", requireContext().getPackageName());
        cubeImage.setImageResource(id);
    }

    private void hybridToggle(ImageButton hybridImage) {
    }

}

