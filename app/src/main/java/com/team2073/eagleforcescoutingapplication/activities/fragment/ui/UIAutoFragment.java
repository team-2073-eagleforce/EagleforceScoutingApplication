package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

import timber.log.Timber;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;
    private ArrayList<String> fieldNames;

    //Bottom Port Views
    private View bottomPort;
    private TextView autoBottomLabel;
    private EditText autoBottomAmt;

    //Outer port Views
    private View outerPort;
    private TextView autoOuterLabel;
    private EditText autoOuterAmt;

    //Inner port Views
    private View innerPort;
    private TextView autoInnerLabel;
    private EditText autoInnerAmt;

    //ImageButtons
    private ImageButton bottomPortButton;
    private ImageButton outerPortButton;
    private ImageButton innerPortButton;
    private ImageButton autolineButton;

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

        //Instantiate bottom port views
        bottomPort = root.findViewById(R.id.bottomport_layout);
        autoBottomLabel = bottomPort.findViewById(R.id.textview);
        autoBottomAmt = bottomPort.findViewById(R.id.edittext);
        bottomPortButton = root.findViewById(R.id.bottomport_button);

        //Instantiate bottom port views
        outerPort = root.findViewById(R.id.outerport_layout);
        autoOuterLabel = outerPort.findViewById(R.id.textview);
        autoOuterAmt = outerPort.findViewById(R.id.edittext);
        outerPortButton = root.findViewById(R.id.outerport_button);

        //Instantiate bottom port views
        innerPort = root.findViewById(R.id.innerport_layout);
        autoInnerLabel = innerPort.findViewById(R.id.textview);
        autoInnerAmt = innerPort.findViewById(R.id.edittext);
        innerPortButton = root.findViewById(R.id.innerport_button);

        initializeViewLabels();
        initializeFieldNames();

        initWriteToPref();
        setEditTextViews();

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            bottomPortButton.setOnClickListener((View v) -> {
                updateOnClick(bottomPortButton);
        });
            outerPortButton.setOnClickListener((View v) -> {
                updateOnClick(outerPortButton);
        });
            innerPortButton.setOnClickListener((View v) -> {
                updateOnClick(innerPortButton);
        });
    }

    private void initWriteToPref(){
        scoutingFormPresenter.initWriteToPreferences(fieldNames.get(3));
        scoutingFormPresenter.initWriteToPreferences(fieldNames.get(2));
        scoutingFormPresenter.initWriteToPreferences(fieldNames.get(1));
    }

    private void initializeViewLabels() {
        autoBottomLabel.setText(getResources().getString(R.string.num_cells_bottom_label));
        autoOuterLabel.setText(getResources().getString(R.string.num_cells_outer_label));
        autoInnerLabel.setText(getResources().getString(R.string.num_cells_inner_label));
    }

    private void setEditTextViews() {
        autoBottomAmt.setText(scoutingFormPresenter.readFromPreferences(fieldNames.get(3)));
        autoOuterAmt.setText(scoutingFormPresenter.readFromPreferences(fieldNames.get(2)));
        autoInnerAmt.setText(scoutingFormPresenter.readFromPreferences(fieldNames.get(1)));
    }

    public void updateOnClick(View v){
        String fieldName;
        Integer updatedTextValue;

        if(v.getId() == (R.id.bottomport_button)) {
            updatedTextValue = Integer.parseInt(autoBottomAmt.getText().toString()) + 1;
            fieldName = fieldNames.get(3);
//            autoBottomAmt.setText(updatedTextValue.toString());
        }else if(v.getId() == (R.id.outerport_button)){
            updatedTextValue = Integer.parseInt(autoOuterAmt.getText().toString()) + 1;
            fieldName = fieldNames.get(2);
        }else if(v.getId() == (R.id.innerport_button)){
            updatedTextValue = Integer.parseInt(autoInnerAmt.getText().toString()) + 1;
            fieldName = fieldNames.get(1);
        }else{
            updatedTextValue = 0;
            fieldName = null;
            Timber.d("No such field available.");
        }
//        updatedTextValue = Integer.parseInt(scoutingFormPresenter.readFromPreferences(fieldName)) + 1;
        scoutingFormPresenter.updatePreferences(fieldName, updatedTextValue.toString());
        setEditTextViews();
    }

    private void initializeFieldNames() {
        fieldNames = scoutingFormPresenter.getScoutingForm().getAutoFieldNames();
    }
}