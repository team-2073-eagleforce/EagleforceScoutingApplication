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
import java.util.Objects;
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
                toggleElement((ImageButton) gridOneTopLeftCone, "Cone"));
        fragmentAutoBinding.gridOneTopCube.setOnClickListener(gridOneTopCube ->
                toggleElement((ImageButton) gridOneTopCube, "Cube"));
        fragmentAutoBinding.gridOneTopRightCone.setOnClickListener(gridOneTopRightCone ->
                toggleElement((ImageButton) gridOneTopRightCone, "Cone"));

        fragmentAutoBinding.gridTwoTopLeftCone.setOnClickListener(gridTwoTopLeftCone ->
                toggleElement((ImageButton) gridTwoTopLeftCone, "Cone"));
        fragmentAutoBinding.gridTwoTopCube.setOnClickListener(gridTwoTopCube ->
                toggleElement((ImageButton) gridTwoTopCube, "Cube"));
        fragmentAutoBinding.gridTwoTopRightCone.setOnClickListener(gridTwoTopRightCone ->
                toggleElement((ImageButton) gridTwoTopRightCone, "Cone"));

        fragmentAutoBinding.gridThreeTopLeftCone.setOnClickListener(gridThreeTopLeftCone ->
                toggleElement((ImageButton) gridThreeTopLeftCone, "Cone"));
        fragmentAutoBinding.gridThreeTopCube.setOnClickListener(gridThreeTopCube ->
                toggleElement((ImageButton) gridThreeTopCube, "Cube"));
        fragmentAutoBinding.gridThreeTopRightCone.setOnClickListener(gridThreeTopRightCone ->
                toggleElement((ImageButton) gridThreeTopRightCone, "Cone"));

        //Middle Grid
        fragmentAutoBinding.gridOneMiddleLeftCone.setOnClickListener(gridOneMiddleLeftCone ->
                toggleElement((ImageButton) gridOneMiddleLeftCone, "Cone"));
        fragmentAutoBinding.gridOneMiddleCube.setOnClickListener(gridOneMiddleCube ->
                toggleElement((ImageButton) gridOneMiddleCube, "Cube"));
        fragmentAutoBinding.gridOneMiddleRightCone.setOnClickListener(gridOneMiddleRightCone ->
                toggleElement((ImageButton) gridOneMiddleRightCone, "Cone"));

        fragmentAutoBinding.gridTwoMiddleLeftCone.setOnClickListener(gridTwoMiddleLeftCone ->
                toggleElement((ImageButton) gridTwoMiddleLeftCone, "Cone"));
        fragmentAutoBinding.gridTwoMiddleCube.setOnClickListener(gridTwoMiddleCube ->
                toggleElement((ImageButton) gridTwoMiddleCube, "Cube"));
        fragmentAutoBinding.gridTwoMiddleRightCone.setOnClickListener(gridTwoMiddleRightCone ->
                toggleElement((ImageButton) gridTwoMiddleRightCone, "Cone"));

        fragmentAutoBinding.gridThreeMiddleLeftCone.setOnClickListener(gridThreeTopLeftCone ->
                toggleElement((ImageButton) gridThreeTopLeftCone, "Cone"));
        fragmentAutoBinding.gridThreeMiddleCube.setOnClickListener(gridThreeMiddleCube ->
                toggleElement((ImageButton) gridThreeMiddleCube, "Cube"));
        fragmentAutoBinding.gridThreeMiddleRightCone.setOnClickListener(gridThreeMiddleRightCone ->
                toggleElement((ImageButton) gridThreeMiddleRightCone, "Cone"));

        //Bottom Grid
        fragmentAutoBinding.gridOneBottomLeftHybrid.setOnClickListener(gridOneBottomLeftHybrid ->
                toggleElement((ImageButton) gridOneBottomLeftHybrid, "Hybrid"));
        fragmentAutoBinding.gridOneBottomMiddleHybrid.setOnClickListener(gridOneBottomMiddleHybrid ->
                toggleElement((ImageButton) gridOneBottomMiddleHybrid, "Hybrid"));
        fragmentAutoBinding.gridOneBottomRightHybrid.setOnClickListener(gridOneBottomRightHybrid ->
                toggleElement((ImageButton) gridOneBottomRightHybrid, "Hybrid"));

        fragmentAutoBinding.gridTwoBottomLeftHybrid.setOnClickListener(gridTwoBottomLeftHybrid ->
                toggleElement((ImageButton) gridTwoBottomLeftHybrid, "Hybrid"));
        fragmentAutoBinding.gridTwoBottomMiddleHybrid.setOnClickListener(gridTwoBottomMiddleHybrid ->
                toggleElement((ImageButton) gridTwoBottomMiddleHybrid, "Hybrid"));
        fragmentAutoBinding.gridTwoBottomRightHybrid.setOnClickListener(gridTwoBottomRightHybrid ->
                toggleElement((ImageButton) gridTwoBottomRightHybrid, "Hybrid"));

        fragmentAutoBinding.gridThreeBottomLeftHybrid.setOnClickListener(gridThreeBottomLeftHybrid ->
                toggleElement((ImageButton) gridThreeBottomLeftHybrid, "Hybrid"));
        fragmentAutoBinding.gridThreeBottomMiddleHybrid.setOnClickListener(gridThreeBottomMiddleHybrid ->
                toggleElement((ImageButton) gridThreeBottomMiddleHybrid, "Hybrid"));
        fragmentAutoBinding.gridThreeBottomRightHybrid.setOnClickListener(gridThreeBottomRightHybrid ->
                toggleElement((ImageButton) gridThreeBottomRightHybrid, "Hybrid"));
    }

    private void toggleElement(ImageButton elementImage, String indicator){
        String imageButtonName = elementImage.getTag().toString();
        String retrievedImage = "";
        int id;

        switch (indicator) {
            case "Cube":
                switch (scoutingFormPresenter.readData(imageButtonName)) {
                    case "0":
                        scoutingFormPresenter.saveData(imageButtonName, "2");
                        imageButtonName += "2";
                        break;
                    case "2":
                        scoutingFormPresenter.saveData(imageButtonName, "0");
                        imageButtonName += "0";
                        break;
                }
                break;
            case "Cone":
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
                break;
            case "Hybrid":
                switch (scoutingFormPresenter.readData(imageButtonName)) {
                    case "0":
                        scoutingFormPresenter.saveData(imageButtonName,"1");
                        imageButtonName += "1";
                        break;
                    case "1":
                        scoutingFormPresenter.saveData(imageButtonName, "2");
                        imageButtonName += "2";
                        break;
                    case "2":
                        scoutingFormPresenter.saveData(imageButtonName, "0");
                        imageButtonName += "0";
                        break;
                }
                break;
        }

        try {
            Properties properties = new Properties();
            AssetManager assetManager = requireContext().getAssets();
            InputStream inputStream = assetManager.open("grid.properties");
            properties.load(inputStream);
            retrievedImage = properties.getProperty(imageButtonName);
        } catch (IOException e) {
            e.fillInStackTrace();
        }

        id = getResources().getIdentifier(retrievedImage, "drawable", requireContext().getPackageName());
        elementImage.setImageResource(id);
    }

//    private void toggleCube(ImageButton cubeImage) {
//        String imageButtonName = cubeImage.getTag().toString();
//        String retrievedImage = "";
//        int id;
//
//        switch (scoutingFormPresenter.readData(imageButtonName)) {
//            case "0":
//                scoutingFormPresenter.saveData(imageButtonName, "2");
//                imageButtonName += "2";
//                break;
//            case "2":
//                scoutingFormPresenter.saveData(imageButtonName, "0");
//                imageButtonName += "0";
//                break;
//        }
//        System.out.println(imageButtonName);
//        //TODO Move to Scouting Presenter or separate class
//        try {
//            Properties properties = new Properties();
//            AssetManager assetManager = getContext().getAssets();
//            InputStream inputStream = assetManager.open("grid.properties");
//            properties.load(inputStream);
//            retrievedImage = properties.getProperty(imageButtonName);
//        } catch (IOException e) {
//            e.fillInStackTrace();
//        }
//
//        id = getResources().getIdentifier(retrievedImage, "drawable", requireContext().getPackageName());
//        cubeImage.setImageResource(id);
//    }

    private void toggleElement(ImageButton hybridImage) {
    }

}

