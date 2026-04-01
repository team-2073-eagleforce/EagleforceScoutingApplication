package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentInfoBinding;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIInfoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Info";
    TextView teamNumberTextView;
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentInfoBinding fragmentInfoBinding;
    private RadioGroup startPosition;

    public static UIInfoFragment newInstance(int index) {
        UIInfoFragment fragment = new UIInfoFragment();
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
        fragmentInfoBinding = UiFragmentInfoBinding.inflate(inflater, container, false);
        teamNumberTextView = getActivity().findViewById(R.id.scoutingTeamNumberTextView);
        startPosition = fragmentInfoBinding.startPosition;
        initRadioGroup();
        initSpinner();
        if (scoutingFormPresenter.readData("position").equals("Red1") || scoutingFormPresenter.readData("position").equals("Red2") || scoutingFormPresenter.readData("position").equals("Red3")){
            if (scoutingFormPresenter.readData("field_side").equals("0")) {
            //RelativeLayout.LayoutParams imgParam = (RelativeLayout.LayoutParams) fragmentInfoBinding.startMap.getLayoutParams();
            fragmentInfoBinding.startMap.setImageResource(R.drawable.red_proc_non_processor);
        } else {
            fragmentInfoBinding.startMap.setImageResource(R.drawable.red_proc_processor);
        }} else if(scoutingFormPresenter.readData("position").equals("Blue1") || scoutingFormPresenter.readData("position").equals("Blue2") || scoutingFormPresenter.readData("position").equals("Blue3")){
            if (scoutingFormPresenter.readData("field_side").equals("0")) {
                //RelativeLayout.LayoutParams imgParam = (RelativeLayout.LayoutParams) fragmentInfoBinding.startMap.getLayoutParams();
                fragmentInfoBinding.startMap.setImageResource(R.drawable.blue_proc_non_processor);
            } else {
                fragmentInfoBinding.startMap.setImageResource(R.drawable.blue_proc_processor);
        }}
        return fragmentInfoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initTextFields();
        initOnChangeEditText();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentInfoBinding = null;
    }

    public void initTextFields() {
        fragmentInfoBinding.editTextName.setText(scoutingFormPresenter.readData("name").equals("0") ? "" : scoutingFormPresenter.readData("name"));
        fragmentInfoBinding.editTextMatchNumber.setText(scoutingFormPresenter.readData("matchNumber").equals("0") ? "" : scoutingFormPresenter.readData("matchNumber"));
        fragmentInfoBinding.editTextTeamNumber.setText(scoutingFormPresenter.readData("teamNumber").equals("0") ? "" : scoutingFormPresenter.readData("teamNumber"));
    }

    private void initSpinner() {
        Spinner matchDropdown = fragmentInfoBinding.selectMatchType;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.match_type_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchDropdown.setAdapter(adapter);
        SharedPreferences quantifierPersist = getActivity().getSharedPreferences("savePrefs", Context.MODE_PRIVATE);

        int savedPosition = quantifierPersist.getInt("spinner_position", 0);
        matchDropdown.setSelection(savedPosition);
        matchDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                if (scoutingFormPresenter.getScheduleList() != null) {
                    matchDropdown.setSelection(adapter.getPosition("Qualifier"));
                }
                if(parent.getItemAtPosition(pos).equals("Qualifier")) {
                    scoutingFormPresenter.saveData("quantifier", "Quals");
                } else if (parent.getItemAtPosition(pos).equals("Practice")){
                    scoutingFormPresenter.saveData("quantifier", "Prac");
                } else {
                    scoutingFormPresenter.saveData("quantifier", "Play Off");
                }
                Timber.d("%s",parent.getItemAtPosition(pos));
                SharedPreferences quantifierPersist = getActivity().getSharedPreferences("savePrefs", Context.MODE_PRIVATE);

                quantifierPersist.edit()
                        .putInt("spinner_position", pos)
                        .apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void initRadioGroup() {
        startPosition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // on below line we are getting radio button from our group.
                RadioButton radioButton = startPosition.findViewById(checkedId);
                String pos = radioButton.getText().toString();
                if (pos.equals("No Show")) {
                    pos = "0";
                }
                scoutingFormPresenter.saveData("startPos", pos);
            }
        });
    }

    public void initOnChangeEditText() {
        fragmentInfoBinding.editTextMatchNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scoutingFormPresenter.saveData("matchNumber", fragmentInfoBinding.editTextMatchNumber.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fragmentInfoBinding.editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scoutingFormPresenter.saveData("name", fragmentInfoBinding.editTextName.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fragmentInfoBinding.editTextTeamNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scoutingFormPresenter.saveData("teamNumber", fragmentInfoBinding.editTextTeamNumber.getText().toString());
                String team_number_display = String.format(getResources().getString(R.string.team_num), scoutingFormPresenter.readData("teamNumber"));
                teamNumberTextView.setText(team_number_display);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
