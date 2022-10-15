package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.QrGeneratorActivity;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class UISubmitFragment extends Fragment implements View.OnClickListener {

    private ScoutingFormPresenter scoutingFormPresenter;
    private Activity mActivity;

    @BindView(R.id.uiComments) EditText formComments;

    private TextView defensePerformance;
    private ImageButton addDefense;
    private TextView defenseScore;
    private ImageButton subtractDefense;

    private TextView driverPerformance;
    private ImageButton addDriver;
    private TextView driverScore;
    private ImageButton subtractDriver;

    String state = Environment.getExternalStorageState();

    private static final String ARG_SECTION_NUMBER = "Submit";
    private PageViewModel pageViewModel;

    public static UISubmitFragment newInstance(int index){
        UISubmitFragment fragment = new UISubmitFragment();
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
        int index;
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);

        formComments = getActivity().findViewById(R.id.uiComments);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_submit, container, false);
        ButterKnife.bind(this, root);

        View autoPerform = root.findViewById(R.id.Defense_Performance);
        defensePerformance = autoPerform.findViewById(R.id.formField);
        addDefense = autoPerform.findViewById(R.id.formAdd);
        defenseScore = autoPerform.findViewById(R.id.formScore);
        subtractDefense = autoPerform.findViewById(R.id.formSubtract);

        View driverPerform = root.findViewById(R.id.Driver_Performance);
        driverPerformance = driverPerform.findViewById(R.id.formField);
        addDriver = driverPerform.findViewById(R.id.formAdd);
        driverScore = driverPerform.findViewById(R.id.formScore);
        subtractDriver = driverPerform.findViewById(R.id.formSubtract);

        root.findViewById(R.id.uiSubmitButton).setOnClickListener(this);
        root.findViewById(R.id.uiSubmitButton).setBackgroundColor(getResources().getColor(R.color.colorAccent));

        initializeViewLabels();
        initFields();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(defenseScore.getText().toString()) + 1;
                if (value >= 3) {
                    value = 3;
                }
                defenseScore.setText(String.valueOf(value));

                scoutingFormPresenter.saveData("Defense Performance", String.valueOf(value));

                Timber.d("shared Preferences: " + "Defense Performance" + ", " + scoutingFormPresenter.readData("Defense Performance"));
            }
        });
        subtractDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(defenseScore.getText().toString()) - 1;
                if (value <= 0) {
                    value = 0;
                }
                defenseScore.setText(String.valueOf(value));

                scoutingFormPresenter.saveData("Defense Performance", String.valueOf(value));

                Timber.d("shared Preferences: " + "Defense Performance" + ", " + scoutingFormPresenter.readData("Defense Performance"));
            }
        });

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(driverScore.getText().toString()) + 1;
                if (value >= 3) {
                    value = 3;
                }
                driverScore.setText(String.valueOf(value));

                scoutingFormPresenter.saveData("Driver Performance", String.valueOf(value));

                Timber.d("shared Preferences: " + "Driver Performance" + ", " + scoutingFormPresenter.readData("Driver Performance"));
            }
        });
        subtractDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(driverScore.getText().toString()) - 1;
                if (value <= 0) {
                    value = 0;
                }
                driverScore.setText(String.valueOf(value));

                scoutingFormPresenter.saveData("Driver Performance", String.valueOf(value));

                Timber.d("shared Preferences: " + "Driver Performance" + ", " + scoutingFormPresenter.readData("Driver Performance"));
            }
        });
    }

    private void initFields() {
        if (scoutingFormPresenter.readData("Defense Performance").equals("0")){
            defenseScore.setText("0");
        } else {
            defenseScore.setText(scoutingFormPresenter.readData("Defense Performance"));
        }

        if (scoutingFormPresenter.readData("Driver Performance").equals("0")){
            driverScore.setText("0");
        } else {
            driverScore.setText(scoutingFormPresenter.readData("Driver Performance"));
        }
    }

    private void initializeViewLabels() {
        driverPerformance.setText(getResources().getString(R.string.driver_performance));
        defensePerformance.setText(getResources().getString(R.string.defense_performance));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.uiSubmitButton:
                String comments = formComments.getText().toString().replace(',', ';');
                scoutingFormPresenter.saveData("comments", comments);
                if (scoutingFormPresenter.readData("comments").length() == 0 || scoutingFormPresenter.readData("name").length() == 0){
                    Toast.makeText(this.getActivity(), "Some Fields are Empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getActivity(), QrGeneratorActivity.class);
                    startActivity(i);
                    ((Activity) getActivity()).overridePendingTransition(0, 0);
//                UISubmitFragment.BluetoothSend bluetoothSend = new UISubmitFragment.BluetoothSend(scoutingFormPresenter);
//                bluetoothSend.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
                break;
        }
    }


//    private static class BluetoothSend extends AsyncTask<Void, Void, Void> {
//
//        private ScoutingFormPresenter scoutingFormPresenter;
//
//        public BluetoothSend(ScoutingFormPresenter scoutingFormPresenter){
//            this.scoutingFormPresenter = scoutingFormPresenter;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            scoutingFormPresenter.sendOverBluetooth();
//            return null;
//        }
//    }
}
