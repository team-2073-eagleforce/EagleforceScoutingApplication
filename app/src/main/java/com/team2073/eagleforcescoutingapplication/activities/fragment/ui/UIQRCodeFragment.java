package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.WriterException;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentQrcodeBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;

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
        generateQRCode(new MessageEvent());
        finishScan();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentQrcodeBinding = null;
    }

    @Subscribe
    public void generateQRCode(MessageEvent event) {
        try {
            fragmentQrcodeBinding.QROutput.setImageBitmap(scoutingFormPresenter.createQR());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void finishScan() {
        fragmentQrcodeBinding.FinishScan.setOnClickListener(finishScan -> {
            try {
                scoutingFormPresenter.saveQR(scoutingFormPresenter.createQR());
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
            scoutingFormPresenter.advanceOnSubmit();
            Intent intent = getActivity().getIntent();
            getActivity().overridePendingTransition(0, 0);
            getActivity().finish();

            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            } catch (Exception e) {
                Timber.d( "setUserVisibleHint: QRCode ");
            }
        }
    }

}
