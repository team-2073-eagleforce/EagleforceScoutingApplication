package com.example.eagleforcescoutingapplication.framework.presenter;

import com.example.eagleforcescoutingapplication.framework.view.BaseView;

public interface Presenter<V extends BaseView> {
    void bindView(V baseView);

    void unbindView();

    void onStop();

    void onResume();
}
