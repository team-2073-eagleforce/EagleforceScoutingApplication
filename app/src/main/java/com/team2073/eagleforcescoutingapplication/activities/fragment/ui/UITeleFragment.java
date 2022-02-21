package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.Bundle;
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

public class UITeleFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private TextView teleUpperLabel;
    private TextView teleUpperText;

    private TextView teleLowerLabel;
    private TextView teleLowerText;

    private ImageButton upperHubButtonRight;
    private ImageButton upperHubButtonLeft;

    private ImageButton lowerHubButtonRight;
    private ImageButton lowerHubButtonLeft;

    public static UITeleFragment newInstance(int index) {
        UITeleFragment fragment = new UITeleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 2;
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
        scoutingFormPresenter = new ScoutingFormPresenter(this.getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_teleop, container, false);

        //Bottom Port Views
        View bottomPort = root.findViewById(R.id.upperhub_layout);
        teleUpperLabel = bottomPort.findViewById(R.id.textview);
        teleUpperText = bottomPort.findViewById(R.id.pointDisplay);
        upperHubButtonRight = root.findViewById(R.id.teleop_bottomport_button_right);
        upperHubButtonLeft = root.findViewById(R.id.teleop_bottomport_button_left);

        //Outer port Views
        View outerPort = root.findViewById(R.id.lowerhub_layout);
        teleLowerLabel = outerPort.findViewById(R.id.textview);
        teleLowerText = outerPort.findViewById(R.id.pointDisplay);
        lowerHubButtonRight = root.findViewById(R.id.teleop_outerport_button_right);
        lowerHubButtonLeft = root.findViewById(R.id.teleop_outerport_button_left);

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


        teleUpperText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (teleUpperText.getText().toString().equals("")) {
                    teleUpperText.setText("0");
                }

                scoutingFormPresenter.saveData("Teleop Bottom", teleUpperText.getText().toString());
                Timber.d("shared Preferences: " + "Teleop Bottom" + ", " + scoutingFormPresenter.readData("Teleop Bottom"));
            }
        });
        teleLowerText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (teleLowerText.getText().toString().equals("")) {
                    teleLowerText.setText("0");
                }
                scoutingFormPresenter.saveData("Teleop Outer", teleLowerText.getText().toString());
                Timber.d("shared Preferences: " + "Teleop Outer" + ", " + scoutingFormPresenter.readData("Teleop Outer"));
            }
        });
    }

    private void initFields() {
        scoutingFormPresenter.saveData("Teleop Lower", "0");
        scoutingFormPresenter.saveData("Teleop Upper", "0");

        teleUpperText.setText("0");
        teleLowerText.setText("0");

    }

    private void initializeViewLabels() {
        teleUpperLabel.setText(getResources().getString(R.string.num_cargo_upper_hub_label));
        teleLowerLabel.setText(getResources().getString(R.string.num_cargo_lower_hub_label));
    }

    @Override
    public void onClick(View v) {
        Integer value = 0;
        switch (v.getId()) {
            case R.id.teleop_bottomport_button_right:
                value = Integer.parseInt(teleLowerText.getText().toString()) + 1;
                teleLowerText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Bottom", value.toString());

                Timber.d("shared Preferences: " + "Teleop Bottom" + ", " + scoutingFormPresenter.readData("Teleop Bottom"));
                break;
            case R.id.teleop_bottomport_button_left:
                value = Integer.parseInt(teleLowerText.getText().toString()) - 1;
                if (value <= 0) {
                    value = 0;
                }
                teleLowerText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Bottom", value.toString());

                Timber.d("shared Preferences: " + "Teleop Bottom" + ", " + scoutingFormPresenter.readData("Teleop Bottom"));
                break;
            case R.id.teleop_outerport_button_right:
                value = Integer.parseInt(teleUpperText.getText().toString()) + 1;
                teleUpperText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Outer", value.toString());

                Timber.d("shared Preferences: " + "Teleop Outer" + ", " + scoutingFormPresenter.readData("Teleop Outer"));
                break;
            case R.id.teleop_outerport_button_left:
                value = Integer.parseInt(teleUpperText.getText().toString()) - 1;
                if (value <= 0) {
                    value = 0;
                }
                teleUpperText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Outer", value.toString());

                Timber.d("shared Preferences: " + "Teleop Outer" + ", " + scoutingFormPresenter.readData("Teleop Outer"));
                break;
            }
        }
    }




