package com.team2073.eagleforcescoutingapplication.activities.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.ScoutingFormRecyclerViewAdapter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;

import timber.log.Timber;

public class TeleOpFragment extends Fragment {

    private static final String TAG = "TeleOpFragment";

    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private ArrayList<String> fieldNames = new ArrayList<>();

    public static TeleOpFragment newInstance(int index) {
        TeleOpFragment fragment = new TeleOpFragment();
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
        View root = inflater.inflate(R.layout.fragment_scouting_form_teleop, container, false);

        initFieldNames();
        initRecyclerView(root);

        return root;

    }

    private void initFieldNames() {
        fieldNames.add("Cargo Cargo");
        fieldNames.add("Cargo Cargo Failed");
        fieldNames.add("Cargo Hatch");
        fieldNames.add("Cargo Hatch Failed");
        fieldNames.add("Rocket Cargo");
        fieldNames.add("Rocket Cargo Failed");
        fieldNames.add("Rocket Hatch");
        fieldNames.add("Rocket Hatch Failed");
    }

    private void initRecyclerView(View root){
        Timber.d("init recyclerView");
        RecyclerView recyclerView = root.findViewById(R.id.teleRecyclerView);
        ScoutingFormRecyclerViewAdapter adapter = new ScoutingFormRecyclerViewAdapter(fieldNames, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}
