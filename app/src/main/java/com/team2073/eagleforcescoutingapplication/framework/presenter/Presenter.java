package com.team2073.eagleforcescoutingapplication.framework.presenter;

import com.team2073.eagleforcescoutingapplication.framework.view.BaseView;

public interface Presenter<V extends BaseView> {
    void bindView(V baseView);

    void unbindView();

    void onStop();

    void onResume();
}
