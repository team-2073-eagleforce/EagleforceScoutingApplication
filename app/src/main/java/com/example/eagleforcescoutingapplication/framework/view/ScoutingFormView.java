package com.example.eagleforcescoutingapplication.framework.view;

public interface ScoutingFormView extends BaseView{
    void onSubmitSuccessful();
    void onSubmitFailed(String errorMessage);
}
