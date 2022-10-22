package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.view.BaseView;
import rx.subscriptions.CompositeSubscription;

public class BasePresenter<V extends BaseView> implements Presenter<V> {

    private V view;
    protected CompositeSubscription subscription;

    public void bindView(V view) {
        this.view = view;
    }

    public void unbindView() {
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

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
