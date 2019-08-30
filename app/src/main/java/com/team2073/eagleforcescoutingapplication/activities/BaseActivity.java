package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.team2073.eagleforcescoutingapplication.EagleforceScoutingApplication;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.view.BaseView;
import com.team2073.eagleforcescoutingapplication.lib.service.NetworkConnectionReceiver;
import com.team2073.eagleforcescoutingapplication.lib.ui.FullScreenProgressDialog;
import com.team2073.eagleforcescoutingapplication.lib.ui.LoadingDialogFragment;
import com.team2073.eagleforcescoutingapplication.util.NetworkUtil;

public abstract class BaseActivity extends AppCompatActivity implements BaseView, NetworkConnectionReceiver.ConnectionReceiverListener {

    private final Object mutex = new Object();

    private LoadingDialogFragment mLoadingDialog;

    private View bannerNetwork;
    private NetworkConnectionReceiver mNetworkConnectionReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutResourceId() != 0) {
            setContentView(getLayoutResourceId());
        }
        mNetworkConnectionReceiver = new NetworkConnectionReceiver();
        initView();
        initEvent();
        bindView();
    }

    @Override
    public void setContentView(int resId) {
        LinearLayout screenRootView = new LinearLayout(this);
        screenRootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        screenRootView.setOrientation(LinearLayout.VERTICAL);
        screenRootView.setBackgroundResource(R.color.colorPrimary);
        bannerNetwork = LayoutInflater.from(this).inflate(R.layout.banner_network_status, null, false);
        (bannerNetwork.findViewById(R.id.tvNetWorkClose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bannerNetwork.setVisibility(View.GONE);
            }
        });
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View screenView = inflater.inflate(resId, null);
        screenRootView.addView(bannerNetwork);
        screenRootView.addView(screenView);

        super.setContentView(screenRootView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkConnectionReceiver);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            bannerNetwork.setVisibility(View.VISIBLE);
        } else {
            bannerNetwork.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveOutStateBundle(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreSavedState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkConnectionReceiver, intentFilter);
        EagleforceScoutingApplication.getInstance().setConnectionListener(this);
        if (NetworkUtil.isNetworkConntected(this)) {
            bannerNetwork.setVisibility(View.GONE);
        } else {
            bannerNetwork.setVisibility(View.VISIBLE);
        }
    }

    protected void saveOutStateBundle(Bundle outState) {

    }

    protected void restoreSavedState(Bundle savedInstanceState) {

    }

    /**
     * Default loading dialog with no message and can not cancel dialog when touch out side
     */
    public void showLoading() {
        synchronized (mutex) {
            if (!isFinishing()) {
                if (mLoadingDialog == null) {
                    mLoadingDialog = LoadingDialogFragment.getInstance(false, null);
                    getFragmentManager().beginTransaction()
                            .add(mLoadingDialog, LoadingDialogFragment.FRAGMENT_TAG)
                            .commitAllowingStateLoss();
                }
            }
        }
    }

    public void dismissLoading() {
        synchronized (mutex) {
            if (!isFinishing()) {
                if (mLoadingDialog != null) {
                    getFragmentManager().beginTransaction()
                            .remove(mLoadingDialog)
                            .commitAllowingStateLoss();
                    mLoadingDialog = null;
                }
            }
        }
    }

    public AlertDialog displayAlertDialog(Context context, String title, String message,
                                          String positiveButton, AlertDialog.OnClickListener positiveOnClickListener,
                                          DialogInterface.OnCancelListener onCancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(positiveButton, positiveOnClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnCancelListener(onCancelListener);
        return alertDialog;
    }


    /**
     * Layout resource id for activity.
     *
     * @return Resource id
     */
    @LayoutRes
    protected abstract int getLayoutResourceId();

    /**
     * Initial event for views.
     */
    protected abstract void initEvent();

    /**
     * Initial all child views to display.
     */
    protected abstract void initView();

    /**
     * Inject view with presenter.
     */
    protected abstract void bindView();

    @Override
    public void updateProgressDialog(boolean isShowProgressDialog) {
        if (isShowProgressDialog) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
    }

    protected void showProgressDialog() {
        FullScreenProgressDialog.show(this);
    }

    protected void hideProgressDialog() {
        FullScreenProgressDialog.hideProgressDialog();
    }

    @Override
    public void showErrorMessageDialog(String errorTitle, String errorMessage, Boolean isBackLogin) {
    }
}
