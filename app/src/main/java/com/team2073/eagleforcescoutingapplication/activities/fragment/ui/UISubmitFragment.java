package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.QrGeneratorActivity;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentAutoBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentSubmitBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class UISubmitFragment extends Fragment implements View.OnClickListener {

    private ScoutingFormPresenter scoutingFormPresenter;
    private Activity mActivity;

    @BindView(R.id.uiComments)
    EditText formComments;

    private UiFragmentSubmitBinding fragmentSubmitBinding;
    private AddSubtractValuesBinding defensePerform;
    private AddSubtractValuesBinding driverPerform;


    String state = Environment.getExternalStorageState();

    private static final String ARG_SECTION_NUMBER = "Submit";

    public static UISubmitFragment newInstance(int index) {
        UISubmitFragment fragment = new UISubmitFragment();
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
        scoutingFormPresenter = new ScoutingFormPresenter(getActivity());
        formComments = getActivity().findViewById(R.id.uiComments);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSubmitBinding = UiFragmentSubmitBinding.inflate(inflater, container, false);
        driverPerform = fragmentSubmitBinding.driverPerformance;
        defensePerform = fragmentSubmitBinding.defensePerformance;

        initializeViewLabels();
        initDataFields();

        return fragmentSubmitBinding.getRoot();

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        defensePerform.formAdd.setOnClickListener(view1 -> {
            addDefense();
        });
        defensePerform.formSubtract.setOnClickListener(view1 -> {
            subtractDefense();
        });
        driverPerform.formAdd.setOnClickListener(view1 -> {
            addDriver();
        });
        driverPerform.formSubtract.setOnClickListener(view1 -> {
            subtractDriver();
        });
    }


    public void addDefense() {
        int value = Integer.parseInt(defensePerform.formScore.getText().toString()) + 1;
        if (value >= 5) {
            value = 5;
        }
        defensePerform.formScore.setText(String.valueOf(value));

        scoutingFormPresenter.saveData("Defense Performance", String.valueOf(value));

        Timber.d("shared Preferences: " + "Defense Performance" + ", " + scoutingFormPresenter.readData("Defense Performance"));
    }

    public void subtractDefense() {
        int value = Integer.parseInt(defensePerform.formField.getText().toString()) - 1;
        if (value <= 0) {
            value = 0;
        }
        defensePerform.formField.setText(String.valueOf(value));

        scoutingFormPresenter.saveData("Defense Performance", String.valueOf(value));

        Timber.d("shared Preferences: " + "Defense Performance" + ", " + scoutingFormPresenter.readData("Defense Performance"));
    }


    public void addDriver() {
        int value = Integer.parseInt(driverPerform.formScore.getText().toString()) + 1;
        if (value >= 5) {
            value = 5;
        }
        driverPerform.formScore.setText(String.valueOf(value));

        scoutingFormPresenter.saveData("Driver Performance", String.valueOf(value));

        Timber.d("shared Preferences: " + "Driver Performance" + ", " + scoutingFormPresenter.readData("Driver Performance"));
    }

    public void subtractDriver() {
        int value = Integer.parseInt(driverPerform.formField.getText().toString()) - 1;
        if (value <= 0) {
            value = 0;
        }
        driverPerform.formField.setText(String.valueOf(value));

        scoutingFormPresenter.saveData("Driver Performance", String.valueOf(value));

        Timber.d("shared Preferences: " + "Driver Performance" + ", " + scoutingFormPresenter.readData("Driver Performance"));
    }



    private void initDataFields() {
        if (scoutingFormPresenter.readData("Defense Performance").equals("0")){
            defensePerform.formField.setText("0");
        } else {
            defensePerform.formField.setText(scoutingFormPresenter.readData("Defense Performance"));
        }

        if (scoutingFormPresenter.readData("Driver Performance").equals("0")){
            driverPerform.formField.setText("0");
        } else {
            driverPerform.formField.setText(scoutingFormPresenter.readData("Driver Performance"));
        }
    }

    private void initializeViewLabels() {
        driverPerform.formField.setText(getResources().getString(R.string.driver_performance));
        defensePerform.formField.setText(getResources().getString(R.string.defense_performance));
    }

    public void onClick(View view) {
        if (view.getId() == R.id.uiSubmitButton) {
            if (scoutingFormPresenter.readData("position").equals("0")) {
                scoutingFormPresenter.saveData("position", "red1");
            }
            if (scoutingFormPresenter.readData("comp_code").equals("0")) {
                scoutingFormPresenter.saveData("comp_code", "test");
            }
            String comments = formComments.getText().toString().replace(',', ';');
            scoutingFormPresenter.saveData("comments", comments);
            if (scoutingFormPresenter.readData("comments").length() == 0 || scoutingFormPresenter.readData("name").length() == 0) {
                Toast.makeText(this.getActivity(), "Some Fields are Empty", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(getActivity(), QrGeneratorActivity.class);
                startActivity(i);
                ((Activity) requireActivity()).overridePendingTransition(0, 0);
            }
        }
    }

}
