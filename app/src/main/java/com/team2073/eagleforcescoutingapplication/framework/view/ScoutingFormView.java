package com.team2073.eagleforcescoutingapplication.framework.view;

public interface ScoutingFormView extends BaseView{
    void onSubmitSuccessful();
    void onSubmitFailed(String errorMessage);
}
