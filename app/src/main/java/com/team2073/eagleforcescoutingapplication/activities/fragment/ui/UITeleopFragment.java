package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesCoralBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesNetBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesProcessorBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesRemovedBinding;
import com.team2073.eagleforcescoutingapplication.databinding.ReefLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.TransportDisplayLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentEndgameBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentTeleopBinding;
import com.team2073.eagleforcescoutingapplication.framework.form.ChargedUpScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.CrescendoScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;


import timber.log.Timber;


public class UITeleopFragment extends Fragment {


    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private Context context;
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentTeleopBinding fragmentTeleopBinding;
    private AddSubtractValuesProcessorBinding process;
    private AddSubtractValuesNetBinding net;
    private AddSubtractValuesRemovedBinding remove;
    //    private AddSubtractValuesAmpBinding teleopAmpBinding;
//    private AddSubtractValuesSpeakerMakeBinding teleopSpeakerMakeBinding;
//    private AddSubtractValuesSpeakerMissBinding teleopSpeakerMissBinding;
//    private AddSubtractValuesPassBinding passBinding;
    private ReefLayoutBinding reef;

    public static UITeleopFragment newInstance(int index) {
        UITeleopFragment fragment = new UITeleopFragment();
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
        fragmentTeleopBinding = UiFragmentTeleopBinding.inflate(inflater, container, false);
        reef = fragmentTeleopBinding.teleopReef;
        process = fragmentTeleopBinding.teleopProcessor;
        net = fragmentTeleopBinding.teleopNet;
        remove = fragmentTeleopBinding.teleopRemoved;


        return fragmentTeleopBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //initDataFields();
        initTextFields();
        initViewImageButtons();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentTeleopBinding = null;
    }


    private void initTextFields() {
        reef.L4.formScore.setText(readData("teleL4"));
        reef.L1.formScore.setText(readData("teleL1"));
        reef.L2.formScore.setText(readData("teleL2"));
        reef.L3.formScore.setText(readData("teleL3"));
        process.formScore.setText(readData("teleProcessor"));
        net.formScore.setText(readData("teleNet"));
        remove.formScore.setText(readData("teleRemoved"));
    }


    private void initViewImageButtons() {
        reef.L1.formAdd.setOnClickListener(teleopCoralAdd -> addTransportValue(reef.L1.formScore,"teleL1"));
        reef.L2.formAdd.setOnClickListener(teleopCoralAdd -> addTransportValue(reef.L2.formScore,"teleL2"));
        reef.L3.formAdd.setOnClickListener(teleopCoralAdd -> addTransportValue(reef.L3.formScore,"teleL3"));
        reef.L4.formAdd.setOnClickListener(teleopCoralAdd -> addTransportValue(reef.L4.formScore,"teleL4"));
        reef.L1.formSubtract.setOnClickListener(teleopCoralAdd -> subtractTransportValue(reef.L1.formScore,"teleL1"));
        reef.L2.formSubtract.setOnClickListener(teleopCoralAdd -> subtractTransportValue(reef.L2.formScore,"teleL2"));
        reef.L3.formSubtract.setOnClickListener(teleopCoralAdd -> subtractTransportValue(reef.L3.formScore,"teleL3"));
        reef.L4.formSubtract.setOnClickListener(teleopCoralAdd -> subtractTransportValue(reef.L4.formScore,"teleL4"));
        process.formAdd.setOnClickListener(teleopProcessorAdd -> addTransportValue(process.formScore,"teleProcessor"));
        net.formAdd.setOnClickListener(teleopNetAdd -> addTransportValue(net.formScore,"teleNet"));
        remove.formAdd.setOnClickListener(teleopRemoveAdd -> addTransportValue(remove.formScore,"teleRemoved"));
    }


    private void addTransportValue(TextView formScore, String transportType) {
        int value = Integer.parseInt(readData(transportType)) + 1;
        if (value >= 100) {
            value = 99;
        }
        saveData(transportType, String.valueOf(value));
        formScore.setText(String.valueOf(value));
        Timber.d("%s:%s", transportType, scoutingFormPresenter.readData(transportType));
    }


    private void subtractTransportValue(TextView formScore, String transportType) {
        int value = Integer.parseInt(readData(transportType)) - 1;
        if (value < 0) {
            value = 0;
        }
        saveData(transportType, String.valueOf(value));
        formScore.setText(String.valueOf(value));
        Timber.d("%s:%s", transportType, scoutingFormPresenter.readData(transportType));
    }


    public String readData(String key) {
        return scoutingFormPresenter.readData(key);
    }


    public void saveData(String key, String data) {
        scoutingFormPresenter.saveData(key, data);
    }


}
