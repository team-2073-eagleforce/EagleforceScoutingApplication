package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.WriterException;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentQrcodeBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

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
            boolean nameFieldFilled = scoutingFormPresenter.readData("name").equals("") || scoutingFormPresenter.readData("name").equals("0");
            boolean matchFieldFilled = scoutingFormPresenter.readData("teamNumber").equals("") || scoutingFormPresenter.readData("teamNumber").equals("0");
            boolean teamFieldFilled = scoutingFormPresenter.readData("matchNumber").equals("") || scoutingFormPresenter.readData("matchNumber").equals("0");

            if (!nameFieldFilled && !matchFieldFilled && !teamFieldFilled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setTitle("Confirm Submit?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
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
                }).setNegativeButton("No", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(buttons -> {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.md_black_1000));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.md_black_1000));
                });
                dialog.show();
            } else {
                Toast.makeText(this.getActivity(), "Make sure Name, Team, and Match are Filled", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            } catch (Exception e) {
                Timber.d("setUserVisibleHint: QRCode ");
            }
        }
    }

}
