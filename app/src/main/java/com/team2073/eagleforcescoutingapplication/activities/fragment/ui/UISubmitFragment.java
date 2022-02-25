package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class UISubmitFragment extends Fragment implements View.OnClickListener {

    private ScoutingFormPresenter scoutingFormPresenter;

    @BindView(R.id.uiComments) EditText formComments;

    private TextView autoPerformance;
    private ImageButton addAuto;
    private TextView autoScore;
    private ImageButton subtractAuto;

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

        View autoPerform = root.findViewById(R.id.Auto_Performance);
        autoPerformance = autoPerform.findViewById(R.id.formField);
        addAuto = autoPerform.findViewById(R.id.formAdd);
        autoScore = autoPerform.findViewById(R.id.formScore);
        subtractAuto = autoPerform.findViewById(R.id.formSubtract);

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

        addAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(autoScore.getText().toString()) + 1;
                if (value >= 5) {
                    value = 5;
                }
                autoScore.setText(String.valueOf(value));

                scoutingFormPresenter.saveData("Auto Performance", String.valueOf(value));

                Timber.d("shared Preferences: " + "Auto Performance" + ", " + scoutingFormPresenter.readData("Auto Performance"));
            }
        });
        subtractAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(autoScore.getText().toString()) - 1;
                if (value <= 0) {
                    value = 0;
                }
                autoScore.setText(String.valueOf(value));

                scoutingFormPresenter.saveData("Auto Performance", String.valueOf(value));

                Timber.d("shared Preferences: " + "Auto Performance" + ", " + scoutingFormPresenter.readData("Auto Performance"));
            }
        });

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(driverScore.getText().toString()) + 1;
                if (value >= 5) {
                    value = 5;
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
        scoutingFormPresenter.saveData("Auto Performance", "0");
        driverScore.setText("0");

        scoutingFormPresenter.saveData("Driver Performance", "0");
        autoScore.setText("0");
    }

    private void initializeViewLabels() {
        driverPerformance.setText(getResources().getString(R.string.driver_performance));
        autoPerformance.setText(getResources().getString(R.string.auto_performance));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.uiSubmitButton:
                scoutingFormPresenter.saveData("comments", formComments.getText().toString());
                scoutingFormPresenter.createCSV();
                scoutingFormPresenter.advanceOnSubmit();
                UISubmitFragment.BluetoothSend bluetoothSend = new UISubmitFragment.BluetoothSend(scoutingFormPresenter);
                bluetoothSend.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
        }
    }

    private static class BluetoothSend extends AsyncTask<Void, Void, Void> {

        private ScoutingFormPresenter scoutingFormPresenter;

        public BluetoothSend(ScoutingFormPresenter scoutingFormPresenter){
            this.scoutingFormPresenter = scoutingFormPresenter;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            scoutingFormPresenter.sendOverBluetooth();
            return null;
        }
    }
}
