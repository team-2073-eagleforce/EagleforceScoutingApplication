package com.team2073.eagleforcescoutingapplication.activities.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

public class SubmitFragment extends Fragment implements View.OnClickListener{

    private ScoutingFormPresenter scoutingFormPresenter;
    private EditText formComments;
    private EditText formName;
    private Button formSubmitButton;

    private String name;
    private String comments;

    private String[] autoData;
    private String[] teleOpData;

    View autoFragment;

    String state = Environment.getExternalStorageState();



    String[] externalStoragePermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] bluetoothPermission = {Manifest.permission.BLUETOOTH};

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
        formSubmitButton = root.findViewById(R.id.formSubmitButton);

        comments = formComments.getText().toString();
        name = formName.getText().toString();

        formSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Environment.MEDIA_MOUNTED.equals(state)){
                    Toast.makeText(getActivity(), "hi buddy", Toast.LENGTH_LONG).show();
//                    scoutingFormPresenter.createCSV();
////                    scoutingFormPresenter.writeCSV();
                }
            }
        });
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.formSubmitButton: {
                scoutingFormPresenter.saveData(getString(R.string.formNameKey), name);
                scoutingFormPresenter.saveData(getString(R.string.formCommentsKey), comments);

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), externalStoragePermission,23);
                }

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), bluetoothPermission,23);
                }
                scoutingFormPresenter.sendOverBluetooth();
                break;
            }
        }
    }
}
