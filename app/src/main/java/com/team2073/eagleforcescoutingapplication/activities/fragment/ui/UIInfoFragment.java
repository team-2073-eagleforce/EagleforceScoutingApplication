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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentInfoBinding;
import com.team2073.eagleforcescoutingapplication.framework.manager.ReplayServerManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIInfoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Info";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentInfoBinding fragmentInfoBinding;
    private RadioGroup startPosition;
    private ReplayServerManager replayServerManager;

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
        replayServerManager = ReplayServerManager.getInstance(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentInfoBinding = UiFragmentInfoBinding.inflate(inflater, container, false);
        startPosition = fragmentInfoBinding.startPosition;
        initRadioGroup();
        initSpinner();
        if (scoutingFormPresenter.readData("position").equals("Red1") || scoutingFormPresenter.readData("position").equals("Red2") || scoutingFormPresenter.readData("position").equals("Red3")){
            if (scoutingFormPresenter.readData("field_side").equals("0")) {
                fragmentInfoBinding.startMap.setImageResource(R.drawable.red_proc_non_processor);
            } else {
                fragmentInfoBinding.startMap.setImageResource(R.drawable.red_proc_processor);
            }
        } else if(scoutingFormPresenter.readData("position").equals("Blue1") || scoutingFormPresenter.readData("position").equals("Blue2") || scoutingFormPresenter.readData("position").equals("Blue3")){
            if (scoutingFormPresenter.readData("field_side").equals("0")) {
                fragmentInfoBinding.startMap.setImageResource(R.drawable.blue_proc_non_processor);
            } else {
                fragmentInfoBinding.startMap.setImageResource(R.drawable.blue_proc_processor);
            }
        }
        return fragmentInfoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initTextFields();
        initOnChangeEditText();
        initStartRecordingButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh button visibility whenever the tab is shown
        refreshStartRecordingButton();
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
                if (getActivity() != null) {
                    android.widget.TextView tv = getActivity().findViewById(R.id.scoutingTeamNumberTextView);
                    if (tv != null) {
                        tv.setText(String.format(getResources().getString(R.string.team_num),
                                scoutingFormPresenter.readData("teamNumber")));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initStartRecordingButton() {
        Button startButton = fragmentInfoBinding.manualStartReplayButton;
        refreshStartRecordingButton();
        startButton.setOnClickListener(v -> onStartRecordingClicked());
    }

    private void refreshStartRecordingButton() {
        if (fragmentInfoBinding == null) return;
        Button startButton = fragmentInfoBinding.manualStartReplayButton;
        if (replayServerManager.isRemoteStartEnabled()) {
            startButton.setVisibility(View.VISIBLE);
        } else {
            startButton.setVisibility(View.GONE);
        }
    }

    private void onStartRecordingClicked() {
        // Navigate to Auto tab immediately — fire and forget
        if (getActivity() instanceof ScoutingFormActivity) {
            ((ScoutingFormActivity) getActivity()).navigateToAutoTab();
        }

        // Fire the remote start request asynchronously
        replayServerManager.fireRemoteStart(new ReplayServerManager.RemoteStartCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) return;
                Toast.makeText(getActivity(),
                        getString(R.string.remote_start_success),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int httpCode, String message) {
                if (getActivity() == null) return;
                String msg;
                switch (httpCode) {
                    case -1:
                        msg = getString(R.string.remote_start_error_no_config);
                        break;
                    case 401:
                        msg = getString(R.string.remote_start_error_401);
                        break;
                    case 409:
                        msg = getString(R.string.remote_start_error_409);
                        break;
                    case 400:
                        msg = getString(R.string.remote_start_error_400);
                        break;
                    default:
                        msg = getString(R.string.remote_start_error_generic, httpCode);
                }
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                Timber.w("Remote start error %d: %s", httpCode, message);
            }

            @Override
            public void onNetworkError(String message) {
                if (getActivity() == null) return;
                Toast.makeText(getActivity(),
                        getString(R.string.remote_start_error_network),
                        Toast.LENGTH_SHORT).show();
                Timber.w("Remote start network error: %s", message);
            }
        });
    }
}
