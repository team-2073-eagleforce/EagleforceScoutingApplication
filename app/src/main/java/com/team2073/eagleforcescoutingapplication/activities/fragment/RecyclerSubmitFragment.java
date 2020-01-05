package com.team2073.eagleforcescoutingapplication.activities.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerSubmitFragment extends Fragment implements View.OnClickListener{

    private ScoutingFormPresenter scoutingFormPresenter;

    @BindView(R.id.rFormComments) EditText formComments;
    @BindView(R.id.rFormName) EditText formName;

    private static final String ARG_SECTION_NUMBER = "Submit";
    private PageViewModel pageViewModel;

    public static RecyclerSubmitFragment newInstance(int index){
        RecyclerSubmitFragment fragment = new RecyclerSubmitFragment();
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

        formComments = getActivity().findViewById(R.id.rFormComments);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_fragment_scouting_form_submit, container, false);
        ButterKnife.bind(this, root);

        if(!scoutingFormPresenter.readData("name").equals("0")) {
            formName.setText(scoutingFormPresenter.readData("name"));
        }

        root.findViewById(R.id.rFormSubmitButton).setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rFormSubmitButton: {
                scoutingFormPresenter.saveData("name", formName.getText().toString());
                scoutingFormPresenter.saveData("comments", formComments.getText().toString());
                scoutingFormPresenter.createCSV();
                scoutingFormPresenter.advanceOnSubmit();
                BluetoothSend bluetoothSend = new BluetoothSend(scoutingFormPresenter);
                bluetoothSend.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
            }
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
