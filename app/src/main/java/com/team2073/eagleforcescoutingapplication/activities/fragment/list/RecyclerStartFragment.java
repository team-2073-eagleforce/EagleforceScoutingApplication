package com.team2073.eagleforcescoutingapplication.activities.fragment.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerStartFragment extends Fragment {

    @BindView(R.id.rMatchNumber) EditText matchNumberText;
    @BindView(R.id.rTeamNumber) EditText teamNumberText;

    private static final String ARG_SECTION_NUMBER = "Start";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    public static RecyclerStartFragment newInstance(int index) {
        RecyclerStartFragment fragment = new RecyclerStartFragment();
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

    private String lastTeamNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_fragment_scouting_form_start, container, false);
        ButterKnife.bind(this, root);

        if (!scoutingFormPresenter.readData("teamNumber").equals("0")
                && !scoutingFormPresenter.readData("teamNumber").equals(lastTeamNumber)) {
            matchNumberText.setText(scoutingFormPresenter.readData("matchNumber"));
            teamNumberText.setText(scoutingFormPresenter.readData("teamNumber"));
            lastTeamNumber = scoutingFormPresenter.readData("teamNumber");
        }

        matchNumberText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                scoutingFormPresenter.saveData("matchNumber", matchNumberText.getText().toString());
            }
        });

        teamNumberText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                scoutingFormPresenter.saveData("teamNumber", teamNumberText.getText().toString());
            }
        });
        return root;

    }

}
