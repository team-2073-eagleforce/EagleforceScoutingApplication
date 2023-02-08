package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.WriterException;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentQrcodeBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

public class UIQRCodeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "QRCode";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentQrcodeBinding fragmentQrcodeBinding;


    public static UIQRCodeFragment newInstance(int index) {
        UIQRCodeFragment fragment = new UIQRCodeFragment();
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
        fragmentQrcodeBinding = UiFragmentQrcodeBinding.inflate(inflater, container, false);
        return fragmentQrcodeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        generateQRCode();
        finishScan();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentQrcodeBinding = null;
    }

    public void generateQRCode() {
        try {
            fragmentQrcodeBinding.QROutput.setImageBitmap(scoutingFormPresenter.createQR());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void finishScan() {
        fragmentQrcodeBinding.FinishScan.setOnClickListener(finishScan -> {
            scoutingFormPresenter.advanceOnSubmit();
            Intent intent = getActivity().getIntent();
            getActivity().overridePendingTransition(0, 0);
            getActivity().finish();

            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }
}
