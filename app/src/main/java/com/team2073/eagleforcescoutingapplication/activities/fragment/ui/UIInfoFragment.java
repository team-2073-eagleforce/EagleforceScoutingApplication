package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class UIInfoFragment extends Fragment {

    @BindView(R.id.editTextMatchNumber) EditText matchNumberText;
    @BindView(R.id.editTextTeamNumber) EditText teamNumberText;
    @BindView(R.id.editTextName) EditText nameText;
    @BindView(R.id.textName)
    TextView textName;
    private TextView teamNumberTextView;

    private static final String ARG_SECTION_NUMBER = "Start";
    private ScoutingFormPresenter scoutingFormPresenter;

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

    private String lastTeamNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_info, container, false);
        ButterKnife.bind(this, root);
        teamNumberTextView = getActivity().findViewById(R.id.scoutingTeamNumberTextView);
        teamNumberTextView.setText("Team: " + scoutingFormPresenter.readData("teamNumber"));

        if (!scoutingFormPresenter.readData("teamNumber").equals("0")
                && !scoutingFormPresenter.readData("teamNumber").equals(lastTeamNumber)) {
            matchNumberText.setText(scoutingFormPresenter.readData("matchNumber"));
            teamNumberText.setText(scoutingFormPresenter.readData("teamNumber"));
            lastTeamNumber = scoutingFormPresenter.readData("teamNumber");
        }

        if(!scoutingFormPresenter.readData("name").equals("0")) {
            nameText.setText(scoutingFormPresenter.readData("name"));
        }

        matchNumberText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                scoutingFormPresenter.saveData("matchNumber", matchNumberText.getText().toString());
                Timber.d("shared Preferences: " + "Match Number" + ", " + scoutingFormPresenter.readData("matchNumber"));
            }
        });

        teamNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scoutingFormPresenter.saveData("teamNumber", teamNumberText.getText().toString());
                Timber.d("shared Preferences: " + "Team Number" + ", " + scoutingFormPresenter.readData("teamNumber"));
                teamNumberTextView.setText("Team: " + scoutingFormPresenter.readData("teamNumber"));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nameText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                scoutingFormPresenter.saveData("name", nameText.getText().toString());
                Timber.d("shared Preferences: " + "Name" + ", " + scoutingFormPresenter.readData("name"));
            }
        });

        return root;

    }

}
