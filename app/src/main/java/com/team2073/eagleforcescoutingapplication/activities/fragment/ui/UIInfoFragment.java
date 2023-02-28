package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentInfoBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIInfoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Info";
    TextView teamNumberTextView;
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentInfoBinding fragmentInfoBinding;

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            } catch (Exception e) {
                Timber.d("setUserVisibleHint: Info ");
            }
        }
    }

}
