package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;

import timber.log.Timber;

public class UITeleFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;
    private SharedPreferences sharedPreferences;
    private PrefsDataManager prefsDataManager = PrefsDataManager.getInstance(getActivity());

    private ArrayList<String> fieldNames = new ArrayList<>();

    private Button rControl;
    private Button pControl;
    private TextView rText;
    private TextView pText;


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

        initFieldNames();

        rControl = root.findViewById(R.id.rotationControl);
        pControl = root.findViewById(R.id.positionControl);
        rText = root.findViewById(R.id.rotationText);
        pText = root.findViewById(R.id.positionText);
        prefsDataManager.writeToPreferences("uiPosition", String.valueOf(0));
        prefsDataManager.writeToPreferences("uiRotation", String.valueOf(0));


        rControl.setOnClickListener(view -> {
            if (prefsDataManager.readFromPreferences("uiRotation").equals(String.valueOf(0))) {
                prefsDataManager.writeToPreferences("uiRotation", String.valueOf(1));
                rText.setText("On");
            } else {
                prefsDataManager.writeToPreferences("uiRotation", String.valueOf(0));
                rText.setText("Off");
            }
        });
        pControl.setOnClickListener(view -> {
            if (prefsDataManager.readFromPreferences("uiPosition").equals(String.valueOf(0))) {
            prefsDataManager.writeToPreferences("uiPosition", String.valueOf(1));
            pText.setText("On");
        } else {
            prefsDataManager.writeToPreferences("uiPosition", String.valueOf(0));
            pText.setText("Off");
        }
        });


        return root;

    }

    private void initFieldNames() {
        fieldNames = scoutingFormPresenter.getScoutingForm().getTeleFieldNames();
    }



    }




