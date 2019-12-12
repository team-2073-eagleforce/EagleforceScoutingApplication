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
import com.team2073.eagleforcescoutingapplication.adapters.ScoutingFormRecyclerViewAdapter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;

import timber.log.Timber;

public class DetailFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Detail";
    private PageViewModel pageViewModel;
    private ScoutingFormPresenter scoutingFormPresenter;

    private ArrayList<String> fieldNames = new ArrayList<>();

    public static DetailFragment newInstance(int index) {
        DetailFragment fragment = new DetailFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        initFieldNames();
        initRecyclerView(root);

        return root;

    }

    private void initFieldNames() {
        fieldNames = scoutingFormPresenter.getScoutingForm().getDetailsFieldNames();
    }

    private void initRecyclerView(View root) {
        Timber.d("init recyclerView");
        RecyclerView recyclerView = root.findViewById(R.id.detailRecyclerView);
        ScoutingFormRecyclerViewAdapter adapter = new ScoutingFormRecyclerViewAdapter(fieldNames, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}
