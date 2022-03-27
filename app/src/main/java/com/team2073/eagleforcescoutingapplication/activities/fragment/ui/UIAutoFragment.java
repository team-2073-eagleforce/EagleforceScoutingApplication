package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIAutoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private TextView autoUpperLabel;
    private TextView autoUpperText;

    private TextView autoLowerLabel;
    private TextView autoLowerText;

    //ImageButtons
    private ImageButton upperHubButtonRight;
    private ImageButton upperHubButtonLeft;

    private ImageButton lowerHubButtonRight;
    private ImageButton lowerHubButtonLeft;

    private ImageButton autoLineButton;

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
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
        scoutingFormPresenter = new ScoutingFormPresenter(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_auto, container, false);

        //Bottom Port Views
        View bottomPort = root.findViewById(R.id.upperhub_layout);
        autoUpperLabel = bottomPort.findViewById(R.id.textview);
        autoUpperText = bottomPort.findViewById(R.id.pointDisplay);
        upperHubButtonRight = root.findViewById(R.id.auto_bottomport_button_right);
        upperHubButtonLeft = root.findViewById(R.id.auto_bottomport_button_left);

        //Outer port Views
        View outerPort = root.findViewById(R.id.lowerhub_layout);
        autoLowerLabel = outerPort.findViewById(R.id.textview);
        autoLowerText = outerPort.findViewById(R.id.pointDisplay);
        lowerHubButtonRight = root.findViewById(R.id.auto_outerport_button_right);
        lowerHubButtonLeft = root.findViewById(R.id.auto_outerport_button_left);

        autoLineButton = root.findViewById(R.id.autoline_button);

        initializeViewLabels();
        initFields();

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        upperHubButtonRight.setOnClickListener(this);
        upperHubButtonLeft.setOnClickListener(this);

        lowerHubButtonRight.setOnClickListener(this);
        lowerHubButtonLeft.setOnClickListener(this);

        autoLineButton.setOnClickListener(this);

        autoUpperText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (autoUpperText.getText().toString().equals("")) {
                    autoUpperText.setText("0");
                }
                scoutingFormPresenter.saveData("Auto Bottom", autoUpperText.getText().toString());
                Timber.d("shared Preferences: " + "Auto Bottom" + ", " + scoutingFormPresenter.readData("Auto Bottom"));
            }
        });
        autoLowerText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (autoLowerText.getText().toString().equals("")) {
                    autoLowerText.setText("0");
                }
                scoutingFormPresenter.saveData("Auto Outer", autoLowerText.getText().toString());
                Timber.d("shared Preferences: " + "Auto Outer" + ", " + scoutingFormPresenter.readData("Auto Outer"));
            }
        });
    }

    private void initFields() {
        scoutingFormPresenter.saveData("Tarmac", "0");
        scoutingFormPresenter.saveData("Auto Outer", "0");
        scoutingFormPresenter.saveData("Auto Bottom", "0");

        autoUpperText.setText("0");
        autoLowerText.setText("0");

    }

    private void initializeViewLabels() {
        autoUpperLabel.setText(getResources().getString(R.string.num_cargo_upper_hub_label));
        autoLowerLabel.setText(getResources().getString(R.string.num_cargo_lower_hub_label));
    }

    @Override
    public void onClick(View v) {

        Integer value = 0;
        switch (v.getId()) {
            case R.id.auto_bottomport_button_right:
                value = Integer.parseInt(autoLowerText.getText().toString()) + 1;
                autoLowerText.setText(value.toString());
                scoutingFormPresenter.saveData("Auto Bottom", value.toString());

                Timber.d("shared Preferences: " + "Auto Bottom" + ", " + scoutingFormPresenter.readData("Auto Bottom"));
                break;
            case R.id.auto_bottomport_button_left:
                value = Integer.parseInt(autoLowerText.getText().toString()) - 1;
                if(value <= 0){
                    value = 0;
                }
                autoLowerText.setText(value.toString());

                scoutingFormPresenter.saveData("Auto Bottom", value.toString());

                Timber.d("shared Preferences: " + "Auto Bottom" + ", " + scoutingFormPresenter.readData("Auto Bottom"));
                break;
            case R.id.auto_outerport_button_right:
                value = Integer.parseInt(autoUpperText.getText().toString()) + 1;
                autoUpperText.setText(value.toString());

                scoutingFormPresenter.saveData("Auto Outer", value.toString());

                Timber.d("shared Preferences: " + "Auto Outer" + ", " + scoutingFormPresenter.readData("Auto Outer"));
                break;
            case R.id.auto_outerport_button_left:
                value = Integer.parseInt(autoUpperText.getText().toString()) - 1;
                if(value <= 0){
                    value = 0;
                }
                autoUpperText.setText(value.toString());

                scoutingFormPresenter.saveData("Auto Outer", value.toString());

                Timber.d("shared Preferences: " + "Auto Outer" + ", " + scoutingFormPresenter.readData("Auto Outer"));
                break;
            case R.id.autoline_button:
                value = Math.abs(Integer.parseInt(scoutingFormPresenter.readData("Tarmac")) - 1);
                if (value == 1) {
                    autoLineButton.setImageResource(R.drawable.tarmac_yellow);
                } else {
                    autoLineButton.setImageResource(R.drawable.tarmac);
                }
                scoutingFormPresenter.saveData("Tarmac", value.toString());

                Timber.d("shared Preferences: " + "Tarmac" + ", " + scoutingFormPresenter.readData("Tarmac"));
                break;
        }
    }
}