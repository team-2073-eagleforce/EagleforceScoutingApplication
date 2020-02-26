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

public class UIAutoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private TextView teamNumberText;

    private TextView autoBottomLabel;
    private EditText autoBottomText;

    private TextView autoOuterLabel;
    private EditText autoOuterText;

    //ImageButtons
    private ImageButton bottomPortButtonRight;
    private ImageButton bottomPortButtonLeft;

    private ImageButton outerPortButtonRight;
    private ImageButton outerPortButtonLeft;

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

        teamNumberText = root.findViewById(R.id.teamNumber_auto);

        //Bottom Port Views
        View bottomPort = root.findViewById(R.id.bottomport_layout);
        autoBottomLabel = bottomPort.findViewById(R.id.textview);
        autoBottomText = bottomPort.findViewById(R.id.edittext);
        bottomPortButtonRight = root.findViewById(R.id.auto_bottomport_button_right);
        bottomPortButtonLeft = root.findViewById(R.id.auto_bottomport_button_left);

        //Outer port Views
        View outerPort = root.findViewById(R.id.outerport_layout);
        autoOuterLabel = outerPort.findViewById(R.id.textview);
        autoOuterText = outerPort.findViewById(R.id.edittext);
        outerPortButtonRight = root.findViewById(R.id.auto_outerport_button_right);
        outerPortButtonLeft = root.findViewById(R.id.auto_outerport_button_left);

        autoLineButton = root.findViewById(R.id.autoline_button);

        initializeViewLabels();
        initFields();

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        teamNumberText.setText("Team " + scoutingFormPresenter.readData("teamNumber"));

        bottomPortButtonRight.setOnClickListener(this);
        bottomPortButtonLeft.setOnClickListener(this);

        outerPortButtonRight.setOnClickListener(this);
        outerPortButtonLeft.setOnClickListener(this);

        autoLineButton.setOnClickListener(this);

        autoBottomText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (autoBottomText.getText().toString().equals("")) {
                    autoBottomText.setText("0");
                }
                scoutingFormPresenter.saveData("Auto Bottom", autoBottomText.getText().toString());
                Timber.d("shared Preferences: " + "Auto Bottom" + ", " + scoutingFormPresenter.readData("Auto Bottom"));
            }
        });
        autoOuterText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (autoOuterText.getText().toString().equals("")) {
                    autoOuterText.setText("0");
                }
                scoutingFormPresenter.saveData("Auto Outer", autoOuterText.getText().toString());
                Timber.d("shared Preferences: " + "Auto Outer" + ", " + scoutingFormPresenter.readData("Auto Outer"));
            }
        });
    }

    private void initFields() {
        scoutingFormPresenter.saveData("Auto Cross", "0");
        scoutingFormPresenter.saveData("Auto Inner", "0");
        scoutingFormPresenter.saveData("Auto Outer", "0");
        scoutingFormPresenter.saveData("Auto Bottom", "0");

        autoBottomText.setText("0");
        autoOuterText.setText("0");

        bottomPortButtonLeft.setRotation(180);
        outerPortButtonLeft.setRotation(180);

    }

    private void initializeViewLabels() {
        autoBottomLabel.setText(getResources().getString(R.string.num_cells_bottom_label));
        autoOuterLabel.setText(getResources().getString(R.string.num_cells_outer_label));
    }

    @Override
    public void onClick(View v) {

        Integer value = 0;
        switch (v.getId()) {
            case R.id.auto_bottomport_button_right:
                value = Integer.parseInt(autoBottomText.getText().toString()) + 1;
                autoBottomText.setText(value.toString());

                scoutingFormPresenter.saveData("Auto Bottom", value.toString());

                Timber.d("shared Preferences: " + "Auto Bottom" + ", " + scoutingFormPresenter.readData("Auto Bottom"));
                break;
            case R.id.auto_bottomport_button_left:
                value = Integer.parseInt(autoBottomText.getText().toString()) - 1;
                if(value <= 0){
                    value = 0;
                }
                autoBottomText.setText(value.toString());

                scoutingFormPresenter.saveData("Auto Bottom", value.toString());

                Timber.d("shared Preferences: " + "Auto Bottom" + ", " + scoutingFormPresenter.readData("Auto Bottom"));
                break;
            case R.id.auto_outerport_button_right:
                value = Integer.parseInt(autoOuterText.getText().toString()) + 1;
                autoOuterText.setText(value.toString());

                scoutingFormPresenter.saveData("Auto Outer", value.toString());

                Timber.d("shared Preferences: " + "Auto Outer" + ", " + scoutingFormPresenter.readData("Auto Outer"));
                break;
            case R.id.auto_outerport_button_left:
                value = Integer.parseInt(autoOuterText.getText().toString()) - 1;
                if(value <= 0){
                    value = 0;
                }
                autoOuterText.setText(value.toString());

                scoutingFormPresenter.saveData("Auto Outer", value.toString());

                Timber.d("shared Preferences: " + "Auto Outer" + ", " + scoutingFormPresenter.readData("Auto Outer"));
                break;
            case R.id.autoline_button:
                value = Math.abs(Integer.parseInt(scoutingFormPresenter.readData("Auto Cross")) - 1);
                if (value == 1) {
                    autoLineButton.setImageResource(R.drawable.autoline_clicked);
                } else {
                    autoLineButton.setImageResource(R.drawable.autoline);
                }
                scoutingFormPresenter.saveData("Auto Cross", value.toString());

                Timber.d("shared Preferences: " + "Auto Cross" + ", " + scoutingFormPresenter.readData("Auto Cross"));
                break;
        }
    }
}