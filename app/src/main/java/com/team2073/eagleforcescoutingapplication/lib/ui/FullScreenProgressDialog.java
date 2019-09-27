package com.team2073.eagleforcescoutingapplication.lib.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.team2073.eagleforcescoutingapplication.R;

import timber.log.Timber;

public class FullScreenProgressDialog extends Dialog {

    public static FullScreenProgressDialog mProgressDialog;

    public FullScreenProgressDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, R.style.AppTheme);

        this.setCancelable(cancelable);
        this.setOnCancelListener(cancelListener);

        /* The next line will add the ProgressBar to the dialog. */
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addContentView(progressBar, layoutParams);

        Window window = this.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }
    }

    public static synchronized void show(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new FullScreenProgressDialog(context, cancelable, cancelListener);
            }
            mProgressDialog.show();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void show(Context context) {
        show(context, false, null);
    }

    public static synchronized void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            mProgressDialog = null;
        }
    }
}
