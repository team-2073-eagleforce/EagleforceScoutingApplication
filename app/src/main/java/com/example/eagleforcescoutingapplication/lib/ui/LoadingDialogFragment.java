package com.example.eagleforcescoutingapplication.lib.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.eagleforcescoutingapplication.R;

public class LoadingDialogFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = LoadingDialogFragment.class.getSimpleName();

    public static final String CANCELABLE_KEY = "cancelable_key";

    public static final String MESSAGE_KEY = "message_key";

    private String mMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            boolean cancelable = bundle.getBoolean(CANCELABLE_KEY, true);
            setCancelable(cancelable);
            mMessage = bundle.getString(MESSAGE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        inflateView(view);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color
                    .TRANSPARENT));
        }
        return dialog;
    }

    public static LoadingDialogFragment getInstance(boolean cancelable, final String message) {
        LoadingDialogFragment fragment = new LoadingDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(CANCELABLE_KEY, cancelable);
        bundle.putString(MESSAGE_KEY, message);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void inflateView(View view) {
        TextView messageTv = view.findViewById(R.id.message_fragment_loading);
        if (!TextUtils.isEmpty(mMessage)) {
            messageTv.setVisibility(View.VISIBLE);
            messageTv.setText(mMessage);
        } else {
            messageTv.setVisibility(View.GONE);
        }
    }
}
