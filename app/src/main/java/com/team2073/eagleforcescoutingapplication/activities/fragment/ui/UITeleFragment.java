package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import org.w3c.dom.Text;

import timber.log.Timber;

public class UITeleFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private TextView teleUpperLabel;
    private EditText teleUpperText;

    private TextView teleLowerLabel;
    private EditText teleLowerText;

    private ImageButton upperHubButtonRight;
    private ImageButton upperHubButtonLeft;

    private ImageButton lowerHubButtonRight;
    private ImageButton lowerHubButtonLeft;

    private Button rControlButton;
    private Button pControlButton;

    private ImageView halfFirst;
    private ImageView halfSecond;
    private ImageView darkHalfFirst;
    private ImageView darkHalfSecond;

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
        View bottomPort = root.findViewById(R.id.bottomport_layout);
        teleUpperLabel = bottomPort.findViewById(R.id.textview);
        teleUpperText = bottomPort.findViewById(R.id.edittext);
        upperHubButtonRight = root.findViewById(R.id.tele_bottomport_button_right);
        upperHubButtonLeft = root.findViewById(R.id.tele_bottomport_button_left);

        //Outer port Views
        View outerPort = root.findViewById(R.id.outerport_layout);
        teleLowerLabel = outerPort.findViewById(R.id.textview);
        teleLowerText = outerPort.findViewById(R.id.edittext);
        lowerHubButtonRight = root.findViewById(R.id.tele_outerport_button_right);
        lowerHubButtonLeft = root.findViewById(R.id.tele_outerport_button_left);

        rControlButton = root.findViewById(R.id.rotationControlButton);
        pControlButton = root.findViewById(R.id.positionControlButton);

        halfFirst = root.findViewById(R.id.wofHalfFirst);
        halfSecond = root.findViewById(R.id.wofHalfSecond);
        darkHalfFirst = root.findViewById(R.id.wofDarkHalfFirst);
        darkHalfSecond = root.findViewById(R.id.wofDarkHalfSecond);


        initializeViewLabels();
        initFields();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        darkHalfFirst.setVisibility(View.VISIBLE);
        darkHalfSecond.setVisibility(View.VISIBLE);
        halfFirst.setVisibility(View.INVISIBLE);
        halfSecond.setVisibility(View.INVISIBLE);


        upperHubButtonRight.setOnClickListener(this);
        upperHubButtonLeft.setOnClickListener(this);

        lowerHubButtonRight.setOnClickListener(this);
        lowerHubButtonLeft.setOnClickListener(this);

        rControlButton.setOnClickListener(this);
        pControlButton.setOnClickListener(this);


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

        scoutingFormPresenter.saveData("Control Panel Rotation", "0");
        scoutingFormPresenter.saveData("Control Panel Position", "0");

        teleUpperText.setText("0");
        teleLowerText.setText("0");

        upperHubButtonLeft.setRotation(180);
        lowerHubButtonLeft.setRotation(180);
    }

    private void initializeViewLabels() {
        teleUpperLabel.setText(getResources().getString(R.string.num_cargo_upper_hub_label));
        teleLowerLabel.setText(getResources().getString(R.string.num_cargo_lower_hub_label));
    }

    @Override
    public void onClick(View v) {
        Integer value = 0;
        switch (v.getId()) {
            case R.id.tele_bottomport_button_right:
                value = Integer.parseInt(teleUpperText.getText().toString()) + 1;
                teleUpperText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Bottom", value.toString());

                Timber.d("shared Preferences: " + "Teleop Bottom" + ", " + scoutingFormPresenter.readData("Teleop Bottom"));
                break;
            case R.id.tele_bottomport_button_left:
                value = Integer.parseInt(teleUpperText.getText().toString()) - 1;
                if (value <= 0) {
                    value = 0;
                }
                teleUpperText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Bottom", value.toString());

                Timber.d("shared Preferences: " + "Teleop Bottom" + ", " + scoutingFormPresenter.readData("Teleop Bottom"));
                break;
            case R.id.tele_outerport_button_right:
                value = Integer.parseInt(teleLowerText.getText().toString()) + 1;
                teleLowerText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Outer", value.toString());

                Timber.d("shared Preferences: " + "Teleop Outer" + ", " + scoutingFormPresenter.readData("Teleop Outer"));
                break;
            case R.id.tele_outerport_button_left:
                value = Integer.parseInt(teleLowerText.getText().toString()) - 1;
                if (value <= 0) {
                    value = 0;
                }
                teleLowerText.setText(value.toString());

                scoutingFormPresenter.saveData("Teleop Outer", value.toString());

                Timber.d("shared Preferences: " + "Teleop Outer" + ", " + scoutingFormPresenter.readData("Teleop Outer"));
                break;
            case R.id.rotationControlButton:
                value = Math.abs(Integer.parseInt(scoutingFormPresenter.readData("Control Panel Rotation")) - 1);
                if (value == 1) {
                    //TODO find a cleaner way to display ON/OFF
                    //First PNG of darkened WOF set OFF
                    //First PNG of normal WOF set ON
                    darkHalfFirst.setVisibility(View.INVISIBLE);
                    halfFirst.setVisibility(View.VISIBLE);
                } else {
                    //First PNG of darkened WOF set ON
                    //First PNG of normal WOF set OFF
                    darkHalfFirst.setVisibility(View.VISIBLE);
                    halfFirst.setVisibility(View.INVISIBLE);

                }
                scoutingFormPresenter.saveData("Control Panel Rotation", value.toString());

                Timber.d("shared Preferences: " + "Control Panel Rotation" + ", " + scoutingFormPresenter.readData("Control Panel Rotation"));
                break;
            case R.id.positionControlButton:
                value = Math.abs(Integer.parseInt(scoutingFormPresenter.readData("Control Panel Position")) - 1);
                if (value == 1) {
                    // Second first PNG of darkened WOF set OFF
                    // First PNG of normal WOF set ON
                    darkHalfSecond.setVisibility(View.INVISIBLE);
                    halfSecond.setVisibility(View.VISIBLE);
                } else {
                    // //First PNG of darkened WOF set ON
                    //First PNG of normal WOF set OFF
                    darkHalfSecond.setVisibility(View.VISIBLE);
                    halfSecond.setVisibility(View.INVISIBLE);
                }
                scoutingFormPresenter.saveData("Control Panel Position", value.toString());

                Timber.d("shared Preferences: " + "Control Panel Position" + ", " + scoutingFormPresenter.readData("Control Panel Position"));
                break;
        }
    }
}




