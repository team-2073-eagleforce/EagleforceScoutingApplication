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
import com.team2073.eagleforcescoutingapplication.databinding.TransportDisplayLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentTeleopBinding;
import com.team2073.eagleforcescoutingapplication.framework.form.ChargedUpScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UITeleopFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentTeleopBinding fragmentTeleopBinding;
    private TransportDisplayLayoutBinding coneTransportBinding;
    private TransportDisplayLayoutBinding cubeTransportBinding;

    private ScoutingForm scoutingForm = new ChargedUpScoutingForm();


    public static UITeleopFragment newInstance(int index) {
        UITeleopFragment fragment = new UITeleopFragment();
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
        fragmentTeleopBinding = UiFragmentTeleopBinding.inflate(inflater, container, false);
        coneTransportBinding = fragmentTeleopBinding.coneTransport;
        cubeTransportBinding = fragmentTeleopBinding.cubeTransport;
        return fragmentTeleopBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDataFields();
        initTextFields();
        initViewImageButtons();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentTeleopBinding = null;
    }

    private void initDataFields() {
        scoutingFormPresenter.saveData("coneTransport", "0");
        scoutingFormPresenter.saveData("cubeTransport", "0");

        for (String teleGridPoint: scoutingForm.getTeleFieldNames()) {
            scoutingFormPresenter.saveData(teleGridPoint, "0");
        }
    }

    private void initTextFields() {
        coneTransportBinding.formField.setText("Cone");
        coneTransportBinding.formScore.setText("0");

        cubeTransportBinding.formField.setText("Cube");
        cubeTransportBinding.formScore.setText("0");
    }

    private void initViewImageButtons() {
        //Top Grid
        fragmentTeleopBinding.gridOneTopLeftCone.setOnClickListener(gridOneTopLeftCone ->
                toggleElement((ImageButton) gridOneTopLeftCone, "Cone"));
        fragmentTeleopBinding.gridOneTopCube.setOnClickListener(gridOneTopCube ->
                toggleElement((ImageButton) gridOneTopCube, "Cube"));
        fragmentTeleopBinding.gridOneTopRightCone.setOnClickListener(gridOneTopRightCone ->
                toggleElement((ImageButton) gridOneTopRightCone, "Cone"));

        fragmentTeleopBinding.gridTwoTopLeftCone.setOnClickListener(gridTwoTopLeftCone ->
                toggleElement((ImageButton) gridTwoTopLeftCone, "Cone"));
        fragmentTeleopBinding.gridTwoTopCube.setOnClickListener(gridTwoTopCube ->
                toggleElement((ImageButton) gridTwoTopCube, "Cube"));
        fragmentTeleopBinding.gridTwoTopRightCone.setOnClickListener(gridTwoTopRightCone ->
                toggleElement((ImageButton) gridTwoTopRightCone, "Cone"));

        fragmentTeleopBinding.gridThreeTopLeftCone.setOnClickListener(gridThreeTopLeftCone ->
                toggleElement((ImageButton) gridThreeTopLeftCone, "Cone"));
        fragmentTeleopBinding.gridThreeTopCube.setOnClickListener(gridThreeTopCube ->
                toggleElement((ImageButton) gridThreeTopCube, "Cube"));
        fragmentTeleopBinding.gridThreeTopRightCone.setOnClickListener(gridThreeTopRightCone ->
                toggleElement((ImageButton) gridThreeTopRightCone, "Cone"));

        //Middle Grid
        fragmentTeleopBinding.gridOneMiddleLeftCone.setOnClickListener(gridOneMiddleLeftCone ->
                toggleElement((ImageButton) gridOneMiddleLeftCone, "Cone"));
        fragmentTeleopBinding.gridOneMiddleCube.setOnClickListener(gridOneMiddleCube ->
                toggleElement((ImageButton) gridOneMiddleCube, "Cube"));
        fragmentTeleopBinding.gridOneMiddleRightCone.setOnClickListener(gridOneMiddleRightCone ->
                toggleElement((ImageButton) gridOneMiddleRightCone, "Cone"));

        fragmentTeleopBinding.gridTwoMiddleLeftCone.setOnClickListener(gridTwoMiddleLeftCone ->
                toggleElement((ImageButton) gridTwoMiddleLeftCone, "Cone"));
        fragmentTeleopBinding.gridTwoMiddleCube.setOnClickListener(gridTwoMiddleCube ->
                toggleElement((ImageButton) gridTwoMiddleCube, "Cube"));
        fragmentTeleopBinding.gridTwoMiddleRightCone.setOnClickListener(gridTwoMiddleRightCone ->
                toggleElement((ImageButton) gridTwoMiddleRightCone, "Cone"));

        fragmentTeleopBinding.gridThreeMiddleLeftCone.setOnClickListener(gridThreeTopLeftCone ->
                toggleElement((ImageButton) gridThreeTopLeftCone, "Cone"));
        fragmentTeleopBinding.gridThreeMiddleCube.setOnClickListener(gridThreeMiddleCube ->
                toggleElement((ImageButton) gridThreeMiddleCube, "Cube"));
        fragmentTeleopBinding.gridThreeMiddleRightCone.setOnClickListener(gridThreeMiddleRightCone ->
                toggleElement((ImageButton) gridThreeMiddleRightCone, "Cone"));

        //Bottom Grid
        fragmentTeleopBinding.gridOneBottomLeftHybrid.setOnClickListener(gridOneBottomLeftHybrid ->
                toggleElement((ImageButton) gridOneBottomLeftHybrid, "Hybrid"));
        fragmentTeleopBinding.gridOneBottomMiddleHybrid.setOnClickListener(gridOneBottomMiddleHybrid ->
                toggleElement((ImageButton) gridOneBottomMiddleHybrid, "Hybrid"));
        fragmentTeleopBinding.gridOneBottomRightHybrid.setOnClickListener(gridOneBottomRightHybrid ->
                toggleElement((ImageButton) gridOneBottomRightHybrid, "Hybrid"));

        fragmentTeleopBinding.gridTwoBottomLeftHybrid.setOnClickListener(gridTwoBottomLeftHybrid ->
                toggleElement((ImageButton) gridTwoBottomLeftHybrid, "Hybrid"));
        fragmentTeleopBinding.gridTwoBottomMiddleHybrid.setOnClickListener(gridTwoBottomMiddleHybrid ->
                toggleElement((ImageButton) gridTwoBottomMiddleHybrid, "Hybrid"));
        fragmentTeleopBinding.gridTwoBottomRightHybrid.setOnClickListener(gridTwoBottomRightHybrid ->
                toggleElement((ImageButton) gridTwoBottomRightHybrid, "Hybrid"));

        fragmentTeleopBinding.gridThreeBottomLeftHybrid.setOnClickListener(gridThreeBottomLeftHybrid ->
                toggleElement((ImageButton) gridThreeBottomLeftHybrid, "Hybrid"));
        fragmentTeleopBinding.gridThreeBottomMiddleHybrid.setOnClickListener(gridThreeBottomMiddleHybrid ->
                toggleElement((ImageButton) gridThreeBottomMiddleHybrid, "Hybrid"));
        fragmentTeleopBinding.gridThreeBottomRightHybrid.setOnClickListener(gridThreeBottomRightHybrid ->
                toggleElement((ImageButton) gridThreeBottomRightHybrid, "Hybrid"));


        //Transport
        coneTransportBinding.formAdd.setOnClickListener(addCone -> {
            int value = Integer.parseInt(coneTransportBinding.formScore.getText().toString()) + 1;
            scoutingFormPresenter.saveData("coneTransport", String.valueOf(value));
            coneTransportBinding.formScore.setText(String.valueOf(value));
            Timber.d("Cone Transport:%s", scoutingFormPresenter.readData("coneTransport"));
        });

        coneTransportBinding.formSubtract.setOnClickListener(addCone -> {
            int value = Integer.parseInt(coneTransportBinding.formScore.getText().toString()) - 1;
            if (value <= 0) {
                value = 0;
            }
            scoutingFormPresenter.saveData("coneTransport", String.valueOf(value));
            coneTransportBinding.formScore.setText(String.valueOf(value));
            Timber.d("Cone Transport:%s", scoutingFormPresenter.readData("coneTransport"));
        });

        cubeTransportBinding.formAdd.setOnClickListener(addCone -> {
            int value = Integer.parseInt(cubeTransportBinding.formScore.getText().toString()) + 1;
            scoutingFormPresenter.saveData("cubeTransport", String.valueOf(value));
            cubeTransportBinding.formScore.setText(String.valueOf(value));
            Timber.d("Cube Transport:%s", scoutingFormPresenter.readData("cubeTransport"));
        });

        cubeTransportBinding.formSubtract.setOnClickListener(addCone -> {
            int value = Integer.parseInt(cubeTransportBinding.formScore.getText().toString()) - 1;
            if (value <= 0) {
                value = 0;
            }
            scoutingFormPresenter.saveData("cubeTransport", String.valueOf(value));
            cubeTransportBinding.formScore.setText(String.valueOf(value));
            Timber.d("Cube Transport:%s", scoutingFormPresenter.readData("cubeTransport"));
        });
    }


    private void toggleElement(ImageButton elementImage, String indicator) {
        String imageButtonName = elementImage.getTag().toString();
        String retrievedImage = scoutingFormPresenter.fetchGridImageFile(imageButtonName, indicator, ARG_SECTION_NUMBER, requireContext());
        int id = getResources().getIdentifier(retrievedImage, "drawable", requireContext().getPackageName());
        elementImage.setImageResource(id);
    }

}

