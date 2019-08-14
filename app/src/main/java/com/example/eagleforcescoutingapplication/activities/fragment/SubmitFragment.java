package com.example.eagleforcescoutingapplication.activities.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.eagleforcescoutingapplication.R;
import com.example.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

public class SubmitFragment extends Fragment implements View.OnClickListener{

    private ScoutingFormPresenter scoutingFormPresenter;
    private EditText formComments;
    private EditText formName;
    private Button submitButton;

    private String name;
    private String comments;

    private static final String ARG_SECTION_NUMBER = "Submit";
    private PageViewModel pageViewModel;

    public static SubmitFragment newInstance(int index){
        SubmitFragment fragment = new SubmitFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoutingFormPresenter = new ScoutingFormPresenter(getActivity());

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 3;
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);

        formComments = getActivity().findViewById(R.id.formComments);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scouting_form_submit, container, false);

        root.findViewById(R.id.formSubmitButton).setOnClickListener(this);

        formComments = root.findViewById(R.id.formComments);
        formName = root.findViewById(R.id.formName);

        comments = formComments.getText().toString();
        name = formName.getText().toString();

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.formSubmitButton: {
                scoutingFormPresenter.saveData(getString(R.string.formNameKey), name);
                scoutingFormPresenter.saveData(getString(R.string.formCommentsKey), comments);
                break;
            }
        }
    }
}
