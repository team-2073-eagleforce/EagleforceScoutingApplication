package com.example.eagleforcescoutingapplication.framework.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.eagleforcescoutingapplication.R;
import com.example.eagleforcescoutingapplication.framework.view.BaseView;
import rx.subscriptions.CompositeSubscription;

public class BasePresenter<V extends BaseView> implements Presenter<V> {

    private V view;
    protected CompositeSubscription subscription;

    public void bindView(V view) {
        this.view = view;
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    public void unbindView() {
        if (isViewAttached()) {
            getView().updateProgressDialog(false);
        }
        this.view = null;
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    protected V getView() {
        return view;
    }

    public boolean isViewAttached() {
        return view != null;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void showNetworkError(Context mContext) {
        if (isViewAttached()) {
            getView().updateProgressDialog(false);
            getView().showErrorMessageDialog(mContext.getResources().getString(R.string.notice), mContext.getResources().getString(R.string.no_internet), false);
        }
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
